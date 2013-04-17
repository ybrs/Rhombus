{
    "name" : "pi-analytics",
    "replicationClass" : "SimpleStrategy",
    "replicationFactor" : 1,
    "definitions" : [
        {
            "name": "testtype",
            "fields": [
                {"name": "foreignid", "type": "bigint"},
                {"name": "type", "type": "int"},
                {"name": "instance", "type": "bigint"},
                {"name": "filtered", "type": "int"},
                {"name": "data1", "type": "varchar"},
                {"name": "data2", "type": "varchar"},
                {"name": "data3", "type": "varchar"}
            ],
            "indexes" : [
                {
                    "name": "foreign",
                    "key": "foreignid",
                    "filters" : [{"type": "CIndexFilterIncludeAll"}],
                    "shardingStrategy": {"type": "ShardingStrategyNone"}
                },
                {
                    "name": "instance",
                    "key": "type:instance",
                    "filters" : [{"type": "CIndexFilterIncludeAll"}],
                    "shardingStrategy": {"type": "ShardingStrategyMonthly"}
                },
                {
                    "name": "foreign_instance",
                    "key": "foreignid:type:instance",
                    "filters" : [{"type": "CIndexFilterIncludeAll"}],
                    "shardingStrategy": {"type": "ShardingStrategyMonthly"}
                },
                {
                    "name": "unfiltered_Instance",
                    "key": "type:instance",
                    "filters" : [{"type": "CIndexFilterExcludeFiltered"}],
                    "shardingStrategy": {"type": "ShardingStrategyMonthly"}
                }
            ]
        }
    ]
}


