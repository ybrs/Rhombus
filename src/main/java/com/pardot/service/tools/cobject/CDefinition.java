package com.pardot.service.tools.cobject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pardot.service.tools.cobject.filters.CIndexFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Pardot, An ExactTarget Company.
 * User: robrighter
 * Date: 4/4/13
 */
public class CDefinition {

	public String name;
	public HashMap<String, CField> fields;
	public HashMap<String, CIndex> indexes;

	public CDefinition(){
	}

	public CDefinition(String json) throws CObjectParseException{
		try{
			parseJson(json);
		}
		catch(Exception e){
			throw new CObjectParseException(e.toString());
		}
	}

	public boolean validateData(HashMap data){
		return true;
	}

	protected void parseJson(String json) throws java.io.IOException, CObjectParseException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode j =  mapper.readTree(json);
		this.name = j.get("name").asText();
		this.fields = this.generateFields(j.get("fields"));
		this.indexes = this.generateIndexes(j.get("indexes"));
	}

	protected HashMap<String, CField> generateFields(JsonNode dict){
		HashMap<String, CField> ret = new HashMap<String, CField>();
		Iterator<String> keys = dict.fieldNames();
		while(keys.hasNext()){
			String key = keys.next();
			String type = dict.get(key).asText();
			ret.put(key, new CField(key, CField.getCDataTypeFromString(type)));
		}
		return ret;
	}

	protected HashMap<String,CIndex> generateIndexes(JsonNode dict) throws CObjectParseException{
		HashMap<String, CIndex> ret = new HashMap<String, CIndex>();
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

	protected ArrayList<CIndexFilter> makeFilterList(JsonNode jn) throws CObjectParseException{
		try{
			ArrayList<CIndexFilter> ret = new ArrayList<CIndexFilter>();
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
