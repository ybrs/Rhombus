package com.pardot.service.tools.cobject;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/8/13
 */
public class CObjectCQLGenerator {

	protected static final String TEMPLATE_STATIC_CREATE = "CREATE TABLE %s (id timeuuid PRIMARY KEY, %s);";
	protected static final String TEMPLATE_WIDE_CREATE = "CREATE TABLE %s (id timeuuid, %s, PRIMARY KEY ((%s),id) );";
	protected static final String TEMPLATE_INSERT = "INSERT INTO %s (id, %s) VALUES (%s, %s) USING TIMESTAMP %s%s;";

	protected Map<String, CDefinition> definitions;

	public CObjectCQLGenerator(){
		this.definitions = Maps.newHashMap();
	}

	public CObjectCQLGenerator(HashMap<String, CDefinition> objectDefinitions){
		this.definitions = objectDefinitions;
	}


	protected static List<String> makeCQLforCreate(CDefinition def){
		List<String> ret = Lists.newArrayList();
		ret.add(makeStaticTableCreate(def));
		for(CIndex i : def.indexes.values()){
			ret.add(makeWideTableCreate(def, i));
		}
		return ret;
	}


	protected static String makeInsertStatement(String table, String fields, String values, UUID uuid, long timestamp, int ttl){
		return String.format(
				TEMPLATE_INSERT,
				table,
				fields,
				uuid.toString(),
				values,
				timestamp+"",
				(ttl == 0) ? "" : (" AND TTL "+ ttl)
		);
	}

	protected static List<String> makeCQLforInsert(@NotNull CDefinition def, @NotNull Map<String,String> data) throws CQLGenerationException{
		return makeCQLforInsert(def,data,null,0,0);
	}

	protected static List<String> makeCQLforInsert(@NotNull CDefinition def, @NotNull Map<String,String> data, @Nullable UUID uuid, long timestamp, int ttl) throws CQLGenerationException{
		List<String> ret = Lists.newArrayList();
		if(uuid == null){
			uuid = UUIDs.timeBased();
		}
		if(timestamp == 0){
			timestamp = UUIDs.unixTimestamp(uuid);
		}
		if(!validateData(def, data)){
			throw new CQLGenerationException("Invalid Insert Requested. Missing Field(s)");
		}
		Map<String,ArrayList<String>> fieldsAndValues = makeFieldAndValueList(def,data);
		//Static Table
		ret.add(makeInsertStatement(
				def.name,
				makeCommaList(fieldsAndValues.get("fields")),
				makeCommaList(fieldsAndValues.get("values")),
				uuid,
				timestamp,
				ttl
		));
		//Index Tables
		for(CIndex i : def.indexes.values()){
			if(i.passesAllFilters(data)){
				ret.add(makeInsertStatement(
						def.name+"__"+i.name,
						makeCommaList(fieldsAndValues.get("fields")),
						makeCommaList(fieldsAndValues.get("values")),
						uuid,
						timestamp,
						ttl
				));
			}
		}
		return ret;
	}

	protected static String makeCQLforGet(CDefinition def, String key){
		return "";
	}

	protected static String makeCQLforGet(CDefinition def, String index, String[] keys){
		return "";
	}

	protected static List<String> makeCQLforDelete(CDefinition def, String key){
		ArrayList<String> ret = Lists.newArrayList();

		return ret;
	}

	@NotNull
	public List<String> makeCQLforCreate(String objType){
		return makeCQLforCreate(this.definitions.get(objType));
	}

	@NotNull
	public List<String> makeCQLforInsert(String objType, Map<String,String> data) throws CQLGenerationException {
		return makeCQLforInsert(this.definitions.get(objType), data);
	}

	@NotNull
	public String makeCQLforGet(String objType, String key){
		return makeCQLforGet(this.definitions.get(objType), key);
	}

	@NotNull
	public String makeCQLforGet(String objType, String index, String[] keys){
	 	return makeCQLforGet(this.definitions.get(objType), index, keys);
	}

	@NotNull
	public List<String> makeCQLforDelete(String objType, String key){
		return makeCQLforDelete(this.definitions.get(objType), key);
	}

	protected static String makeStaticTableCreate(CDefinition def){
		return String.format(
			TEMPLATE_STATIC_CREATE,
			def.name,
			makeFieldList(def.fields.values(), true));
	}

	protected static String makeWideTableCreate(CDefinition def, CIndex index){
		return String.format(
			TEMPLATE_WIDE_CREATE,
			def.name+"__"+index.name,
			makeFieldList(def.fields.values(), true),
			makeCommaList(index.compositeKeyList));
	}

	protected static Map<String,ArrayList<String>> makeFieldAndValueList(CDefinition def, Map<String,String> data){
		ArrayList<String> fieldList = new ArrayList<String>(def.fields.size());
		ArrayList<String> valueList = new ArrayList<String>(def.fields.size());
		for(CField f : def.fields.values()){
			fieldList.add(f.name);
			valueList.add(getCQLValueString(f,data.get(f.name)));
		}
		Map<String,ArrayList<String>> ret = Maps.newHashMap();
		ret.put("fields", fieldList);
		ret.put("values", valueList);
		return ret;
	}

	protected static boolean validateData(CDefinition def, Map<String,String> data){
		Collection<CField> fields = def.fields.values();
		for( CField f : fields){
			if(!data.containsKey(f.name)){
				return false;
			}
		}
		return true;
	}

	protected static String getCQLValueString(CField f, String value){
		String strTemplate = "'%s'";
		switch (f.type){
			case ASCII:
			case TEXT:
			case TIMESTAMP:
			case VARCHAR:
				return String.format(strTemplate, value);
			default:
				return value;
		}
	}

	protected static String makeCommaList(List<String> strings){
		Iterator<String> it = strings.iterator();
		String ret = "";
		while(it.hasNext()){
			String s = it.next();
			ret = ret + s +(it.hasNext() ? ", " : "");
		}
		return ret;
	}

	protected static String makeFieldList(Collection<CField> fields, boolean withType){
		Iterator<CField> it = fields.iterator();
		String ret = "";
		while(it.hasNext()){
			CField f = it.next();
			ret = ret + f.name +
				(withType ? " " + f.type : "") +
				(it.hasNext() ? "," : "");
		}
		return ret;
	}

}
