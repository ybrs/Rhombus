Rhombus
===========================

A time-series object store for Cassandra that handles all the complexity of building wide row indexes.

When to use Rhombus
===========================
Rhombus is most useful when your use case meets the following criteria:

* You need to store time-series data
* You intend to query out that data in time-series order
* You want to query out that data in a manner similar to sql (where field_a = something and field_b = something else)
* Your data is mostly immutable (Updates are supported but are not as fast as normal inserts)

In summary, Rhombus is the right solution if you are storing huge amounts of time-series data and wish you could query it in real time (fast enough for a web page view) with something like:

    SELECT * from sometype 
        where field_a = xx and
        field_b = yy and
        field_c = zz and
        created after some_date and
        created before some_other_date
        limit 10;

*note - you wont actually query data from Rhombus via sql, but since sql is something everybody understands it is used above.


Define objects by creating a json document for each object type
================================================================

We call these objects CDefinitions. They are a very simple mapping of fields to types. It should also include a list of indexes which define the ways in which you intend to query the data. With Rhombus you can query data by any combination of fields. However, in order to make that possible, you need to indicate which field combinations you intend to query with at the time of definition.

<strong>Example CDefinition:</strong>

    {
        "name": "home_runs",
        "fields": [
            {"name": "player_name", "type": "bigint"},
            {"name": "players_on_base", "type": "int"},
            {"name": "player_team", "type": "varchar"},
            {"name": "baseball_stadium", "type": "varchar"},
            {"name": "pitcher_name", "type": "varchar"}
        ],
        "indexes" : [
            {
                "key": "player_team",
                "shardingStrategy": {"type": "ShardingStrategyMonthly"}
            },
            {
                "key": "baseball_stadium",
                "shardingStrategy": {"type": "ShardingStrategyMonthly"}
            },
            {
                "key": "player_team:players_on_base",
                "shardingStrategy": {"type": "ShardingStrategyNone"}
            },
            {
                "key": "player_name:baseball_stadium",
                "shardingStrategy": {"type": "ShardingStrategyNone"}
            }
        ]
    }


<strong>Queries</strong>

I have created an object called a 'home_run'. With this definition of home run I have lots of options for quering, those options are:

1. Give me all the home runs by the 'Atlanta Braves'.

2. Give me all the home runs at 'Yankee Stadium' that occured yesterday

3. Give me all the home runs at 'Yankee Stadium' over the last 4 years

4. Give me all the home runs by the 'SF Giants' where they had 3 players on base

5. Give me all the home runs by 'Barry Bonds' at 'AT&T Park' between 2000 and 2007

6. Give me all home runs ever (paginated)

7. Give me all home runs between the years 1930 - 2002 paginated.

6. Each CDefinition has an implied object instance id. So I could say give me home run of id xxxxxxx.


<strong>What is a ShardStrategy?</strong>

You will notice that each index has a ShardingStrategy. In cassandra wide rows have a limit on just how wide they can be (usually somewhere in the low millions of records). You can find more details on how large wide rows should be in this blog post: http://www.ebaytechblog.com/2012/08/14/cassandra-data-modeling-best-practices-part-2/ . ShardingStrategies in Rhombus
will automatically take care of sharding for you. The only thing you need to do is provide a hint to indicate the size of your data growth. In the above example, we are expecting some amazing baseball. We estimate that a single MLB team will be hitting over a few million home runs every month. Therefore we decided to break up our wide rows for team and stadium on a monthly strategy. However, we are expecting a low number for the other 2 indexes and therefore used no sharding for those indexes. This means that we will include all of the entries for those last 2 indexes in a single wide row.

<strong>Updates</strong>

In Rhombus you can only update objects by id. So you can say things like "Change home_run x to be in stadium 'Turner Field' instead of 'At&T Field'" but I cannot say "Make every home_run in 'Turner Field' now be 'AT&T Field'". Updates should be use sparingly. They will scale, but because cassandra is an eventually consistent datastore Rhombus needs to take extra care to avoid data inconsistencies. The updates are performed in a manner that will avoid 99% of all inconsistencies, however a background job will also need to run periodically to verify that no update inconsistencies get persisted.




