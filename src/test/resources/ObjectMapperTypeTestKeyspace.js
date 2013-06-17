{
    "name" : "functional",
    "replicationClass" : "SimpleStrategy",
    "replicationFactors" : {
        "replication_factor" : 1
    },
    "definitions" : [
        {
            "name": "testobjecttype",
            "fields": [
                {"name": "asciiType", "type": "ascii"},
                {"name": "varcharType", "type": "varchar"},
                {"name": "textType", "type": "text"},
                {"name": "bigintType", "type": "bigint"},
                {"name": "booleanType", "type": "boolean"},
                {"name": "decimalType", "type": "decimal"},
                {"name": "doubleType", "type": "double"},
                {"name": "floatType", "type": "float"},
                {"name": "intType", "type": "int"},
                {"name": "timestampType", "type": "timestamp"},
                {"name": "uuidType", "type": "uuid"},
                {"name": "timeuuidType", "type": "timeuuid"},
                {"name": "varintType", "type": "varint"},
                {"name": "varintStringType", "type": "varint"}
            ]
        }
    ]
}


