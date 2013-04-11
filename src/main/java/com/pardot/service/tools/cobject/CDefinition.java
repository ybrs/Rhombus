package com.pardot.service.tools.cobject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pardot.service.tools.cobject.filters.CIndexFilter;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

/**
 * Pardot, An ExactTarget Company.
 * User: robrighter
 * Date: 4/4/13
 */
public class CDefinition {

	public String name;
	public Map<String, CField> fields;
	public Map<String, CIndex> indexes;

	public CDefinition(){
	}

	public CDefinition(@NotNull String json) throws CObjectParseException{
		try{
			parseJson(json);
		}
		catch(Exception e){
			throw new CObjectParseException(e.toString());
		}
	}

	protected void parseJson(@NotNull String json) throws java.io.IOException, CObjectParseException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode j =  mapper.readTree(json);
		this.name = j.get("name").asText();
		this.fields = this.generateFields(j.get("fields"));
		this.indexes = this.generateIndexes(j.get("indexes"));
	}

	@NotNull
	protected Map<String, CField> generateFields(@NotNull JsonNode dict) throws CObjectParseException {
		Map<String, CField> ret = Maps.newHashMap();
		Iterator<String> keys = dict.fieldNames();
		while(keys.hasNext()){
			String key = keys.next();
			String type = dict.get(key).asText();
			ret.put(key, new CField(key, CField.getCDataTypeFromString(type)));
		}
		return ret;
	}

	@NotNull
	protected Map<String,CIndex> generateIndexes(@NotNull JsonNode dict) throws CObjectParseException{
		Map<String, CIndex> ret = Maps.newHashMap();
		Iterator<String> keys = dict.fieldNames();
		while(keys.hasNext()){
			String name = keys.next();
			String key = dict.get(name).get("key").asText();
			CIndex toadd = new CIndex(name, key);
			toadd.filters = this.makeFilterList(dict.get(name).get("filters"));
			ret.put(name,toadd);
		}
		return ret;
	}

	@NotNull
	protected List<CIndexFilter> makeFilterList(@NotNull JsonNode jn) throws CObjectParseException{
		try{
			List<CIndexFilter> ret = Lists.newArrayList();
			Iterator<JsonNode> it = jn.iterator();
			while(it.hasNext()){
				JsonNode item = it.next();
				String TheClassName = item.asText();
				Class c = Class.forName("com.pardot.service.tools.cobject.filters."+TheClassName);
				CIndexFilter toadd = (CIndexFilter)c.newInstance();
				ret.add(toadd);
			}
			return ret;
		}
		catch (ClassNotFoundException ce){
			throw new CObjectParseException("Could not find class given by filter list");
		}
		catch (InstantiationException ie){
			throw new CObjectParseException("Could not instantiate filter class");
		}
		catch (Exception e){
			throw new CObjectParseException(e.toString());
		}
	}

}
