package com.pardot.service.tools.cobject;

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
				return "counter";
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

	public String name;
	public CDataType type;

	public CField(String name, CDataType type){
		this.name = name;
		this.type = type;
	}

	public static CDataType getCDataTypeFromString(String str) throws CObjectParseException {
		for (CDataType t : CDataType.values()) {
			if(t.toString().equals(str)){
				return t;
			}
		}
		throw new CObjectParseException("Invalid C* type provided in definition: " + str);
	}


}
