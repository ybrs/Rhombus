package com.pardot.rhombus.cobject;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;
import com.pardot.rhombus.Criteria;
import com.pardot.rhombus.cobject.shardingstrategy.ShardStrategyException;
import com.pardot.rhombus.cobject.shardingstrategy.ShardingStrategyNone;
import org.apache.commons.codec.digest.DigestUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.*;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/8/13
 */
public class CObjectCQLGenerator {

	private static Logger logger = LoggerFactory.getLogger(CObjectCQLGenerator.class);

	protected static final String TEMPLATE_CREATE_STATIC = "CREATE TABLE \"%s\" (id timeuuid PRIMARY KEY, %s);";
	protected static final String TEMPLATE_CREATE_WIDE = "CREATE TABLE \"%s\" (id timeuuid, shardid bigint, %s, PRIMARY KEY ((shardid, %s),id) );";
	protected static final String TEMPLATE_CREATE_WIDE_INDEX = "CREATE TABLE \"%s\" (shardid bigint, tablename varchar, indexvalues varchar, targetrowkey varchar, PRIMARY KEY ((tablename, indexvalues),shardid) );";
	protected static final String TEMPLATE_CREATE_INDEX_UPDATES = "CREATE TABLE \"__index_updates\" (id timeuuid, statictablename varchar, instanceid timeuuid, indexvalues varchar, PRIMARY KEY ((statictablename,instanceid),id))";
	protected static final String TEMPLATE_DROP = "DROP TABLE \"%s\";";
	protected static final String TEMPLATE_INSERT_STATIC = "INSERT INTO \"%s\" (%s) VALUES (%s)%s;";//"USING TIMESTAMP %s%s;";//Add back when timestamps become preparable
	protected static final String TEMPLATE_INSERT_WIDE = "INSERT INTO \"%s\" (%s) VALUES (%s)%s;";//"USING TIMESTAMP %s%s;";//Add back when timestamps become preparable
	protected static final String TEMPLATE_INSERT_WIDE_INDEX = "INSERT INTO \"%s\" (tablename, indexvalues, shardid, targetrowkey) VALUES (?, ?, ?, ?);";//"USING TIMESTAMP %s;";//Add back when timestamps become preparable
	protected static final String TEMPLATE_INSERT_INDEX_UPDATES = "INSERT INTO \"__index_updates\" (id, statictablename, instanceid, indexvalues) values (?, ?, ?, ?);";
	protected static final String TEMPLATE_SELECT_STATIC = "SELECT * FROM \"%s\" WHERE %s;";
	protected static final String TEMPLATE_SELECT_WIDE = "SELECT * FROM \"%s\" WHERE shardid = %s AND %s ORDER BY id %s %s ALLOW FILTERING;";
	protected static final String TEMPLATE_SELECT_WIDE_INDEX = "SELECT shardid FROM \"%s\" WHERE tablename = ? AND indexvalues = ?%s ORDER BY shardid %s ALLOW FILTERING;";
	protected static final String TEMPLATE_DELETE = "DELETE FROM %s WHERE %s;";//"DELETE FROM %s USING TIMESTAMP %s WHERE %s;"; //Add back when timestamps become preparable
	protected Map<String, CDefinition> definitions;
	protected CObjectShardList shardList;

	/**
	 * No Param constructor, mostly for testing convenience. Use the other constructor.
	 */
	public CObjectCQLGenerator(){
		this.definitions = Maps.newHashMap();
	}


	/**
	 *
	 * @param objectDefinitions - A map where the key is the CDefinition.name and the value is the CDefinition.
	 *                          This map should include a CDefinition for every object in the system.
	 */
	public CObjectCQLGenerator(Map<String, CDefinition> objectDefinitions, CObjectShardList shardList){
		this.definitions = objectDefinitions;
		setShardList(shardList);
	}

	/**
	 * Set the Definitions to be used
	 * @param objectDefinitions - A map where the key is the CDefinition.name and the value is the CDefinition.
	 *                          This map should include a CDefinition for every object in the system.
	 */
	public void setDefinitions(Map<String, CDefinition> objectDefinitions){
		this.definitions = objectDefinitions;
	}

	/**
	 *
	 * @param objType - The name of the Object type aka CDefinition.name
	 * @return Iterator of CQL statements that need to be executed for this task.
	 */
	public CQLStatementIterator makeCQLforCreate(String objType){
		return makeCQLforCreate(this.definitions.get(objType));
	}

	/**
	 *
	 * @param objType - The name of the Object type aka CDefinition.name
	 * @return Iterator of CQL statements that need to be executed for this task.
	 */
	public CQLStatementIterator makeCQLforDrop(String objType){
		return makeCQLforDrop(this.definitions.get(objType));
	}

	/**
	 *
	 * @param objType - The name of the Object type aka CDefinition.name
	 * @param data - A map of fieldnames to values representing the data to insert
	 * @return Iterator of CQL statements that need to be executed for this task.
	 * @throws CQLGenerationException
	 */
	@NotNull
	public CQLStatementIterator makeCQLforInsert(String objType, Map<String,Object> data) throws CQLGenerationException {
		return makeCQLforInsert(this.definitions.get(objType), data);
	}

	/**
	 *
	 * @param objType - The name of the Object type aka CDefinition.name
	 * @param data - A map of fieldnames to values representing the data to insert
	 * @return Iterator of CQL statements that need to be executed for this task.
	 * @throws CQLGenerationException
	 */
	@NotNull
	public CQLStatementIterator makeCQLforInsert(String objType, Map<String,Object> data, UUID key, Long timestamp) throws CQLGenerationException {
		return makeCQLforInsert(this.definitions.get(objType), data, key, timestamp, 0);
	}

	/**
	 *
	 * @param objType - The name of the Object type aka CDefinition.name
	 * @param key - The TimeUUID of the object to retrieve
	 * @return Iterator of CQL statements that need to be executed for this task. (Should have a length of 1 for this particular method)
	 */
	@NotNull
	public CQLStatementIterator makeCQLforGet(String objType, UUID key){
		return makeCQLforGet(this.definitions.get(objType), key);
	}

	/**
	 *
	 * @param objType - The name of the Object type aka CDefinition.name
	 * @param criteria - The criteria object describing which rows to retrieve
	 * @return Iterator of CQL statements that need to be executed for this task.
	 */
	@NotNull
	public CQLStatementIterator makeCQLforGet(String objType, Criteria criteria) throws CQLGenerationException {
		CDefinition definition = this.definitions.get(objType);
		CObjectOrdering ordering = (criteria.getOrdering() != null ? criteria.getOrdering(): CObjectOrdering.DESCENDING);
		UUID endUuid = (criteria.getEndUuid() == null ? UUIDs.startOf(DateTime.now().getMillis()) : criteria.getEndUuid());
		return makeCQLforGet(shardList, definition, criteria.getIndexKeys(), ordering,  criteria.getStartUuid(),
				 endUuid, criteria.getLimit(), criteria.getInclusive());
	}

	/**
	 *
	 * @param objType - The name of the Object type aka CDefinition.name
	 * @param indexkeys - A map of fieldnames to values representing the where clause of the index query
	 * @param ordering - CObjectOrdering.ASCENDING or CObjectOrdering.DESCENDING
	 * @param start - UUID of the item before the first result
	 * @param end - UUID of the item after the first result (Assuming the limit doesnt override it)
	 * @param limit - The maximum number of results
	 * @return Iterator of CQL statements that need to be executed for this task.
	 * @throws CQLGenerationException
	 */
	@NotNull
	public CQLStatementIterator makeCQLforGet(String objType, SortedMap<String,Object> indexkeys,CObjectOrdering ordering,@Nullable UUID start, @Nullable UUID end, long limit) throws CQLGenerationException {
		return makeCQLforGet(this.shardList, this.definitions.get(objType), indexkeys,ordering,start,end,limit, false);
	}

	/**
	 *
	 * @param objType - The name of the Object type aka CDefinition.name
	 * @param indexkeys - A map of fieldnames to values representing the where clause of the index query
	 * @param limit - The maximum number of results
	 * @return Iterator of CQL statements that need to be executed for this task.
	 * @throws CQLGenerationException
	 */
	public CQLStatementIterator makeCQLforGet(String objType, SortedMap<String,Object> indexkeys, Long limit) throws CQLGenerationException {
		return makeCQLforGet(this.shardList, this.definitions.get(objType), indexkeys,limit);
	}

	/**
	 *
	 * @param objType - The name of the Object type aka CDefinition.name
	 * @param indexkeys - A map of fieldnames to values representing the where clause of the index query
	 * @param ordering - CObjectOrdering.ASCENDING or CObjectOrdering.DESCENDING
	 * @param starttimestamp - Return results equal to or after this timestamp
	 * @param endtimestamp - Return results equal to or before this timestamp
	 * @param limit - The maximum number of results
	 * @return Iterator of CQL statements that need to be executed for this task.
	 * @throws CQLGenerationException
	 */
	public CQLStatementIterator makeCQLforGet(String objType, SortedMap<String,Object> indexkeys, CObjectOrdering ordering, Long starttimestamp, Long endtimestamp, Long limit) throws CQLGenerationException {
		return makeCQLforGet(this.shardList, this.definitions.get(objType), indexkeys,ordering, starttimestamp, endtimestamp, limit);
	}

	/**
	 *
	 * @param objType - The name of the Object type aka CDefinition.name
	 * @param key - The TimeUUID of the object to delete
	 * @param data - All the values of the fields existing in this object (or just the required fields will work)
	 * @param timestamp - The timestamp for the request
	 * @return Iterator of CQL statements that need to be executed for this task.
	 */
	@NotNull
	public CQLStatementIterator makeCQLforDelete(String objType, UUID key,  Map<String,Object> data, Long timestamp){
		return makeCQLforDelete(this.definitions.get(objType), key, data, timestamp);
	}

	/**
	 *
	 * @return String of single CQL statement required to create the Shard Index Table
	 */
	public static CQLStatement makeCQLforShardIndexTableCreate(){
		return CQLStatement.make(String.format(TEMPLATE_CREATE_WIDE_INDEX,CObjectShardList.SHARD_INDEX_TABLE_NAME));
	}

	/**
	 *
	 * @return String of single CQL statement required to create the Shard Index Table
	 */
	public static CQLStatement makeCQLforShardIndexTableDrop(){
		return CQLStatement.make(String.format(TEMPLATE_DROP, CObjectShardList.SHARD_INDEX_TABLE_NAME));
	}


	/**
	 *
	 * @param def - CIndex for the index for which to pull the shard list
	 * @param indexValues - Values identifing the specific index for which to pull the shard list
	 * @param ordering - ASC or DESC
	 * @param start - Start UUID for bounding
	 * @param end - End UUID for bounding
	 * @return Single CQL statement needed to retrieve the list of shardids
	 */
	public static CQLStatement makeCQLforGetShardIndexList(CDefinition def, SortedMap<String,Object> indexValues, CObjectOrdering ordering,@Nullable UUID start, @Nullable UUID end) throws CQLGenerationException {
		CIndex i = def.getIndex(indexValues);
		String indexValueString = makeIndexValuesString(indexValues.values());
		List values = Lists.newArrayList();
		values.add(makeTableName(def,i));
		values.add(indexValueString);

		String whereCQL = "";
		if(start != null){
			whereCQL += " AND shardid >= ?";
			values.add(Long.valueOf(i.getShardingStrategy().getShardKey(start)));
		}
		if(end != null){
			whereCQL += " AND shardid <= ?";
			values.add(Long.valueOf(i.getShardingStrategy().getShardKey(end)));
		}
		String query =  String.format(
			TEMPLATE_SELECT_WIDE_INDEX,
			CObjectShardList.SHARD_INDEX_TABLE_NAME,
			whereCQL,
			ordering
		);
		logger.debug("Making statement with query: {} and values: {}", query, values);
		return CQLStatement.make(query, values.toArray());
	}

	public static CQLStatement makeCQLforIndexUpdateTableCreate(){
		return CQLStatement.make(TEMPLATE_CREATE_INDEX_UPDATES);
	}

	public static CQLStatementIterator makeCQLforUpdate(CDefinition def, UUID key, Map<String,Object> oldValues, Map<String, Object> newValues) throws CQLGenerationException {
		List<CQLStatement> ret = Lists.newArrayList();
		//(1) Detect if there are any changed index values in values
		List<CIndex> effectedIndexes =  getEffectedIndexes(def,oldValues,newValues);
		List<CIndex> uneffectedIndexes =  getUneffectedIndexes(def,oldValues,newValues);

		//(2) Delete from any indexes that are no longer applicable
		for(CIndex i : effectedIndexes){
			ret.add(makeCQLforDeleteUUIDFromIndex(def, i, key, i.getIndexKeyAndValues(oldValues), null));
		}

		//(3) Construct a complete copy of the object
		Map<String,Object> completeValues = Maps.newHashMap(oldValues);
		for(String k : newValues.keySet()){
			completeValues.put(k, newValues.get(k));
		}
		Map<String,ArrayList> fieldsAndValues = makeFieldAndValueList(def,completeValues);

		//(4) Insert into the new indexes like a new insert
		for(CIndex i: effectedIndexes){
			addCQLStatmentsForIndexInsert(true, ret, def, completeValues, i, key, fieldsAndValues,null, null);
		}

		//(4) Insert into the existing indexes without the shard index addition and only update new values
		Map<String,ArrayList> fieldsAndValuesOnlyForChanges = makeFieldAndValueList(def,newValues);
		for(CIndex i: uneffectedIndexes){
			addCQLStatmentsForIndexInsert(false, ret, def, newValues, i, key, fieldsAndValuesOnlyForChanges,null, null);
		}

		//(5) Update the static table (be sure to only update and not insert the completevalues just in case they are wrong, the background job will fix them later)
		ret.add(makeInsertStatementStatic(
				makeTableName(def,null),
				(List<String>)fieldsAndValuesOnlyForChanges.get("fields").clone(),
				(List<Object>)fieldsAndValuesOnlyForChanges.get("values").clone(),
				key,
				null,
				null
		));

		//(6) Insert a snapshot of the updated values for this id into the __index_updates
		ret.add(makeInsertUpdateIndexStatement(def, key, def.makeIndexValues(completeValues)));

		return new BoundedCQLStatementIterator(ret);
	}

	public static List<CIndex> getEffectedIndexes(CDefinition def, Map<String,Object> oldValues, Map<String,Object> newValues){
		List<CIndex> ret = Lists.newArrayList();
		if(def.getIndexes() == null) {
			return ret;
		}
		for(CIndex i : def.getIndexes().values()){
			if(i.areValuesAssociatedWithIndex(newValues)){
				//This change does indeed effect this index
				ret.add(i);
			}
		}
		return ret;
	}

	public static List<CIndex> getUneffectedIndexes(CDefinition def, Map<String,Object> oldValues, Map<String,Object> newValues){
		List<CIndex> ret = Lists.newArrayList();
		if(def.getIndexes() == null) {
			return ret;
		}
		for(CIndex i : def.getIndexes().values()){
			if(!i.areValuesAssociatedWithIndex(newValues)){
				//This change does not effect this index
				ret.add(i);
			}
		}
		return ret;
	}

	protected static CQLStatementIterator makeCQLforCreate(CDefinition def){
		List<CQLStatement> ret = Lists.newArrayList();
		ret.add(makeStaticTableCreate(def));
		if(def.getIndexes() != null) {
			for(CIndex i : def.getIndexes().values()){
				ret.add(makeWideTableCreate(def, i));
			}
		}
		return new BoundedCQLStatementIterator(ret);
	}


	protected static CQLStatementIterator makeCQLforDrop(CDefinition def){
		List<CQLStatement> ret = Lists.newArrayList();
		ret.add(makeTableDrop(def.getName()));
		if(def.getIndexes() != null) {
			for(CIndex i : def.getIndexes().values()){
				ret.add(makeTableDrop(makeTableName(def, i)));
			}
		}
		return new BoundedCQLStatementIterator(ret);
	}


	protected static CQLStatement makeInsertStatementStatic(String tableName, List<String> fields, List values, UUID uuid, Long timestamp, Integer ttl){
		fields.add(0,"id");
		values.add(0, uuid);
		String query = String.format(
				TEMPLATE_INSERT_STATIC,
				tableName,
				makeCommaList(fields),
				makeCommaList(values, true),
				//timestamp.toString(), //add timestamp back when timestamps become preparable
				(ttl == null) ? "" : (" USING TTL "+ttl)//(" AND TTL "+ttl) //Revert this back to AND when timestamps are preparable
		);

		return CQLStatement.make(query, values.toArray());
	}

	protected static CQLStatement makeInsertUpdateIndexStatement(CDefinition def, UUID instanceId, Map<String,Object> indexvalues) throws CQLGenerationException {
		UUID id = UUIDs.timeBased();
		String tableName = makeTableName(def,null);
		String indexValuesAsJson;
		try{
			org.codehaus.jackson.map.ObjectMapper om = new org.codehaus.jackson.map.ObjectMapper();
			indexValuesAsJson = om.writeValueAsString(indexvalues);
		}
		catch (Exception e){
			throw new CQLGenerationException(e.getMessage());
		}
		return CQLStatement.make(TEMPLATE_INSERT_INDEX_UPDATES, Arrays.asList(id, tableName, instanceId, indexValuesAsJson).toArray() );
	}

	protected static CQLStatement makeInsertStatementWide(String tableName, List<String> fields, List<Object> values, UUID uuid, long shardid, Long timestamp, Integer ttl){
		fields.add(0,"shardid");
		values.add(0,Long.valueOf(shardid));
		fields.add(0,"id");
		values.add(0,uuid);

		String query = String.format(
			TEMPLATE_INSERT_WIDE,
			tableName,
			makeCommaList(fields),
			makeCommaList(values,true),
			//timestamp.toString(), //add timestamp back when timestamps become preparable
			(ttl == null) ? "" : (" USING TTL "+ttl)//(" AND TTL "+ttl) //Revert this back to AND when timestamps are preparable
		);

		return CQLStatement.make(query, values.toArray());
	}

	protected static CQLStatement makeInsertStatementWideIndex(String tableName, String targetTableName, long shardId, List indexValues, Long timestamp) throws CQLGenerationException {
		String indexValuesString = makeIndexValuesString(indexValues);
		Object[] values = {targetTableName, indexValuesString, Long.valueOf(shardId), shardId+":"+indexValuesString};
		return CQLStatement.make(String.format(
				TEMPLATE_INSERT_WIDE_INDEX,
				tableName
				//timestamp.toString() //Add back timestamp when timestamps become preparable
			),values);
	}

	protected static CQLStatementIterator makeCQLforInsert(@NotNull CDefinition def, @NotNull Map<String,Object> data) throws CQLGenerationException{
		return makeCQLforInsert(def, data, null, null, 0);
	}

	protected static CQLStatementIterator makeCQLforInsert(@NotNull CDefinition def, @NotNull Map<String,Object> data, @Nullable UUID uuid, Long timestamp, Integer ttl) throws CQLGenerationException{
		List<CQLStatement> ret = Lists.newArrayList();
		if(uuid == null){
			uuid = UUIDs.timeBased();
		}
		if(timestamp == 0){
			timestamp = System.currentTimeMillis();
		}
		if(!validateData(def, data)){
			throw new CQLGenerationException("Invalid Insert Requested. Missing Field(s)");
		}
		Map<String,ArrayList> fieldsAndValues = makeFieldAndValueList(def,data);
		//Static Table
		ret.add(makeInsertStatementStatic(
				makeTableName(def,null),
				(List<String>)fieldsAndValues.get("fields").clone(),
				(List<Object>)fieldsAndValues.get("values").clone(),
				uuid,
				timestamp,
				ttl
		));
		//Index Tables
		if(def.getIndexes() != null) {
			for(CIndex i : def.getIndexes().values()){
				if(def.isAllowNullPrimaryKeyInserts()){
					//check if we have the necessary primary fields to insert on this index. If not just continue;
					if(!i.validateIndexKeys(i.getIndexKeyAndValues(data))){
						continue;
					}
				}
				//insert it into the index
				addCQLStatmentsForIndexInsert(true, ret, def,data,i,uuid,fieldsAndValues,timestamp,ttl);
			}
		}
		return new BoundedCQLStatementIterator(ret);
	}

	protected static void addCQLStatmentsForIndexInsert(boolean includeShardInsert, List<CQLStatement> statementListToAddTo, CDefinition def, @NotNull Map<String,Object> data, CIndex i, UUID uuid, Map<String,ArrayList> fieldsAndValues,Long timestamp, Integer ttl) throws CQLGenerationException {
		//insert it into the index
		long shardId = i.getShardingStrategy().getShardKey(uuid);
		statementListToAddTo.add(makeInsertStatementWide(
				makeTableName(def,i),
				(List<String>)fieldsAndValues.get("fields").clone(),
				(List<Object>)fieldsAndValues.get("values").clone(),
				uuid,
				shardId,
				timestamp,
				ttl
		));
		if( includeShardInsert && (!(i.getShardingStrategy() instanceof ShardingStrategyNone))){
			//record that we have made an insert into that shard
			statementListToAddTo.add(makeInsertStatementWideIndex(
					CObjectShardList.SHARD_INDEX_TABLE_NAME,
					makeTableName(def,i),
					shardId,
					i.getIndexValues(data),
					timestamp
			));
		}
	}

	protected static CQLStatementIterator makeCQLforGet(CDefinition def, UUID key){
		Object[] values = {key};
		CQLStatement statement = CQLStatement.make(String.format(TEMPLATE_SELECT_STATIC,def.getName(),"id = ?"), values);
		return new BoundedCQLStatementIterator(Lists.newArrayList(statement));
	}

	@NotNull
	protected static CQLStatementIterator makeCQLforGet(CObjectShardList shardList, CDefinition def, SortedMap<String,Object> indexValues, CObjectOrdering ordering,@Nullable UUID start, @Nullable UUID end, Long limit, boolean inclusive) throws CQLGenerationException {

		CIndex i = def.getIndex(indexValues);
		if(i == null){
			throw new CQLGenerationException(String.format("Could not find specified index on CDefinition %s",def.getName()));
		}
		if(!i.validateIndexKeys(indexValues)){
			throw new CQLGenerationException(String.format("Cannot query index %s on CDefinition %s with the provided list of index values",i.getName(),def.getName()));
		}
		CQLStatement whereCQL = makeAndedEqualList(def,indexValues);
		String whereQuery = whereCQL.getQuery();
		List values = new ArrayList(Arrays.asList(whereCQL.getValues()));
		if(start != null){
			whereQuery +=  " AND id >"+(inclusive ? "= "  :  " ")+ "?";
			values.add(start);
		}
		if(end != null){
			whereQuery += " AND id <"+(inclusive ? "= "  :  " ") + "?";
			values.add(end);
		}
		String limitCQL = "";
		if(limit.longValue() > 0){
			limitCQL = "LIMIT %d";
		}
		String CQLTemplate = String.format(
			TEMPLATE_SELECT_WIDE,
			makeTableName(def,i),
			"?",
			whereQuery,
			ordering,
			limitCQL);

		CQLStatement templateCQLStatement = CQLStatement.make(CQLTemplate, values.toArray());
		templateCQLStatement.setCacheable(true);

		Long starttime = (start == null) ? null : Long.valueOf(UUIDs.unixTimestamp(start));
		Long endtime = (end == null) ? null : Long.valueOf(UUIDs.unixTimestamp(end));
		if( (starttime != null && endtime != null) || (i.getShardingStrategy() instanceof ShardingStrategyNone) ){
			//the query is either bounded or unsharded, so we do not need to check the shardindex
			try{
				Range<Long> shardIdRange = i.getShardingStrategy().getShardKeyRange(starttime,endtime);
				return new UnboundableCQLStatementIterator(shardIdRange,limit,ordering,templateCQLStatement);
			}
			catch(ShardStrategyException e){
				throw new CQLGenerationException(e.getMessage());
			}
		}
		else{
			//we have an unbounded query
			return new BoundedLazyCQLStatementIterator(
					shardList.getShardIdList(def,indexValues,ordering,start,end),
					templateCQLStatement,
					limit
			);
		}
	}

	protected static CQLStatementIterator makeCQLforGet(CObjectShardList shardList, CDefinition def, SortedMap<String,Object> indexvalues, Long limit) throws CQLGenerationException {
		DateTime now = new DateTime(DateTimeZone.UTC);
		long unixtimestamp = (long)now.getMillis();
		return makeCQLforGet(shardList, def, indexvalues, CObjectOrdering.DESCENDING, null, UUIDs.endOf(unixtimestamp), limit, false);
	}

	protected static CQLStatementIterator makeCQLforGet(CObjectShardList shardList, CDefinition def, SortedMap<String,Object> indexvalues, CObjectOrdering ordering,Long starttimestamp, Long endtimestamp, Long limit) throws CQLGenerationException {
		UUID startUUID = (starttimestamp == null) ? null : UUIDs.startOf(starttimestamp.longValue());
		UUID endUUID = (endtimestamp == null) ? null : UUIDs.endOf(endtimestamp.longValue());
		return makeCQLforGet(shardList, def,indexvalues,ordering,startUUID,endUUID,limit, true);
	}

	protected static CQLStatementIterator makeCQLforDelete(CDefinition def, UUID key, Map<String,Object> data, Long timestamp){
		if(timestamp == null){
			timestamp = Long.valueOf(System.currentTimeMillis());
		}
		List<CQLStatement> ret = Lists.newArrayList();
		ret.add(makeCQLforDeleteUUIDFromStaticTable(def, key, timestamp));
		for(CIndex i : def.getIndexes().values()){
			ret.add(makeCQLforDeleteUUIDFromIndex(def, i, key, i.getIndexKeyAndValues(data), timestamp));
		}
		return new BoundedCQLStatementIterator(ret);
	}

	protected static CQLStatement makeCQLforDeleteUUIDFromStaticTable(CDefinition def, UUID uuid, Long timestamp){
		Object[] values = {uuid};
		return CQLStatement.make(String.format(
				TEMPLATE_DELETE,
				makeTableName(def, null),
				//timestamp, //Add back when timestamps become preparable
				"id = ?")
			, values);
	}


	protected static CQLStatement makeCQLforDeleteUUIDFromIndex(CDefinition def, CIndex index, UUID uuid, Map<String,Object> indexValues, Long timestamp){
		List values = Lists.newArrayList( uuid, Long.valueOf(index.getShardingStrategy().getShardKey(uuid)) );
		CQLStatement wheres = makeAndedEqualList(def, indexValues);
		values.addAll(Arrays.asList(wheres.getValues()));
		String whereCQL = String.format( "id = ? AND shardid = ? AND %s", wheres.getQuery());
		String query = String.format(
			TEMPLATE_DELETE,
			makeTableName(def,index),
			//timestamp, //Add back when timestamps become preparable
			whereCQL);

		return CQLStatement.make(query,values.toArray());
	}

	protected static CQLStatement makeTableDrop(String tableName){
		return CQLStatement.make(String.format(TEMPLATE_DROP, tableName));
	}

	protected static CQLStatement makeStaticTableCreate(CDefinition def){
		String query = String.format(
			TEMPLATE_CREATE_STATIC,
			def.getName(),
			makeFieldList(def.getFields().values(),true));
		return CQLStatement.make(query);
	}

	protected static CQLStatement makeWideTableCreate(CDefinition def, CIndex index){
		String query = String.format(
			TEMPLATE_CREATE_WIDE,
			makeTableName(def,index),
			makeFieldList(def.getFields().values(), true),
			makeCommaList(index.getCompositeKeyList()));
		return CQLStatement.make(query);
	}

	public static String makeIndexValuesString(Collection values) throws CQLGenerationException{
		//note, this escaping mechanism can in very rare situations cause index collisions, for example
		//one:two as a value collides with another value one&#58;two
		List<String> escaped = Lists.newArrayList();
		for(Object v : values){
			escaped.add(coerceValueToString(v).replaceAll(":", "&#58;"));
		}
		return Joiner.on(":").join(escaped);
	}

	public static String coerceValueToString(Object value) throws CQLGenerationException {
		if(value instanceof String){
			return (String)value;
		}
		if( (value instanceof UUID) || (value instanceof Long) || (value instanceof Boolean) || (value instanceof Float) || (value instanceof Double) || (value instanceof Integer) || (value instanceof BigInteger) ){
			return value.toString();
		}
		if( value instanceof java.util.Date){
			return ((java.util.Date)value).getTime()+"";
		}
		throw new CQLGenerationException("Rhombus does not support indexes on fields of type " + value.getClass().toString());
	}

	protected static Map<String,ArrayList> makeFieldAndValueList(CDefinition def, Map<String,Object> data){
		ArrayList fieldList = new ArrayList<String>(def.getFields().size());
		ArrayList valueList = new ArrayList<Object>(def.getFields().size());
		for(CField f : def.getFields().values()){
			if(data.get(f.getName()) != null){
				fieldList.add(f.getName());
				valueList.add(data.get(f.getName()));
			}
		}
		Map<String,ArrayList> ret = Maps.newHashMap();
		ret.put("fields", fieldList);
		ret.put("values", valueList);
		return ret;
	}

	protected static boolean validateData(CDefinition def, Map<String,Object> data){
		if(def.isAllowNullPrimaryKeyInserts()){
			return true;
		}
		Collection<String> fields = def.getRequiredFields();
		for( String f : fields){
			if(!data.containsKey(f)){
				return false;
			}
		}
		return true;
	}

	protected static CQLStatement makeAndedEqualList(CDefinition def, Map<String,Object> data){
		String query = "";
		List values = Lists.newArrayList();
		int count = 0;
		for(String key : data.keySet()){
			CField f = def.getFields().get(key);
			query+=f.getName() + " = ?";
			values.add(data.get(key));
			if(++count < data.keySet().size()){
				query += " AND ";
			}
		}
		return CQLStatement.make(query, values.toArray());
	}

	protected static String makeCommaList(List strings, boolean onlyQuestionMarks){
		Iterator<Object> it = strings.iterator();
		String ret = "";
		while(it.hasNext()){
			String thenext = it.next().toString();
			String s = onlyQuestionMarks ? "?" : thenext;
			ret = ret + s +(it.hasNext() ? ", " : "");
		}
		return ret;
	}

	protected static String makeCommaList(List strings){
		return makeCommaList(strings, false);
	}

	protected static String makeFieldList(Collection<CField> fields, boolean withType){
		Iterator<CField> it = fields.iterator();
		String ret = "";
		while(it.hasNext()){
			CField f = it.next();
			ret = ret + f.getName() +
					(withType ? " " + f.getType() : "") +
					(it.hasNext() ? "," : "");
		}
		return ret;
	}

	protected static String makeTableName(CDefinition def, @Nullable CIndex index){
		String objName = def.getName();
		if(index == null){
			return objName;
		}
		else{
			return makeIndexTableName(def,index);
		}
	}

	protected static String makeIndexTableName(CDefinition def, CIndex index){
		String indexName = Joiner.on('_').join(index.getCompositeKeyList());
		String hash = DigestUtils.md5Hex(def.getName()+"|"+indexName);
		//md5 hashes (in hex) give us 32 chars. We have 48 chars available so that gives us 16 chars remaining for a pretty
		//display name for the object type.
		String objDisplayName = def.getName().length() > 15 ? def.getName().substring(0,16) : def.getName();
		return objDisplayName+hash;
	}

	public void setShardList(CObjectShardList shardList) {
		this.shardList = shardList;
	}

}