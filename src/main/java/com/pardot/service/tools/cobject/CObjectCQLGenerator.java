package com.pardot.service.tools.cobject;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
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
		for(CIndex i : def.indexes){
			ret.add(makeWideTableCreate(def, i));
		}
		return ret;
	}

	protected static ArrayList<String> makeCQLforInsert(CDefinition def, JsonNode obj){
		ArrayList<String> ret = new ArrayList<String>();

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

	public ArrayList<String> makeCQLforInsert(String objType, JsonNode obj){
		return makeCQLforInsert(this.definitions.get(objType), obj);
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
			makeFieldList(def.fields, true));
	}

	protected static String makeWideTableCreate(CDefinition def, CIndex index){
		return String.format(
			TEMPLATE_WIDE_CREATE,
			def.name+":"+index.name,
			makeFieldList(def.fields, true),
			makeCommaList(index.compositeKeyList));
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

	protected static String makeFieldList(ArrayList<CField> fields, boolean withType){
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
