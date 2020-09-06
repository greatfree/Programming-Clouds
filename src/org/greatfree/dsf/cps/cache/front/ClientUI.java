package org.greatfree.dsf.cps.cache.front;

import java.io.IOException;

import org.greatfree.dsf.cps.cache.TestCacheConfig;
import org.greatfree.dsf.cps.threetier.message.FrontResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Rand;

// Created: 07/06/2018, Bing Li
class ClientUI
{
	/*
	 * Initialize. 04/23/2017, Bing Li
	 */
	private ClientUI()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static ClientUI instance = new ClientUI();
	
	public static ClientUI CPS()
	{
		if (instance == null)
		{
			instance = new ClientUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
	}

	/*
	 * Print the menu list on the screen. 04/23/2017, Bing Li
	 */
	public void printMenu()
	{
		System.out.println(ClientMenu.MENU_HEAD);
		System.out.println(ClientMenu.NOTIFY);
		System.out.println(ClientMenu.REQUEST);
		System.out.println(ClientMenu.DELIMITER);

		System.out.println(ClientMenu.SAVE_DATA_INTO_DISTRIBUTED_MAP);
		System.out.println(ClientMenu.SAVE_MUCH_DATA_INTO_DISTRIBUTED_MAP);
		System.out.println(ClientMenu.LOAD_DATA_FROM_DISTRIBUTED_MAP);
		System.out.println(ClientMenu.LOAD_DATA_BY_KEYS_FROM_DISTRIBUTED_MAP);
		System.out.println(ClientMenu.DELIMITER);

		System.out.println(ClientMenu.SAVE_DATA_INTO_POINTING_DISTRIBUTED_LIST);
		System.out.println(ClientMenu.SAVE_MUCH_DATA_INTO_POINTING_DISTRIBUTED_LIST);
		System.out.println(ClientMenu.LOAD_DATA_FROM_POINTING_DISTRIBUTED_LIST);
		System.out.println(ClientMenu.LOAD_TOP_DATA_FROM_POINTING_DISTRIBUTED_LIST);
		System.out.println(ClientMenu.LOAD_RANGE_DATA_FROM_POINTING_DISTRIBUTED_LIST);
		System.out.println(ClientMenu.LOAD_SPECIFIC_DATA_FROM_POINTING_DISTRIBUTED_LIST);
		System.out.println(ClientMenu.LOAD_SPECIFIC_TOP_DATA_FROM_POINTING_DISTRIBUTED_LIST);
		System.out.println(ClientMenu.LOAD_SPECIFIC_RANGE_DATA_FROM_POINTING_DISTRIBUTED_LIST);
		System.out.println(ClientMenu.DELIMITER);

		System.out.println(ClientMenu.SAVE_DATA_INTO_POINTING_DISTRIBUTED_MAP);
		System.out.println(ClientMenu.SAVE_MUCH_DATA_INTO_POINTING_DISTRIBUTED_MAP);
		System.out.println(ClientMenu.LOAD_MIN_DATA_FROM_POINTING_DISTRIBUTED_MAP);
		System.out.println(ClientMenu.LOAD_MAX_DATA_FROM_POINTING_DISTRIBUTED_MAP);
		System.out.println(ClientMenu.LOAD_DATA_FROM_POINTING_DISTRIBUTED_MAP);
		System.out.println(ClientMenu.DELIMITER);
		
		System.out.println(ClientMenu.LOAD_DATA_FROM_POST_DISTRIBUTED_MAP);
		System.out.println(ClientMenu.LOAD_DATA_BY_KEYS_FROM_POST_DISTRIBUTED_MAP);
		System.out.println(ClientMenu.DELIMITER);

		System.out.println(ClientMenu.SAVE_DATA_INTO_POINTING_DISTRIBUTED_CACHE_STORE);
		System.out.println(ClientMenu.SAVE_MUCH_DATA_INTO_POINTING_DISTRIBUTED_CACHE_STORE);
		System.out.println(ClientMenu.SAVE_ORDERED_DATA_INTO_POINTING_DISTRIBUTED_CACHE_STORE);
		System.out.println(ClientMenu.CONTAINS_KEY_FROM_POINTING_DISTRIBUTED_CACHE_STORE);
		System.out.println(ClientMenu.LOAD_MAX_FROM_POINTING_DISTRIBUTED_CACHE_STORE);
		System.out.println(ClientMenu.LOAD_DATA_BY_KEY_FROM_POINTING_DISTRIBUTED_CACHE_STORE);
		System.out.println(ClientMenu.IS_CACHE_EXISTED_FROM_POINTING_DISTRIBUTED_CACHE_STORE);
		System.out.println(ClientMenu.IS_CACHE_EMPTY_FROM_POINTING_DISTRIBUTED_CACHE_STORE);
		System.out.println(ClientMenu.LOAD_DATA_INDEX_FROM_POINTING_DISTRIBUTED_CACHE_STORE);
		System.out.println(ClientMenu.LOAD_TOP_DATA_FROM_POINTING_DISTRIBUTED_CACHE_STORE);
		System.out.println(ClientMenu.LOAD_RANGE_DATA_FROM_POINTING_DISTRIBUTED_CACHE_STORE);
		System.out.println(ClientMenu.DELIMITER);
		
		System.out.println(ClientMenu.LOAD_DATA_INDEX_FROM_POINTING_PREFETCH_LIST_STORE);
		System.out.println(ClientMenu.LOAD_TOP_DATA_FROM_POINTING_PREFETCH_LIST_STORE);
		System.out.println(ClientMenu.LOAD_RANGE_DATA_FROM_POINTING_DPREFETCH_LIST_STORE);
		System.out.println(ClientMenu.DELIMITER);

		System.out.println(ClientMenu.IS_CACHE_EXISTED_FROM_POINTING_DISTRIBUTED_READ_CACHE_STORE);
		System.out.println(ClientMenu.LOAD_TOP_DATA_FROM_POINTING_DISTRIBUTED_READ_CACHE_STORE);
		System.out.println(ClientMenu.LOAD_RANGE_DATA_FROM_POINTING_DISTRIBUTED_READ_CACHE_STORE);
		System.out.println(ClientMenu.DELIMITER);

		System.out.println(ClientMenu.PUSH_INTO_ONE_STACK_STORE);
		System.out.println(ClientMenu.POP_FROM_ONE_STACK_STORE);
		System.out.println(ClientMenu.POP_MUCH_FROM_ONE_STACK_STORE);
		System.out.println(ClientMenu.PUSH_INTO_STACK_STORE);
		System.out.println(ClientMenu.PUSH_MUCH_INTO_STACK_STORE);
		System.out.println(ClientMenu.POP_FROM_STACK_STORE);
		System.out.println(ClientMenu.POP_ALL_FROM_STACK_STORE);
		System.out.println(ClientMenu.PEEK_FROM_STACK_STORE);
		System.out.println(ClientMenu.PEEK_ALL_FROM_STACK_STORE);
		System.out.println(ClientMenu.PEEK_RANGE_FROM_STACK_STORE);
		System.out.println(ClientMenu.GET_FROM_STACK_STORE);
		System.out.println(ClientMenu.DELIMITER);

		System.out.println(ClientMenu.ENQUEUE_INTO_ONE_QUEUE_STORE);
		System.out.println(ClientMenu.DEQUEUE_FROM_ONE_QUEUE_STORE);
		System.out.println(ClientMenu.DEQUEUE_MUCH_FROM_ONE_QUEUE_STORE);
		System.out.println(ClientMenu.ENQUEUE_INTO_QUEUE_STORE);
		System.out.println(ClientMenu.ENQUEUE_MUCH_INTO_QUEUE_STORE);
		System.out.println(ClientMenu.DEQUEUE_FROM_QUEUE_STORE);
		System.out.println(ClientMenu.DEQUEUE_ALL_FROM_QUEUE_STORE);
		System.out.println(ClientMenu.PEEK_FROM_QUEUE_STORE);
		System.out.println(ClientMenu.PEEK_ALL_FROM_QUEUE_STORE);
		System.out.println(ClientMenu.PEEK_RANGE_FROM_QUEUE_STORE);
		System.out.println(ClientMenu.GET_FROM_QUEUE_STORE);
		
		System.out.println(ClientMenu.DELIMITER);

		System.out.println(ClientMenu.POP_FROM_READ_STACK_STORE);
		System.out.println(ClientMenu.POP_ALL_FROM_READ_STACK_STORE);
		System.out.println(ClientMenu.PEEK_FROM_READ_STACK_STORE);
		System.out.println(ClientMenu.PEEK_ALL_FROM_READ_STACK_STORE);
		System.out.println(ClientMenu.PEEK_RANGE_FROM_READ_STACK_STORE);
		System.out.println(ClientMenu.GET_FROM_READ_STACK_STORE);
		System.out.println(ClientMenu.DELIMITER);

		System.out.println(ClientMenu.SAVE_DATA_INTO_DISTRIBUTED_MAP_STORE);
		System.out.println(ClientMenu.SAVE_MUCH_DATA_INTO_DISTRIBUTED_MAP_STORE);
		System.out.println(ClientMenu.LOAD_DATA_FROM_DISTRIBUTED_MAP_STORE);
		System.out.println(ClientMenu.LOAD_DATA_BY_KEYS_FROM_DISTRIBUTED_MAP_STORE);
		System.out.println(ClientMenu.LOAD_DATA_KEYS_FROM_DISTRIBUTED_MAP_STORE);
		
		System.out.println(ClientMenu.DELIMITER);

		System.out.println(ClientMenu.SAVE_DATA_INTO_DISTRIBUTED_LIST);
		System.out.println(ClientMenu.SAVE_MUCH_DATA_INTO_DISTRIBUTED_LIST);
		System.out.println(ClientMenu.LOAD_DATA_FROM_DISTRIBUTED_LIST);
		System.out.println(ClientMenu.LOAD_TOP_DATA_FROM_DISTRIBUTED_LIST);
		System.out.println(ClientMenu.LOAD_RANGE_DATA_FROM_DISTRIBUTED_LIST);
		System.out.println(ClientMenu.DELIMITER);

		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}
	
	public void send(int option, boolean isTiming) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		FrontResponse response;
		switch (option)
		{
			case MenuOptions.NOTIFY:
				FrontEventer.RE().notify("I am learning cloud programming ...");
				break;
				
			case MenuOptions.REQUEST:
				response = FrontReader.RR().query("What is cloud programming?");
				System.out.println("The answer is: " + response.getAnswer());
				break;
				
			case MenuOptions.SAVE_DATA_INTO_DISTRIBUTED_MAP:
				DistributedMapClient.saveDataIntoDistributedMap();
				break;
				
			case MenuOptions.SAVE_MUCH_DATA_INTO_DISTRIBUTED_MAP:
				DistributedMapClient.saveMuchDataIntoDistributedMap();
				break;
				
			case MenuOptions.LOAD_DATA_FROM_DISTRIBUTED_MAP:
				DistributedMapClient.loadDataFromDistributedMap();
				break;

			case MenuOptions.LOAD_DATA_BY_KEYS_FROM_DISTRIBUTED_MAP:
				DistributedMapClient.loadDataByKeysFromDistributedMap();
				break;
				
			case MenuOptions.SAVE_DATA_INTO_POINTING_DISTRIBUTED_LIST:
				PointingDistributedListClient.saveDataIntoPointingDistributedList();
				break;
				
			case MenuOptions.SAVE_MUCH_DATA_INTO_POINTING_DISTRIBUTED_LIST:
				PointingDistributedListClient.saveMuchDataIntoPointingDistributedList();
				break;
				
			case MenuOptions.LOAD_DATA_FROM_POINTING_DISTRIBUTED_LIST:
				PointingDistributedListClient.loadDataFromPointingDistributedList();
				break;
				
			case MenuOptions.LOAD_TOP_DATA_FROM_POINTING_DISTRIBUTED_LIST:
				PointingDistributedListClient.loadTopDataFromPointingDistributedList();
				break;
				
			case MenuOptions.LOAD_RANGE_DATA_FROM_POINTING_DISTRIBUTED_LIST:
				PointingDistributedListClient.loadRangeDataFromPointingDistributedList();
				break;
				
			case MenuOptions.LOAD_SPECIFIC_DATA_FROM_POINTING_DISTRIBUTED_LIST:
//				DataAccessor.loadDataFromPointingDistributedList(19999);
//				CacheAccessor.loadDataFromPointingDistributedList(5);
				PointingDistributedListClient.loadDataFromPointingDistributedList(0);
				break;
				
			case MenuOptions.LOAD_SPECIFIC_TOP_DATA_FROM_POINTING_DISTRIBUTED_LIST:
//				DataAccessor.loadTopDataFromPointingDistributedList(10);
				PointingDistributedListClient.loadTopDataFromPointingDistributedList(Rand.getRandom(TestCacheConfig.SORTED_DISTRIBUTED_LIST_CACHE_SIZE));
				break;
				
			case MenuOptions.LOAD_SPECIFIC_RANGE_DATA_FROM_POINTING_DISTRIBUTED_LIST:
				PointingDistributedListClient.loadRangeDataFromPointingDistributedList(10, 100);
				break;
				
			case MenuOptions.SAVE_DATA_INTO_POINTING_DISTRIBUTED_MAP:
				DistributedPointingMapClient.savePointingIntoDistributedPointingMap();
				break;
				
			case MenuOptions.SAVE_MUCH_DATA_INTO_POINTING_DISTRIBUTED_MAP:
				DistributedPointingMapClient.savePointingsIntoDistributedPointingMap();
				break;
				
			case MenuOptions.LOAD_MIN_DATA_FROM_POINTING_DISTRIBUTED_MAP:
				DistributedPointingMapClient.loadMinPointingFromDistributedPointingMap();
				break;
				
			case MenuOptions.LOAD_MAX_DATA_FROM_POINTING_DISTRIBUTED_MAP:
				DistributedPointingMapClient.loadMaxPointingFromDistributedPointingMap();
				break;
				
			case MenuOptions.LOAD_DATA_FROM_POINTING_DISTRIBUTED_MAP:
				DistributedPointingMapClient.loadPointingFromDistributedPointingMap();
				break;
				
			case MenuOptions.LOAD_DATA_FROM_POST_DISTRIBUTED_MAP:
				PostDistributedMapClient.loadDataFromPostDistributedMap();
				break;
				
			case MenuOptions.LOAD_DATA_BY_KEYS_FROM_POST_DISTRIBUTED_MAP:
				PostDistributedMapClient.loadDataByKeysFromPostDistributedMap();
				break;
				
			case MenuOptions.SAVE_DATA_INTO_POINTING_DISTRIBUTED_CACHE_STORE:
				if (!isTiming)
				{
					PointingDistributedCacheStoreClient.saveDataIntoPointingDistributedCacheStore();
				}
				else
				{
					TimingDistributedCacheStoreClient.saveDataIntoTimingDistributedCacheStore();
				}
				break;
				
			case MenuOptions.SAVE_ALL_DATA_INTO_POINTING_DISTRIBUTED_CACHE_STORE:
				if (!isTiming)
				{
					PointingDistributedCacheStoreClient.saveDataIntoPointingsDistributedCacheStore();
				}
				else
				{
					TimingDistributedCacheStoreClient.saveDataIntoTimingsDistributedCacheStore();
				}
				break;
				
			case MenuOptions.SAVE_ORDERD_DATA_INTO_POINTING_DISTRIBUTED_CACHE_STORE:
				TimingDistributedCacheStoreClient.saveSortedDataIntoTimingsDistributedCacheStore();
				break;
				
			case MenuOptions.CONTAINS_KEY_FROM_POINTING_DISTRIBUTED_CACHE_STORE:
				if (!isTiming)
				{
					PointingDistributedCacheStoreClient.containsKey();
				}
				else
				{
					TimingDistributedCacheStoreClient.containsKey();
				}
				break;
				
			case MenuOptions.LOAD_MAX_FROM_POINTING_DISTRIBUTED_CACHE_STORE:
				if (!isTiming)
				{
					PointingDistributedCacheStoreClient.getMaxCachePointing();
				}
				else
				{
					TimingDistributedCacheStoreClient.getMaxCacheTiming();
				}
				break;
				
			case MenuOptions.LOAD_DATA_BY_KEY_FROM_POINTING_DISTRIBUTED_CACHE_STORE:
				if (!isTiming)
				{
					PointingDistributedCacheStoreClient.getCachePointingByKey();
				}
				else
				{
					TimingDistributedCacheStoreClient.getCacheTimingByKey();
				}
				break;
				
			case MenuOptions.IS_CACHE_EXISTED_FROM_POINTING_DISTRIBUTED_CACHE_STORE:
				if (!isTiming)
				{
					PointingDistributedCacheStoreClient.isCacheExisted();
				}
				else
				{
					TimingDistributedCacheStoreClient.isCacheExisted();
				}
				break;
				
			case MenuOptions.IS_CACHE_EMPTY_FROM_POINTING_DISTRIBUTED_CACHE_STORE:
				if (!isTiming)
				{
					PointingDistributedCacheStoreClient.isCacheEmpty();
				}
				else
				{
					TimingDistributedCacheStoreClient.isCacheEmpty();
				}
				break;
				
			case MenuOptions.LOAD_DATA_INDEX_FROM_POINTING_DISTRIBUTED_CACHE_STORE:
				if (!isTiming)
				{
					PointingDistributedCacheStoreClient.getCachePointingByIndex();
				}
				else
				{
					TimingDistributedCacheStoreClient.getCacheTimingByIndex();
				}
				break;
				
			case MenuOptions.LOAD_TOP_DATA_FROM_POINTING_DISTRIBUTED_CACHE_STORE:
				if (!isTiming)
				{
					PointingDistributedCacheStoreClient.getTopCachePointings();
				}
				else
				{
					TimingDistributedCacheStoreClient.getTopCacheTimings();
				}
				break;
				
			case MenuOptions.LOAD_RANGE_DATA_FROM_POINTING_DISTRIBUTED_CACHE_STORE:
				if (!isTiming)
				{
					PointingDistributedCacheStoreClient.getRangeCachePointings();
				}
				else
				{
					TimingDistributedCacheStoreClient.getRangeCacheTimings();
				}
				break;
				
			case MenuOptions.LOAD_DATA_INDEX_FROM_POINTING_PREFETCH_LIST_STORE:
				PointingPrefetchListStoreClient.getCachePointingByIndex();
				break;
				
			case MenuOptions.LOAD_TOP_DATA_FROM_POINTING_PREFETCH_LIST_STORE:
				PointingPrefetchListStoreClient.getTopCachePointings();
				break;
				
			case MenuOptions.LOAD_RANGE_DATA_FROM_POINTING_PREFETCH_LIST_STORE:
				PointingPrefetchListStoreClient.getRangeCachePointings();
				break;
				
			case MenuOptions.IS_CACHE_EXISTED_FROM_POINTING_DISTRIBUTED_READ_CACHE_STORE:
				PointingDistributedReadCacheStoreClient.isCacheExisted();
				break;
				
			case MenuOptions.LOAD_TOP_DATA_FROM_POINTING_DISTRIBUTED_READ_CACHE_STORE:
				PointingDistributedReadCacheStoreClient.getTopCachePointings();
				break;
				
			case MenuOptions.LOAD_RANGE_DATA_FROM_POINTING_DISTRIBUTED_READ_CACHE_STORE:
				PointingDistributedReadCacheStoreClient.getRangeCachePointings();
				break;
				
			case MenuOptions.PUSH_INTO_ONE_STACK_STORE:
				DistributedStackStoreClient.pushDataIntoOneStackStore();
				break;
				
			case MenuOptions.POP_FROM_ONE_STACK_STORE:
				DistributedStackStoreClient.popSingleFromOneStack();
				break;
				
			case MenuOptions.POP_MUCH_FROM_ONE_STACK_STORE:
				DistributedStackStoreClient.popMuchFromOneStack();
				break;
				
			case MenuOptions.PUSH_INTO_STACK_STORE:
				DistributedStackStoreClient.pushDataIntoStackStore();
				break;
				
			case MenuOptions.PUSH_MUCH_INTO_STACK_STORE:
				DistributedStackStoreClient.pushMuchDataIntoStackStore();
				break;
				
			case MenuOptions.POP_FROM_STACK_STORE:
				DistributedStackStoreClient.popSingle();
				break;
				
			case MenuOptions.POP_ALL_FROM_STACK_STORE:
				DistributedStackStoreClient.popMuch();
				break;
				
			case MenuOptions.PEEK_FROM_STACK_STORE:
				DistributedStackStoreClient.peekSingle();
				break;
				
			case MenuOptions.PEEK_ALL_FROM_STACK_STORE:
				DistributedStackStoreClient.peekMuch();
				break;
				
			case MenuOptions.PEEK_RANGE_FROM_STACK_STORE:
				DistributedStackStoreClient.peekRange();
				break;
				
			case MenuOptions.GET_FROM_STACK_STORE:
				DistributedStackStoreClient.get();
				break;
				
			case MenuOptions.ENQUEUE_INTO_ONE_QUEUE_STORE:
				DistributedQueueStoreClient.enqueueDataIntoOneQueueStore();
				break;
				
			case MenuOptions.DEQUEUE_FROM_ONE_QUEUE_STORE:
				DistributedQueueStoreClient.dequeueSingleFromOneQueue();
				break;
				
			case MenuOptions.DEQUEUE_MUCH_FROM_ONE_QUEUE_STORE:
				DistributedQueueStoreClient.dequeueMuchFromOneQueue();
				break;
				
			case MenuOptions.ENQUEUE_INTO_QUEUE_STORE:
				DistributedQueueStoreClient.enqueueDataIntoQueueStore();
				break;
				
			case MenuOptions.ENQUEUE_MUCH_INTO_QUEUE_STORE:
				DistributedQueueStoreClient.enqueueMuchDataIntoQueueStore();
				break;
				
			case MenuOptions.DEQUEUE_FROM_QUEUE_STORE:
				DistributedQueueStoreClient.dequeueSingle();
				break;
				
			case MenuOptions.DEQUEUE_ALL_FROM_QUEUE_STORE:
				DistributedQueueStoreClient.dequeueMuch();
				break;
				
			case MenuOptions.PEEK_FROM_QUEUE_STORE:
				DistributedQueueStoreClient.peekSingle();
				break;
				
			case MenuOptions.PEEK_ALL_FROM_QUEUE_STORE:
				DistributedQueueStoreClient.peekMuch();
				break;
				
			case MenuOptions.PEEK_RANGE_FROM_QUEUE_STORE:
				DistributedQueueStoreClient.peekRange();
				break;
				
			case MenuOptions.GET_FROM_QUEUE_STORE:
				DistributedQueueStoreClient.get();
				break;
				
			case MenuOptions.POP_FROM_READ_STACK_STORE:
				DistributedReadStackStoreClient.popSingle();
				break;
				
			case MenuOptions.POP_ALL_FROM_READ_STACK_STORE:
				DistributedReadStackStoreClient.popMuch();
				break;
				
			case MenuOptions.PEEK_FROM_READ_STACK_STORE:
				DistributedReadStackStoreClient.peekSingle();
				break;
				
			case MenuOptions.PEEK_ALL_FROM_READ_STACK_STORE:
				DistributedReadStackStoreClient.peekMuch();
				break;
				
			case MenuOptions.PEEK_RANGE_FROM_READ_STACK_STORE:
				DistributedReadStackStoreClient.peekRange();
				break;
				
			case MenuOptions.GET_FROM_READ_STACK_STORE:
				DistributedReadStackStoreClient.get();
				break;
				
			case MenuOptions.SAVE_DATA_INTO_DISTRIBUTED_MAP_STORE:
				DistributedMapStoreClient.saveDataIntoDistributedMapStore();
				break;
				
			case MenuOptions.SAVE_MUCH_DATA_INTO_DISTRIBUTED_MAP_STORE:
				DistributedMapStoreClient.saveMuchDataIntoDistributedMapStore();
				break;
				
			case MenuOptions.LOAD_DATA_FROM_DISTRIBUTED_MAP_STORE:
				DistributedMapStoreClient.loadDataFromDistributedMapStore();
				break;

			case MenuOptions.LOAD_DATA_BY_KEYS_FROM_DISTRIBUTED_MAP_STORE:
				DistributedMapStoreClient.loadDataByKeysFromDistributedMapStore();
				break;
				
			case MenuOptions.LOAD_DATA_KEYS_FROM_DISTRIBUTED_MAP_STORE:
				DistributedMapStoreClient.loadDataKeysFromDistributedMapStore();
				break;
				
			case MenuOptions.SAVE_DATA_INTO_DISTRIBUTED_LIST:
				DistributedListClient.saveUKIntoDistributedList();
				break;
				
			case MenuOptions.SAVE_MUCH_DATA_INTO_DISTRIBUTED_LIST:
				DistributedListClient.saveMuchUKIntoDistributedList();
				break;
				
			case MenuOptions.LOAD_DATA_FROM_DISTRIBUTED_LIST:
				DistributedListClient.loadDataFromDistributedList();
				break;
				
			case MenuOptions.LOAD_TOP_DATA_FROM_DISTRIBUTED_LIST:
				DistributedListClient.loadTopDataFromDistributedList();
				break;
				
			case MenuOptions.LOAD_RANGE_DATA_FROM_DISTRIBUTED_LIST:
				DistributedListClient.loadRangeDataFromDistributedList();
				break;

			case MenuOptions.QUIT:
				break;
		}
	}

}
