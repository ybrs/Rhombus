package com.pardot.service.tools.CassandraObj;

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
				return "bingint";
			}
		}
	}

	public String name;
    public CDataType type;

}
