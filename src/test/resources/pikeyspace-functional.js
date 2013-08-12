{
    "name" : "pifunctional",
    "replicationClass" : "NetworkTopologyStrategy",
    "replicationFactors" : {
        "SEATTLE" : 2,
        "DALLAS" : 2
    },
    "consistencyLevel" : "QUORUM",
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
        },
        {
            "name": "visitor_audit",
            "allowNullPrimaryKeyInserts": true,
            "fields": [
                {"name": "account_id", "type": "uuid"},
                {"name": "audit_object_type", "type": "varchar"},
                {"name": "audit_object_id", "type": "uuid"},
                {"name": "source_type", "type": "varchar"},
                {"name": "source_id", "type": "uuid"},
                {"name": "user_id", "type": "uuid"},
                {"name": "type", "type": "varchar"},
                {"name": "change_object_type", "type": "varchar"},
                {"name": "change_object_id", "type": "uuid"},
                {"name": "change_value_type", "type": "varchar"},
                {"name": "change_value_id", "type": "uuid"},
                {"name": "change_value", "type": "varchar"},
                {"name": "change_old_value_type", "type": "varchar"},
                {"name": "change_old_value_id", "type": "uuid"},
                {"name": "change_old_value", "type": "varchar"}
            ],
            "indexes" : [
                {
                    "key": "account_id:audit_object_type:audit_object_id",
                    "shardingStrategy": {"type": "ShardingStrategyNone"}
                },
                {
                    "key": "account_id:audit_object_type:audit_object_id:change_object_type:change_object_id",
                    "shardingStrategy": {"type": "ShardingStrategyNone"}
                }
            ]
        }
    ]
}
