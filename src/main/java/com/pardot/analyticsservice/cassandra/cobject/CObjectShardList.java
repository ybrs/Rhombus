package com.pardot.analyticsservice.cassandra.cobject;

import javax.annotation.Nullable;
import java.util.List;
import java.util.SortedMap;
import java.util.UUID;

/**
 * Pardot, An ExactTarget Company
 * User: robrighter
 * Date: 4/17/13
 */
public interface CObjectShardList {

	public static String SHARD_INDEX_TABLE_NAME = "__shardindex";

	List<Long> getShardIdList(CDefinition def, SortedMap<String,String> indexValues, CObjectOrdering ordering,@Nullable UUID start, @Nullable UUID end);
}
