package com.pardot.rhombus.cobject;

/**
 * Pardot, An ExactTarget Company.
 * User: robrighter
 * Date: 4/4/13
 */
public class CField {

	public enum CDataType {
		//Should include all of the type definitions found here:
		//http://www.datastax.com/docs/1.1/references/cql/cql_data_types
		ASCII {
			@Override
			public String toString(){
				return "ascii";
			}
		},
		BIGINT {
			@Override
			public String toString(){
				return "bigint";
			}
		},
		BLOB {
		   @Override
			public String toString(){
			   return "blob";
		   }
		},
		BOOLEAN {
			@Override
			public String toString(){
				return "boolean";
			}
		},
		COUNTER {
			@Override
			public String toString(){
				return "counter";
			}
		},
		DECIMAL {
			@Override
			public String toString(){
				return "decimal";
			}
		},
		DOUBLE {
			@Override
			public String toString(){
				return "double";
			}
		},
		FLOAT {
			@Override
			public String toString(){
				return "float";
			}
		},
		INT {
			@Override
			public String toString(){
				return "int";
			}
		},
		TEXT {
			@Override
			public String toString(){
				return "text";
			}
		},
		TIMESTAMP {
			@Override
			public String toString(){
				return "timestamp";
			}
		},
		UUID {
			@Override
			public String toString(){
				return "uuid";
			}
		},
		TIMEUUID {
			@Override
			public String toString(){
				return "timeuuid";
			}
		},
		VARCHAR {
			@Override
			public String toString(){
				return "varchar";
			}
		},
		VARINT {
			@Override
			public String toString(){
				return "varint";
			}
		}
	}

	private String name;
	private CDataType type;

	public CField() {

	}

	public CField(String name, CDataType type){
		this.name = name;
		this.type = type;
	}

	public CField(String name, String type) throws CObjectParseException {
		this.name = name;
		this.setType(type);
	}

	public static CDataType getCDataTypeFromString(String str) throws CObjectParseException {
		for (CDataType t : CDataType.values()) {
			if(t.toString().equals(str)){
				return t;
			}
		}
		throw new CObjectParseException("Invalid C* type provided in definition: " + str);
	}

	public CDataType getType() {
		return type;
	}
	public void setType(String type) throws CObjectParseException {
		this.type = getCDataTypeFromString(type);
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
