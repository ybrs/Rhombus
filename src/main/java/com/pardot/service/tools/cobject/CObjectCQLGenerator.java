package com.pardot.service.tools.cobject;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/8/13
 */
public class CObjectCQLGenerator {

	protected static final String TEMPLATE_STATIC_CREATE = "CREATE TABLE %s (id timeuuid PRIMARY KEY, %s);";
	protected static final String TEMPLATE_WIDE_CREATE = "CREATE TABLE %s (id timeuuid, %s, PRIMARY KEY ((%s),id) );";

	protected static final String TEMPLATE_INSERT = "INSERT INTO %s (id, %s) VALUES (NOW(), %s);";

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
		for(CIndex i : def.getIndexes().values()){
			ret.add(makeWideTableCreate(def, i));
		}
		return ret;
	}

	//TODO: TIMESTAMPS AND CONSISTENCY
	protected static List<String> makeCQLforInsert(CDefinition def, Map<String,String> data) throws CQLGenerationException{
		List<String> ret = Lists.newArrayList();
		if(!validateData(def, data)){
			throw new CQLGenerationException("Invalid Insert Requested. Missing Field(s)");
		}
		Map<String,ArrayList<String>> fieldsAndValues = makeFieldAndValueList(def,data);
		//Static Table
		ret.add(String.format(
				TEMPLATE_INSERT,
				def.getName(),
				makeCommaList(fieldsAndValues.get("fields")),
				makeCommaList(fieldsAndValues.get("values"))
		));
		//Index Tables
		for(CIndex i : def.getIndexes().values()){
			if(i.passesAllFilters(data)){
				ret.add(String.format(
						TEMPLATE_INSERT,
						def.getName()+"__"+i.getName(),
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

	protected static List<String> makeCQLforDelete(CDefinition def, String key){
		ArrayList<String> ret = Lists.newArrayList();

		return ret;
	}

	public List<String> makeCQLforCreate(String objType){
		return makeCQLforCreate(this.definitions.get(objType));
	}

	public List<String> makeCQLforInsert(String objType, Map<String,String> data) throws CQLGenerationException {
		return makeCQLforInsert(this.definitions.get(objType), data);
	}

	public String makeCQLforGet(String objType, String key){
		return makeCQLforGet(this.definitions.get(objType), key);
	}

	public String makeCQLforGet(String objType, String index, String[] keys){
	 	return makeCQLforGet(this.definitions.get(objType), index, keys);
	}

	public List<String> makeCQLforDelete(String objType, String key){
		return makeCQLforDelete(this.definitions.get(objType), key);
	}

	protected static String makeStaticTableCreate(CDefinition def){
		return String.format(
			TEMPLATE_STATIC_CREATE,
			def.getName(),
			makeFieldList(def.getFields().values(), true));
	}

	protected static String makeWideTableCreate(CDefinition def, CIndex index){
		return String.format(
			TEMPLATE_WIDE_CREATE,
			def.getName()+"__"+index.getName(),
			makeFieldList(def.getFields().values(), true),
			makeCommaList(index.compositeKeyList));
	}

	protected static Map<String,ArrayList<String>> makeFieldAndValueList(CDefinition def, Map<String,String> data){
		ArrayList<String> fieldList = new ArrayList<String>(def.getFields().size());
		ArrayList<String> valueList = new ArrayList<String>(def.getFields().size());
		for(CField f : def.getFields().values()){
			fieldList.add(f.getName());
			valueList.add(getCQLValueString(f,data.get(f.getName())));
		}
		Map<String,ArrayList<String>> ret = Maps.newHashMap();
		ret.put("fields", fieldList);
		ret.put("values", valueList);
		return ret;
	}

	protected static boolean validateData(CDefinition def, Map<String,String> data){
		Collection<CField> fields = def.getFields().values();
		for( CField f : fields){
			if(!data.containsKey(f.getName())){
				return false;
			}
		}
		return true;
	}

	protected static String getCQLValueString(CField f, String value){
		String strTemplate = "'%s'";
		switch (f.getType()){
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
			ret = ret + f.getName() +
				(withType ? " " + f.getType() : "") +
				(it.hasNext() ? "," : "");
		}
		return ret;
	}

}
