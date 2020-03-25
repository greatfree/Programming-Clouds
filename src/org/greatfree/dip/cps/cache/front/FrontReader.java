package org.greatfree.dip.cps.cache.front;

import java.io.IOException;
import java.util.Set;

import org.greatfree.client.RemoteReader;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.cps.cache.message.front.CachePointingByIndexRequest;
import org.greatfree.dip.cps.cache.message.front.CachePointingByIndexResponse;
import org.greatfree.dip.cps.cache.message.front.CachePointingByKeyRequest;
import org.greatfree.dip.cps.cache.message.front.CachePointingByKeyResponse;
import org.greatfree.dip.cps.cache.message.front.ContainsKeyOfCachePointingRequest;
import org.greatfree.dip.cps.cache.message.front.ContainsKeyOfCachePointingResponse;
import org.greatfree.dip.cps.cache.message.front.DequeueSingleMyStoreDataRequest;
import org.greatfree.dip.cps.cache.message.front.DequeueSingleMyStoreDataResponse;
import org.greatfree.dip.cps.cache.message.front.IsCacheEmptyInPointingStoreRequest;
import org.greatfree.dip.cps.cache.message.front.IsCacheEmptyInPointingStoreResponse;
import org.greatfree.dip.cps.cache.message.front.IsCacheExistedInPointingStoreRequest;
import org.greatfree.dip.cps.cache.message.front.IsCacheExistedInPointingStoreResponse;
import org.greatfree.dip.cps.cache.message.front.IsCacheReadExistedInPointingStoreRequest;
import org.greatfree.dip.cps.cache.message.front.IsCacheReadExistedInPointingStoreResponse;
import org.greatfree.dip.cps.cache.message.front.LoadMapStoreDataKeysRequest;
import org.greatfree.dip.cps.cache.message.front.LoadMapStoreDataKeysResponse;
import org.greatfree.dip.cps.cache.message.front.LoadMaxMyPointingRequest;
import org.greatfree.dip.cps.cache.message.front.LoadMaxMyPointingResponse;
import org.greatfree.dip.cps.cache.message.front.LoadMinMyPointingRequest;
import org.greatfree.dip.cps.cache.message.front.LoadMinMyPointingResponse;
import org.greatfree.dip.cps.cache.message.front.LoadMyDataByKeysRequest;
import org.greatfree.dip.cps.cache.message.front.LoadMyDataByKeysResponse;
import org.greatfree.dip.cps.cache.message.front.LoadMyDataRequest;
import org.greatfree.dip.cps.cache.message.front.LoadMyDataResponse;
import org.greatfree.dip.cps.cache.message.front.LoadMyPointingMapRequest;
import org.greatfree.dip.cps.cache.message.front.LoadMyPointingMapResponse;
import org.greatfree.dip.cps.cache.message.front.LoadMyPointingRequest;
import org.greatfree.dip.cps.cache.message.front.LoadMyPointingResponse;
import org.greatfree.dip.cps.cache.message.front.LoadMyUKRequest;
import org.greatfree.dip.cps.cache.message.front.LoadMyUKResponse;
import org.greatfree.dip.cps.cache.message.front.LoadRangeMyPointingsRequest;
import org.greatfree.dip.cps.cache.message.front.LoadRangeMyPointingsResponse;
import org.greatfree.dip.cps.cache.message.front.LoadRangeMyUKsRequest;
import org.greatfree.dip.cps.cache.message.front.LoadRangeMyUKsResponse;
import org.greatfree.dip.cps.cache.message.front.LoadTopMyPointingsRequest;
import org.greatfree.dip.cps.cache.message.front.LoadTopMyPointingsResponse;
import org.greatfree.dip.cps.cache.message.front.LoadTopMyUKsRequest;
import org.greatfree.dip.cps.cache.message.front.LoadTopMyUKsResponse;
import org.greatfree.dip.cps.cache.message.front.MaxCachePointingRequest;
import org.greatfree.dip.cps.cache.message.front.MaxCachePointingResponse;
import org.greatfree.dip.cps.cache.message.front.PeekMyStoreDataQueueRequest;
import org.greatfree.dip.cps.cache.message.front.PeekMyStoreDataQueueResponse;
import org.greatfree.dip.cps.cache.message.front.PeekMyStoreDataRequest;
import org.greatfree.dip.cps.cache.message.front.PeekMyStoreDataResponse;
import org.greatfree.dip.cps.cache.message.front.PeekSingleMyStoreDataQueueRequest;
import org.greatfree.dip.cps.cache.message.front.PeekSingleMyStoreDataQueueResponse;
import org.greatfree.dip.cps.cache.message.front.PeekSingleMyStoreDataRequest;
import org.greatfree.dip.cps.cache.message.front.PeekSingleMyStoreDataResponse;
import org.greatfree.dip.cps.cache.message.front.PointingByIndexPrefetchListRequest;
import org.greatfree.dip.cps.cache.message.front.PointingByIndexPrefetchListResponse;
import org.greatfree.dip.cps.cache.message.front.PopSingleMyStoreDataRequest;
import org.greatfree.dip.cps.cache.message.front.PopSingleMyStoreDataResponse;
import org.greatfree.dip.cps.cache.message.front.RangeCachePointingsRequest;
import org.greatfree.dip.cps.cache.message.front.RangeCachePointingsResponse;
import org.greatfree.dip.cps.cache.message.front.RangePointingsPrefetchListRequest;
import org.greatfree.dip.cps.cache.message.front.RangePointingsPrefetchListResponse;
import org.greatfree.dip.cps.cache.message.front.RangeReadCachePointingsRequest;
import org.greatfree.dip.cps.cache.message.front.RangeReadCachePointingsResponse;
import org.greatfree.dip.cps.cache.message.front.TopCachePointingsRequest;
import org.greatfree.dip.cps.cache.message.front.TopCachePointingsResponse;
import org.greatfree.dip.cps.cache.message.front.TopPointingsPrefetchListRequest;
import org.greatfree.dip.cps.cache.message.front.TopPointingsPrefetchListResponse;
import org.greatfree.dip.cps.cache.message.front.TopReadCachePointingsRequest;
import org.greatfree.dip.cps.cache.message.front.TopReadCachePointingsResponse;
import org.greatfree.dip.cps.cache.message.prefetch.DequeueMyStoreDataRequest;
import org.greatfree.dip.cps.cache.message.prefetch.DequeueMyStoreDataResponse;
import org.greatfree.dip.cps.cache.message.prefetch.PopMyStoreDataRequest;
import org.greatfree.dip.cps.cache.message.prefetch.PopMyStoreDataResponse;
import org.greatfree.dip.cps.threetier.message.FrontRequest;
import org.greatfree.dip.cps.threetier.message.FrontResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.NodeID;

// Created: 07/06/2018, Bing Li
class FrontReader
{
	private FrontReader()
	{
	}
	
	/*
	 * Initialize a singleton. 07/06/2018, Bing Li
	 */
	private static FrontReader instance = new FrontReader();
	
	public static FrontReader RR()
	{
		if (instance == null)
		{
			instance = new FrontReader();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void init()
	{
		RemoteReader.REMOTE().init(ClientConfig.CLIENT_READER_POOL_SIZE);
	}

	/*
	 * Shutdown the remote reader. 11/23/2014, Bing Li
	 */
	public void shutdown() throws IOException
	{
		RemoteReader.REMOTE().shutdown();
	}

	public FrontResponse query(String query) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (FrontResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new FrontRequest(query)));
	}

	public LoadMyDataResponse loadDataFromDistributedMap(String myDataKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadMyDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadMyDataRequest(myDataKey)));
	}

	public LoadMyDataResponse loadDataFromDistributedMapStore(String mapKey, String myDataKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadMyDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadMyDataRequest(mapKey, myDataKey)));
	}

	public LoadMyDataResponse loadDataFromPostDistributedMap(String myDataKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadMyDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadMyDataRequest(myDataKey, true)));
	}
	
	public LoadMyDataByKeysResponse loadDataFromDistributedMap(Set<String> keys) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadMyDataByKeysResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadMyDataByKeysRequest(keys)));
	}
	
	public LoadMyDataByKeysResponse loadDataFromDistributedMapStore(String mapKey, Set<String> keys) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadMyDataByKeysResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadMyDataByKeysRequest(mapKey, keys)));
	}
	
	public LoadMyDataByKeysResponse loadDataFromPostDistributedMap(Set<String> keys) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadMyDataByKeysResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadMyDataByKeysRequest(keys, true)));
	}
	
	public LoadMyPointingResponse loadMyPointingFromPointingDistributedList(int index) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadMyPointingResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadMyPointingRequest(index)));
	}
	
	public LoadTopMyPointingsResponse loadTopMyPointingsFromPointingDistributedList(int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadTopMyPointingsResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadTopMyPointingsRequest(endIndex)));
	}
	
	public LoadRangeMyPointingsResponse loadRangeMyPointingsFromPointingDistributedList(int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadRangeMyPointingsResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadRangeMyPointingsRequest(startIndex, endIndex)));
	}
	
	public LoadMyPointingMapResponse loadMyPointingFromPointingDistributedMap(String key) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadMyPointingMapResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadMyPointingMapRequest(key)));
	}
	
	public LoadMaxMyPointingResponse loadMaxMyPointingFromPointingDistributedMap() throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadMaxMyPointingResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadMaxMyPointingRequest()));
	}
	
	public LoadMinMyPointingResponse loadMinMyPointingFromPointingDistributedMap() throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadMinMyPointingResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadMinMyPointingRequest()));
	}
	
	public ContainsKeyOfCachePointingResponse containsKeyOfCachePointing(String mapKey, String resourceKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (ContainsKeyOfCachePointingResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new ContainsKeyOfCachePointingRequest(mapKey, resourceKey, false)));
	}
	
	public ContainsKeyOfCachePointingResponse containsKeyOfCacheTiming(String mapKey, String resourceKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (ContainsKeyOfCachePointingResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new ContainsKeyOfCachePointingRequest(mapKey, resourceKey, true)));
	}
	
	public MaxCachePointingResponse getMaxCachePointing(String mapKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (MaxCachePointingResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new MaxCachePointingRequest(mapKey, false)));
	}
	
	public MaxCachePointingResponse getMaxCacheTiming(String mapKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (MaxCachePointingResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new MaxCachePointingRequest(mapKey, true)));
	}
	
	public CachePointingByKeyResponse getCachePointing(String mapKey, String resourceKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (CachePointingByKeyResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new CachePointingByKeyRequest(mapKey, resourceKey, false)));
	}
	
	public CachePointingByKeyResponse getCacheTiming(String mapKey, String resourceKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (CachePointingByKeyResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new CachePointingByKeyRequest(mapKey, resourceKey, true)));
	}

	public IsCacheExistedInPointingStoreResponse isPointingCacheExisted(String mapKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (IsCacheExistedInPointingStoreResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new IsCacheExistedInPointingStoreRequest(mapKey, false)));
	}

	public IsCacheExistedInPointingStoreResponse isTimingCacheExisted(String mapKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (IsCacheExistedInPointingStoreResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new IsCacheExistedInPointingStoreRequest(mapKey, true)));
	}

	public IsCacheEmptyInPointingStoreResponse isPointingCacheEmpty(String mapKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (IsCacheEmptyInPointingStoreResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new IsCacheEmptyInPointingStoreRequest(mapKey, false)));
	}

	public IsCacheEmptyInPointingStoreResponse isTimingCacheEmpty(String mapKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (IsCacheEmptyInPointingStoreResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new IsCacheEmptyInPointingStoreRequest(mapKey, true)));
	}

	public CachePointingByIndexResponse getCachePointing(String mapKey, int index) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (CachePointingByIndexResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new CachePointingByIndexRequest(mapKey, index, false)));
	}

	public CachePointingByIndexResponse getCacheTiming(String mapKey, int index) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (CachePointingByIndexResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new CachePointingByIndexRequest(mapKey, index, true)));
	}
	
	public TopCachePointingsResponse getTopCachePointing(String mapKey, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (TopCachePointingsResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new TopCachePointingsRequest(mapKey, endIndex, false)));
	}
	
	public TopCachePointingsResponse getTopCacheTiming(String mapKey, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (TopCachePointingsResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new TopCachePointingsRequest(mapKey, endIndex, true)));
	}
	
	public RangeCachePointingsResponse getRangeCachePointing(String mapKey, int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (RangeCachePointingsResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new RangeCachePointingsRequest(mapKey, startIndex, endIndex, false)));
	}
	
	public RangeCachePointingsResponse getRangeCacheTiming(String mapKey, int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (RangeCachePointingsResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new RangeCachePointingsRequest(mapKey, startIndex, endIndex, true)));
	}

	public PointingByIndexPrefetchListResponse getPointingPrefetchList(String listKey, int index) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PointingByIndexPrefetchListResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PointingByIndexPrefetchListRequest(listKey, index)));
	}
	
	public TopPointingsPrefetchListResponse getTopPointingsPrefetchList(String listKey, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (TopPointingsPrefetchListResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new TopPointingsPrefetchListRequest(listKey, endIndex)));
	}
	
	public RangePointingsPrefetchListResponse getRangePointingPrefetchList(String listKey, int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (RangePointingsPrefetchListResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new RangePointingsPrefetchListRequest(listKey, startIndex, endIndex)));
	}

	public IsCacheReadExistedInPointingStoreResponse isCacheReadExisted(String mapKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (IsCacheReadExistedInPointingStoreResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new IsCacheReadExistedInPointingStoreRequest(mapKey)));
	}
	
	public TopReadCachePointingsResponse getTopReadCachePointing(String mapKey, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (TopReadCachePointingsResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new TopReadCachePointingsRequest(mapKey, endIndex)));
	}
	
	public RangeReadCachePointingsResponse getRangeReadCachePointing(String mapKey, int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (RangeReadCachePointingsResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new RangeReadCachePointingsRequest(mapKey, startIndex, endIndex)));
	}

	public PopSingleMyStoreDataResponse popSingleFromStack(String stackKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PopSingleMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PopSingleMyStoreDataRequest(stackKey, false)));
	}

	public PopSingleMyStoreDataResponse popSingleFromReadStack(String stackKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PopSingleMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PopSingleMyStoreDataRequest(stackKey, true)));
	}

	public PopMyStoreDataResponse popFromStack(String stackKey, int count) throws ClassNotFoundException, RemoteReadException, IOException
	{
//		return (PopMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PopMyStoreDataRequest(stackKey, count, false, false)));
		return (PopMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PopMyStoreDataRequest(stackKey, count, false, false)));
	}

	public PopMyStoreDataResponse popFromReadStack(String stackKey, int count) throws ClassNotFoundException, RemoteReadException, IOException
	{
//		return (PopMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PopMyStoreDataRequest(stackKey, count, true, false)));
		return (PopMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PopMyStoreDataRequest(stackKey, count, true, false)));
	}

	public PeekSingleMyStoreDataResponse peekSingleFromStack(String stackKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PeekSingleMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PeekSingleMyStoreDataRequest(stackKey, true)));
	}

	public PeekSingleMyStoreDataResponse peekSingleFromReadStack(String stackKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PeekSingleMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PeekSingleMyStoreDataRequest(stackKey, true)));
	}

	public PeekMyStoreDataResponse peekFromStack(String stackKey, int count) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PeekMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PeekMyStoreDataRequest(stackKey, count, false)));
	}
	
	public PeekMyStoreDataResponse peekFromStack(String stackKey, int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PeekMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PeekMyStoreDataRequest(stackKey, startIndex, endIndex, false)));
	}
	
	public PeekMyStoreDataResponse getFromStack(String stackKey, int index) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PeekMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PeekMyStoreDataRequest(stackKey, false, index)));
	}

	public PeekMyStoreDataResponse peekFromReadStack(String stackKey, int count) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PeekMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PeekMyStoreDataRequest(stackKey, count, true)));
	}

	public PeekMyStoreDataResponse peekFromReadStack(String stackKey, int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PeekMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PeekMyStoreDataRequest(stackKey, startIndex, endIndex, true)));
	}

	public PeekMyStoreDataResponse getFromReadStack(String stackKey, int index) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PeekMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PeekMyStoreDataRequest(stackKey, true, index)));
	}
	
	public DequeueSingleMyStoreDataResponse dequeueSingleFromQueue(String queueKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (DequeueSingleMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new DequeueSingleMyStoreDataRequest(queueKey)));
	}

	public DequeueMyStoreDataResponse dequeueFromQueue(String queueStack, int count) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (DequeueMyStoreDataResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new DequeueMyStoreDataRequest(queueStack, count, false)));
	}

	public PeekSingleMyStoreDataQueueResponse peekSingleFromQueue(String queueKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PeekSingleMyStoreDataQueueResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PeekSingleMyStoreDataQueueRequest(queueKey)));
	}

	public PeekMyStoreDataQueueResponse peekFromQueue(String queueKey, int count) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PeekMyStoreDataQueueResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PeekMyStoreDataQueueRequest(queueKey, count)));
	}

	public PeekMyStoreDataQueueResponse peekFromQueue(String queueKey, int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PeekMyStoreDataQueueResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PeekMyStoreDataQueueRequest(queueKey, startIndex, endIndex)));
	}

	public PeekMyStoreDataQueueResponse getFromQueue(String queueKey, int startIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PeekMyStoreDataQueueResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PeekMyStoreDataQueueRequest(startIndex, queueKey)));
	}

	public LoadMapStoreDataKeysResponse getDataKeys(String mapKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadMapStoreDataKeysResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadMapStoreDataKeysRequest(mapKey)));
	}
	
	public LoadMyUKResponse loadUKFromDistributedList(int index) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadMyUKResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadMyUKRequest(index)));
	}
	
	public LoadTopMyUKsResponse loadTopUKsFromDistributedList(int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadTopMyUKsResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadTopMyUKsRequest(endIndex)));
	}
	
	public LoadRangeMyUKsResponse loadRangeUKsFromDistributedList(int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (LoadRangeMyUKsResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new LoadRangeMyUKsRequest(startIndex, endIndex)));
	}
}
