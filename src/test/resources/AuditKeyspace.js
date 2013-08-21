{
    "name" : "functional",
    "replicationClass" : "SimpleStrategy",
    "replicationFactors" : {
        "replication_factor" : 1
    },
    "definitions" : [
        {
            "name": "object_audit",
            "allowNullPrimaryKeyInserts": true,
            "fields": [
                {"name": "account_id", "type": "uuid"},
                {"name": "object_type", "type": "varchar"},
                {"name": "object_id", "type": "uuid"},
                {"name": "source_type", "type": "varchar"},
                {"name": "source_id", "type": "uuid"},
                {"name": "user_id", "type": "uuid"},
                {"name": "created_at", "type": "timestamp"},
                {"name": "type", "type": "varchar"},
                {"name": "changes", "type": "varchar"}
            ],
            "indexes" : [
                {
                    "key": "account_id:object_id:object_type",
                    "shardingStrategy": {"type": "ShardingStrategyNone"}
                },
                {
                    "key": "account_id:user_id",
                    "shardingStrategy": {"type": "ShardingStrategyMonthly"}
                }
            ]
        }
    ]
}


