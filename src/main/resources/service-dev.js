{
    "environment": "dev",
    "cassandraConfiguration": {
        "contactPoints" : [
            "cdev.localhost.com"
        ]
    },

    "logging" : {
        "level": "INFO",
        "loggers": {
            "com.pardot.analyticsservice": "DEBUG",
            "com.pardot.analyticsservice.cassandra": "DEBUG"
        },
        "console": {
            "enabled": "true",
            "threshold": "ALL"
        }

    }

}