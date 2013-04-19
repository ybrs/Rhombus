package com.pardot.analyticsservice.core;

import com.google.common.collect.Maps;
import com.pardot.analyticsservice.cassandra.ConnectionManager;
import com.pardot.analyticsservice.cassandra.Criteria;
import com.pardot.analyticsservice.cassandra.ObjectMapper;
import com.pardot.analyticsservice.cassandra.cobject.CObjectOrdering;
import com.pardot.analyticsservice.cassandra.cobject.CQLGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.UUID;

/**
 * Pardot, an ExactTarget company
 * User: Michael Frank
 * Date: 4/17/13
 */
public class AnalyticsDataProvider {
	private static Logger logger = LoggerFactory.getLogger(AnalyticsDataProvider.class);

	private ConnectionManager connectionManager;

	public AnalyticsDataProvider(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	public boolean rebuildKeyspace() {
		connectionManager.rebuildKeyspace(null);
		return true;
	}

	/**
	 * Converts HTTP GET params into a criteria and passes those and the object type to the
	 * proper ObjectMapper to retrieve the requested data.  This will include get by id
	 * requests in addition to normal queries.
	 *
	 * @param params
	 * @return Objects requested by the query or an empty list if no matches are found
	 */
	public List<Map<String, String>> doQuery(String objectType, MultivaluedMap<String, String> params) {
		try {
			Criteria criteria = criteriaFromQueryParams(params);
			return connectionManager.getObjectMapper().list(objectType, criteria);
		} catch(Exception e) {
			//TODO
			return null;
		}
	}

	public Map<String, String> doGet(String objectType, String id) {
		UUID uuid = UUID.fromString(id);
		return connectionManager.getObjectMapper().getByKey(objectType, uuid);
	}

	public String doInsert(String objectType, Map<String, String> values) {
		try {
			ObjectMapper mapper = connectionManager.getObjectMapper();
			UUID uuid = mapper.insert(objectType, values);
			String id = uuid.toString();
			return id;
		} catch (CQLGenerationException e) {
			//TODO
			return null;
		}
	}

	public String doUpdate(String objectType, String id, Map<String,String> values) {
		try {
			ObjectMapper mapper = connectionManager.getObjectMapper();
			UUID uuid = mapper.update(objectType, UUID.fromString(id), values);
			return uuid.toString();
		} catch (CQLGenerationException e) {
			//TODO
			return null;
		}
	}

	public void doDelete(String objectType, String id) {
		connectionManager.getObjectMapper().delete(objectType, UUID.fromString(id));
	}

	private Criteria criteriaFromQueryParams(MultivaluedMap<String, String> params) {
		Criteria criteria = new Criteria();
		criteria.setOrdering(params.getFirst("_ordering"));
		criteria.setStartTimestamp(safeParseLong(params.getFirst("_start")));
		criteria.setEndTimestamp(safeParseLong(params.getFirst("_end")));
		criteria.setLimit(safeParseLong(params.getFirst("_limit")));
		SortedMap<String, String> indexes = Maps.newTreeMap();
		for(String key : params.keySet()) {
			if(!key.startsWith("_")) {
				indexes.put(key, params.getFirst(key));
			}
		}
		criteria.setIndexKeys(indexes);
		return criteria;
	}

	private Long safeParseLong(String string) {
		Long long1 = null;
		try {
			long1 = Long.parseLong(string);
		} catch(Exception e) {
			//ignore
		}
		return long1;
	}
}
