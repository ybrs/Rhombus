package com.pardot.service.tools.cobject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	public ArrayList<CField> fields;
	public ArrayList<CIndex> indexes;

	public CDefinition(){
	}

	public CDefinition(String json) throws CObjectParseException{
		try{
			parseJson(json);
		}
		catch(Exception e){
			System.out.println(e.toString());
			throw new CObjectParseException();
		}
	}

	public boolean validateData(HashMap data){
		return true;
	}

	protected void parseJson(String json) throws java.io.IOException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode j =  mapper.readTree(json);
		this.name = j.get("name").asText();
		this.fields = this.generateFields(j.get("fields"));
		this.indexes = this.generateIndexes(j.get("indexes"));
	}

	protected ArrayList<CField> generateFields(JsonNode dict){
		ArrayList<CField> ret = new ArrayList<CField>();
		Iterator<String> keys = dict.fieldNames();
		while(keys.hasNext()){
			String key = keys.next();
			String type = dict.get(key).asText();
			ret.add(new CField(key, CField.getCDataTypeFromString(type)));
		}
		return ret;
	}

	protected ArrayList<CIndex> generateIndexes(JsonNode dict){
		ArrayList<CIndex> ret = new ArrayList<CIndex>();



		return ret;
	}

}
