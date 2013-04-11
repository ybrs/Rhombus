package com.pardot.service.tools.cobject;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/8/13
 */
public class CObjectCQLGenerator {

	protected static final String TEMPLATE_STATIC_CREATE = "CREATE TABLE %s (id timeuuid PRIMARY KEY, %s);";
	protected static final String TEMPLATE_WIDE_CREATE = "CREATE TABLE %s (id timeuuid, %s, PRIMARY KEY ((%s),id) );";

	protected static final String TEMPLATE_INSERT = "INSERT INTO %s (id, %s) VALUES (NOW(), %s);";

	protected HashMap<String, CDefinition> definitions;

	public CObjectCQLGenerator(){
		this.definitions = new HashMap<String, CDefinition>();
	}

	public CObjectCQLGenerator(HashMap<String, CDefinition> objectDefinitions){
		this.definitions = objectDefinitions;
	}

	protected static ArrayList<String> makeCQLforCreate(CDefinition def){
		ArrayList<String> ret = new ArrayList<String>();
		ret.add(makeStaticTableCreate(def));
		for(CIndex i : def.indexes.values()){
			ret.add(makeWideTableCreate(def, i));
		}
		return ret;
	}

	//TODO: TIMESTAMPS AND CONSISTENCY
	protected static ArrayList<String> makeCQLforInsert(CDefinition def, HashMap<String,String> data) throws CQLGenerationException{
		ArrayList<String> ret = new ArrayList<String>();
		if(!validateData(def, data)){
			throw new CQLGenerationException("Invalid Insert Requested. Missing Field(s)");
		}
		HashMap<String,ArrayList<String>> fieldsAndValues = makeFieldAndValueList(def,data);
		//Static Table
		ret.add(String.format(
				TEMPLATE_INSERT,
				def.name,
				makeCommaList(fieldsAndValues.get("fields")),
				makeCommaList(fieldsAndValues.get("values"))
		));
		//Index Tables
		for(CIndex i : def.indexes.values()){
			if(i.passesAllFilters(data)){
				ret.add(String.format(
						TEMPLATE_INSERT,
						def.name+"__"+i.name,
						makeCommaList(fieldsAndValues.get("fields")),
						makeCommaList(fieldsAndValues.get("values"))
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

	protected static ArrayList<String> makeCQLforDelete(CDefinition def, String key){
		ArrayList<String> ret = new ArrayList<String>();

		return ret;
	}

	public ArrayList<String> makeCQLforCreate(String objType){
		return makeCQLforCreate(this.definitions.get(objType));
	}

	public ArrayList<String> makeCQLforInsert(String objType, HashMap<String,String> data) throws CQLGenerationException {
		return makeCQLforInsert(this.definitions.get(objType), data);
	}

	public String makeCQLforGet(String objType, String key){
		return makeCQLforGet(this.definitions.get(objType), key);
	}

	public String makeCQLforGet(String objType, String index, String[] keys){
	 	return makeCQLforGet(this.definitions.get(objType), index, keys);
	}

	public ArrayList<String> makeCQLforDelete(String objType, String key){
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

	protected static HashMap<String,ArrayList<String>> makeFieldAndValueList(CDefinition def, HashMap<String,String> data){
		ArrayList<String> fieldList = new ArrayList<String>(def.fields.size());
		ArrayList<String> valueList = new ArrayList<String>(def.fields.size());
		for(CField f : def.fields.values()){
			fieldList.add(f.name);
			valueList.add(getCQLValueString(f,data.get(f.name)));
		}
		HashMap<String,ArrayList<String>> ret = new HashMap<String, ArrayList<String>>();
		ret.put("fields", fieldList);
		ret.put("values", valueList);
		return ret;
	}

	protected static boolean validateData(CDefinition def, HashMap<String,String> data){
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

	protected static String makeCommaList(ArrayList<String> strings){
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
