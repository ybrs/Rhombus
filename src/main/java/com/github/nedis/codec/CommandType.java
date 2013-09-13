package com.github.nedis.codec;

/**
 * User: roger
 * Date: 12-3-15 1:45
 */
public enum CommandType {
    // Connection

    AUTH, ECHO, PING, QUIT, SELECT, OK,

    // Server

    BGREWRITEAOF, BGSAVE, CLIENT, CONFIG, DBSIZE, DEBUG, FLUSHALL,
    FLUSHDB, INFO, LASTSAVE, MONITOR, SAVE, SHUTDOWN, SLAVEOF,
    SLOWLOG, SYNC,

    // Keys

    DEL, EXISTS, EXPIRE, EXPIREAT, KEYS, MOVE, OBJECT, PERSIST,
    RANDOMKEY, RENAME, RENAMENX, TTL, TYPE,

    // String

    APPEND, GET, GETBIT, GETRANGE, GETSET, MGET, MSET, MSETNX,
    SET, SETEX, SETNX, SETBIT, SETRANGE, STRLEN,

    // Integer

    DECR, DECRBY, INCR, INCRBY,

    // List

    BLPOP, BRPOP, BRPOPLPUSH,
    LINDEX, LINSERT, LLEN, LPOP, LPUSH, LPUSHX, LRANGE, LREM, LSET, LTRIM,
    RPOP, RPOPLPUSH, RPUSH, RPUSHX, SORT,

    // Hash

    HDEL, HEXISTS, HGET, HGETALL, HINCRBY, HKEYS, HLEN,
    HMGET, HMSET, HSET, HSETNX, HVALS,

    // Transaction

    DISCARD, EXEC, MULTI, UNWATCH, WATCH,

    // Pub/Sub

    PSUBSCRIBE, PUBLISH, PUNSUBSCRIBE, SUBSCRIBE, UNSUBSCRIBE,

    // Sets

    SADD, SCARD, SDIFF, SDIFFSTORE, SINTER, SINTERSTORE, SISMEMBER,
    SMEMBERS, SMOVE, SPOP, SRANDMEMBER, SREM, SUNION, SUNIONSTORE,

    // Sorted Set

    ZADD, ZCARD, ZCOUNT, ZINCRBY, ZINTERSTORE, ZRANGE, ZRANGEBYSCORE,
    ZRANK, ZREM, ZREMRANGEBYRANK, ZREMRANGEBYSCORE, ZREVRANGE,
    ZREVRANGEBYSCORE, ZREVRANK, ZSCORE, ZUNIONSTORE;

    public  byte[] value;

    private CommandType() {
        value = name().getBytes();
    }
}
