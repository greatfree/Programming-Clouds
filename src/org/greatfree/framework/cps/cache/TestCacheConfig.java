package org.greatfree.framework.cps.cache;

// Created: 07/09/2018, Bing Li
public class TestCacheConfig
{
//	public final static int CACHE_THREAD_POOL_SIZE = 100;
	public final static int CACHE_THREAD_POOL_SIZE = 500;
	
	public final static String DISTRIBUTED_MAP_KEY = "DistributedMap";
	public final static int DISTRIBUTED_MAP_CACHE_SIZE = 100;
//	public final static int DISTRIBUTED_MAP_CACHE_SIZE = 200;
//	public final static int DISTRIBUTED_MAP_CACHE_SIZE = 2000;
//	public final static int DISTRIBUTED_MAP_OFF_HEAP_SIZE_IN_MB = 5;
//	public final static int DISTRIBUTED_MAP_OFF_HEAP_SIZE_IN_MB = 2;
	public final static int DISTRIBUTED_MAP_OFF_HEAP_SIZE_IN_MB = 1;
//	public final static int DISTRIBUTED_MAP_OFF_HEAP_SIZE_IN_MB = 1;
//	public final static int DISTRIBUTED_MAP_DISK_SIZE_IN_MB = 10;
//	public final static int DISTRIBUTED_MAP_DISK_SIZE_IN_MB = 3;
	public final static int DISTRIBUTED_MAP_DISK_SIZE_IN_MB = 2;
	public final static long DISTRIBUTED_MAP_POSTFETCH_TIMEOUT = 15000;
	
	public final static String DISTRIBUTED_LIST_KEY = "DistributedList";
	public final static int DISTRIBUTED_LIST_CACHE_SIZE = 100;
	public final static int DISTRIBUTED_LIST_OFF_HEAP_SIZE_IN_MB = 1;
	public final static int DISTRIBUTED_LIST_DISK_SIZE_IN_MB = 2;
	public final static int DISTRIBUTED_LIST_PREFETCHING_SIZE = 30;
	public final static long DISTRIBUTED_LIST_POSTFETCHING_TIMEOUT = 15000;
	public final static int DISTRIBUTED_LIST_PREFETCH_THRESHOLD_SIZE = 20;

	public final static String DISTRIBUTED_MAP_STORE_KEY = "DistributedMapStore";
	public final static int DISTRIBUTED_MAP_STORE_SIZE = 1000;
	public final static int DISTRIBUTED_MAP_STORE_CACHE_SIZE = 100;
	public final static int DISTRIBUTED_MAP_STORE_OFF_HEAP_SIZE_IN_MB = 1;
	public final static int DISTRIBUTED_MAP_STORE_DISK_SIZE_IN_MB = 2;
	public final static long DISTRIBUTED_MAP_STORE_POSTFETCH_TIMEOUT = 15000;

	public final static String POST_DISTRIBUTED_MAP_KEY = "PostDistributedMap";
	public final static int POST_DISTRIBUTED_MAP_CACHE_SIZE = 100;
	public final static int POST_DISTRIBUTED_MAP_OFF_HEAP_SIZE_IN_MB = 1;
	public final static int POST_DISTRIBUTED_MAP_DISK_SIZE_IN_MB = 2;
	public final static long POST_DISTRIBUTED_MAP_POSTFETCH_TIMEOUT = 15000;

	public final static String TERMINAL_MAP_KEY = "TerminalMap";
	public final static int TERMINAL_MAP_CACHE_SIZE = 100;
//	public final static int TERMINAL_MAP_OFF_HEAP_SIZE_IN_MB = 5;
//	public final static int TERMINAL_MAP_OFF_HEAP_SIZE_IN_MB = 2;
//	public final static int TERMINAL_MAP_OFF_HEAP_SIZE_IN_MB = 1;
	public final static int TERMINAL_MAP_OFF_HEAP_SIZE_IN_MB = 50;
//	public final static int TERMINAL_MAP_DISK_SIZE_IN_MB = 10;
//	public final static int TERMINAL_MAP_DISK_SIZE_IN_MB = 3;
//	public final static int TERMINAL_MAP_DISK_SIZE_IN_MB = 2;
	public final static int TERMINAL_MAP_DISK_SIZE_IN_MB = 100;
	public final static String TERMINAL_MAP_STORE = "TerminalMapStore";
	public final static int TERMINAL_MAP_EVICTED_COUNT = 300;

	public final static String TERMINAL_LIST_KEY = "TerminalList";
	public final static int TERMINAL_LIST_CACHE_SIZE = 100;
	public final static int TERMINAL_LIST_OFF_HEAP_SIZE_IN_MB = 50;
	public final static int TERMINAL_LIST_DISK_SIZE_IN_MB = 100;
	public final static String TERMINAL_LIST_STORE = "TerminalMapList";
	public final static int TERMINAL_LIST_EVICTED_COUNT = 300;

	public final static String SORTED_DISTRIBUTED_LIST_KEY = "SortedDistributedList";
//	public final static int POINTING_DISTRIBUTED_LIST_CACHE_SIZE = 10000;
	public final static int SORTED_DISTRIBUTED_LIST_CACHE_SIZE = 100;
//	public final static int POINTING_DISTRIBUTED_LIST_CACHE_SIZE = 1000;
//	public final static long POINTING_DISTRIBUTED_LIST_OFFHEAP_SIZE_IN_MB = 50;
//	public final static long POINTING_DISTRIBUTED_LIST_OFFHEAP_SIZE_IN_MB = 5;
	public final static int SORTED_DISTRIBUTED_LIST_OFFHEAP_SIZE_IN_MB = 2;
//	public final static long POINTING_DISTRIBUTED_LIST_OFFHEAP_SIZE_IN_MB = 1;
//	public final static long POINTING_DISTRIBUTED_LIST_DISK_SIZE_IN_MB = 500;
//	public final static long POINTING_DISTRIBUTED_LIST_DISK_SIZE_IN_MB = 10;
	public final static int SORTED_DISTRIBUTED_LIST_DISK_SIZE_IN_MB = 3;
//	public final static long POINTING_DISTRIBUTED_LIST_DISK_SIZE_IN_MB = 2;
//	public final static int POINTING_DISTRIBUTED_LIST_PREFETCHING_SIZE = 200;
	public final static int SORTED_DISTRIBUTED_LIST_PREFETCHING_SIZE = 30;
//	public final static int POINTING_DISTRIBUTED_LIST_POSTFETCHING_SIZE = 200;
//	public final static long POINTING_DISTRIBUTED_LIST_POSTFETCHING_TIMEOUT = 5000;
	public final static long SORTED_DISTRIBUTED_LIST_POSTFETCHING_TIMEOUT = 15000;
//	public final static int POINTING_DISTRIBUTED_LIST_PREFETCH_THRESHOLD_SIZE = 200;
	public final static int SORTED_DISTRIBUTED_LIST_PREFETCH_THRESHOLD_SIZE = 20;
	public final static int SORTED_DISTRIBUTED_LIST_SORT_SIZE = 100;
	
	public final static String SORTED_TERMINAL_LIST_CACHE_KEY = "SortedTerminalList";
//	public final static int POINTING_TERMINAL_LIST_CACHE_SIZE = 10000;
	public final static int SORTED_TERMINAL_LIST_CACHE_SIZE = 20000;
	public final static int SORTED_TERMINAL_LIST_CACHE_OFFHEAP_SIZE_IN_MB = 50;
	public final static int SORTED_TERMINAL_LIST_CACHE_DISK_SIZE_IN_MB = 500;
	public final static String SORTED_TERMINAL_LIST_STORE = "SortedTerminalListStore";
	public final static int POINTING_TERMINAL_LIST_EVICTED_COUNT_ALERT = 2000;
	public final static int SORTED_TERMINAL_LIST_SORT_SIZE = 200;

	public final static String SORTED_DISTRIBUTED_MAP_KEY = "SortedDistributedMap";
	public final static int SORTED_DISTRIBUTED_MAP_CACHE_SIZE = 100;
	public final static int SORTED_DISTRIBUTED_MAP_OFFHEAP_SIZE_IN_MB = 1;
	public final static int SORTED_DISTRIBUTED_MAP_DISK_SIZE_IN_MB = 2;
	public final static long SORTED_DISTRIBUTED_MAP_POSTFETCHING_TIMEOUT = 15000;
	public final static int SORTED_DISTRIBUTED_MAP_SORT_SIZE = 100;
	
	public final static String SORTED_DISTRIBUTED_CACHE_STORE_KEY = "SortedDistributedCacheStore";
	public final static int SORTED_DISTRIBUTED_CACHE_STORE_SIZE = 1000;
	public final static int SORTED_DISTRIBUTED_CACHE_SIZE = 100;
	public final static int SORTED_DISTRIBUTED_CACHE_STORE_OFFHEAP_SIZE = 2;
	public final static int SORTED_DISTRIBUTED_CACHE_STORE_DISK_SIZE = 3;
	public final static int SORTED_DISTRIBUTED_CACHE_STORE_PREFETCHING_SIZE = 30;
	public final static long SORTED_DISTRIBUTED_CACHE_STORE_POSTFETCH_TIMEOUT = 15000;
	public final static int DISTRIBUTED_CACHE_STORE_PREFETCH_THRESHOLD_SIZE = 20;
	public final static int SORTED_DISTRIBUTED_CACHE_STORE_SORT_SIZE = 100;

	public final static String TIMING_DISTRIBUTED_CACHE_STORE_KEY = "TimingDistributedCacheStore";
	public final static int TIMING_DISTRIBUTED_CACHE_STORE_SIZE = 1000;
	public final static int TIMING_DISTRIBUTED_CACHE_SIZE = 100;
	public final static int TIMING_DISTRIBUTED_CACHE_STORE_OFFHEAP_SIZE = 2;
	public final static int TIMING_DISTRIBUTED_CACHE_STORE_DISK_SIZE = 3;
	public final static int TIMING_DISTRIBUTED_CACHE_STORE_PREFETCHING_SIZE = 30;
	public final static long TIMING_DISTRIBUTED_CACHE_STORE_POSTFETCH_TIMEOUT = 15000;
	public final static int TIMING_DISTRIBUTED_CACHE_STORE_SORT_SIZE = 100;

	public final static String SORTED_TERMINAL_MAP_STORE_KEY = "SortedTerminalMapStore";
	public final static int SORTED_TERMINAL_MAP_STORE_SIZE = 30000;
	public final static int SORTED_TERMINAL_MAP_STORE_CACHE_SIZE = 100;
	public final static int SORTED_TERMINAL_MAP_STORE_OFFHEAP_SIZE_IN_MB = 50;
	public final static int SORTED_TERMINAL_MAP_STORE_DISK_SIZE_IN_MB  = 500;
	public final static String SORTED_TERMINAL_MAP_STORE = "SortedTerminalMapStore";
	public final static int SORTED_TERMINAL_MAP_STORE_EVICTED_COUNT_ALERT = 2000;
	public final static int SORTED_TERMINAL_MAP_STORE_SORT_SIZE = 200;

	public final static String SORTED_TERMINAL_LIST_STORE_KEY = "SortedTerminalListStore";
	public final static int SORTED_TERMINAL_LIST_STORE_SIZE = 30000;
	public final static int SORTED_TERMINAL_LIST_STORE_CACHE_SIZE = 100;
	public final static int SORTED_TERMINAL_LIST_STORE_OFFHEAP_SIZE_IN_MB = 50;
	public final static int SORTED_TERMINAL_LIST_STORE_DISK_SIZE_IN_MB  = 500;
	public final static String SORTED_TERMINAL_LIST_DB_STORE = "SortedTerminalListStore";
	public final static int SORTED_TERMINAL_LIST_STORE_EVICTED_COUNT_ALERT = 2000;
	public final static int SORTED_TERMINAL_LIST_STORE_SORT_SIZE = 200;

	public final static String SORTED_PREFETCH_LIST_STORE_KEY = "SortedPrefetchListStore";
	public final static int SORTED_PREFETCH_LIST_STORE_SIZE = 1000;
	public final static int SORTED_PREFETCH_LIST_CACHE_SIZE = 100;
	public final static int SORTED_PREFETCH_LIST_STORE_OFFHEAP_SIZE = 2;
	public final static int SORTED_PREFETCH_LIST_STORE_DISK_SIZE = 3;
	public final static int SORTED_PREFETCH_LIST_STORE_PREFETCHING_SIZE = 30;
	public final static long SORTED_PREFETCH_LIST_STORE_POSTFETCH_TIMEOUT = 15000;
	public final static int SORTED_PREFETCH_LIST_STORE_PREFETCH_THRESHOLD_SIZE = 20;
	public final static int SORTED_PREFETCH_LIST_STORE_SORT_SIZE = 100;
	
	public final static String DISTRIBUTED_STACK_STORE_KEY = "DistributedStackStore";
	public final static int DISTRIBUTED_STACK_STORE_SIZE = 1000;
	public final static int DISTRIBUTED_STACK_CACHE_SIZE = 100;
	public final static int DISTRIBUTED_STACK_STORE_OFFHEAP_SIZE = 2;
	public final static int DISTRIBUTED_STACK_STORE_DISK_SIZE = 3;
	public final static long DISTRIBUTED_STACK_STORE_POSTFETCH_TIMEOUT = 15000;
	public final static int POINTING_DISTRIBUTED_STACK_STORE_PREFETCH_THRESHOLD_SIZE = 20;
	public final static int DISTRIBUTED_STACK_PREFETCHING_SIZE = 30;
	
	public final static String TERMINAL_STACK_STORE_KEY = "TerminalStackStore";
	public final static int TERMINAL_STACK_STORE_SIZE = 30000;
	public final static int TERMINAL_STACK_STORE_CACHE_SIZE = 100;
	public final static int TERMINAL_STACK_STORE_OFFHEAP_SIZE_IN_MB = 50;
	public final static int TERMINAL_STACK_STORE_DISK_SIZE_IN_MB  = 500;
	public final static String TERMINAL_STACK_STORE = "TerminalStackStore";
	public final static int TERMINAL_STACK_STORE_EVICTED_COUNT_ALERT = 2000;
	
	public final static String DISTRIBUTED_QUEUE_STORE_KEY = "DistributedQueueStore";
	public final static int DISTRIBUTED_QUEUE_STORE_SIZE = 1000;
	public final static int DISTRIBUTED_QUEUE_CACHE_SIZE = 100;
	public final static int DISTRIBUTED_QUEUE_STORE_OFFHEAP_SIZE = 2;
	public final static int DISTRIBUTED_QUEUE_STORE_DISK_SIZE = 3;
	public final static long DISTRIBUTED_QUEUE_STORE_POSTFETCH_TIMEOUT = 15000;
	public final static int POINTING_DISTRIBUTED_QUEUE_STORE_PREFETCH_THRESHOLD_SIZE = 20;
	public final static int DISTRIBUTED_QUEUE_PREFETCHING_SIZE = 30;
	
	public final static String TERMINAL_QUEUE_STORE_KEY = "TerminalQueueStore";
	public final static int TERMINAL_QUEUE_STORE_SIZE = 30000;
	public final static int TERMINAL_QUEUE_STORE_CACHE_SIZE = 100;
	public final static int TERMINAL_QUEUE_STORE_OFFHEAP_SIZE_IN_MB = 50;
	public final static int TERMINAL_QUEUE_STORE_DISK_SIZE_IN_MB  = 500;
	public final static String TERMINAL_QUEUE_STORE = "TerminalStackStore";
	public final static int TERMINAL_QUEUE_STORE_EVICTED_COUNT_ALERT = 2000;

	public final static String TIMING_TERMINAL_MAP_STORE_KEY = "TimingTerminalMapStore";
	public final static int TIMING_TERMINAL_MAP_STORE_SIZE = 30000;
	public final static int TIMING_TERMINAL_MAP_STORE_CACHE_SIZE = 100;
	public final static int TIMING_TERMINAL_MAP_STORE_OFFHEAP_SIZE_IN_MB = 50;
	public final static int TIMING_TERMINAL_MAP_STORE_DISK_SIZE_IN_MB  = 500;
	public final static String TIMING_TERMINAL_MAP_STORE = "TimingTerminalMapStore";
	public final static int TIMING_TERMINAL_MAP_STORE_EVICTED_COUNT_ALERT = 2000;
	public final static int TIMING_TERMINAL_MAP_STORE_SORT_SIZE = 200;

	public final static String DISTRIBUTED_READ_STACK_STORE_KEY = "DistributedReadStackStore";
	public final static int DISTRIBUTED_READ_STACK_STORE_SIZE = 1000;
	public final static int DISTRIBUTED_READ_STACK_CACHE_SIZE = 100;
	public final static int DISTRIBUTED_READ_STACK_STORE_OFFHEAP_SIZE = 2;
	public final static int DISTRIBUTED_READ_STACK_STORE_DISK_SIZE = 3;
	public final static long DISTRIBUTED_READ_STACK_STORE_POSTFETCH_TIMEOUT = 15000;
	public final static int POINTING_DISTRIBUTED_READ_STACK_STORE_PREFETCH_THRESHOLD_SIZE = 20;
	public final static int DISTRIBUTED_READ_STACK_PREFETCHING_SIZE = 30;
	
	public final static String SORTED_DISTRIBUTED_READ_CACHE_STORE_KEY = "SortedDistributedReadCacheStore";
	public final static int SORTED_DISTRIBUTED_READ_CACHE_STORE_SIZE = 1000;
	public final static int SORTED_DISTRIBUTED_READ_CACHE_SIZE = 100;
	public final static int SORTED_DISTRIBUTED_READ_CACHE_STORE_OFFHEAP_SIZE = 2;
	public final static int SORTED_DISTRIBUTED_READ_CACHE_STORE_DISK_SIZE = 3;
	public final static int SORTED_DISTRIBUTED_READ_CACHE_STORE_PREFETCHING_SIZE = 30;
	public final static long SORTED_DISTRIBUTED_READ_CACHE_STORE_POSTFETCH_TIMEOUT = 15000;
	public final static int DISTRIBUTED_READ_CACHE_STORE_PREFETCH_THRESHOLD_SIZE = 20;
	public final static int SORTED_DISTRIBUTED_READ_CACHE_STORE_SORT_SIZE = 100;

	public final static String TERMINAL_MAP_STORE_KEY = "TerminalMapStore";
	public final static int TERMINAL_MAP_STORE_TOTAL_SIZE = 1000;
	public final static int TERMINAL_MAP_STORE_CACHE_SIZE = 100;
	public final static int TERMINAL_MAP_STORE_OFF_HEAP_SIZE_IN_MB = 50;
	public final static int TERMINAL_MAP_STORE_DISK_SIZE_IN_MB = 100;
	public final static String TERMINAL_MAP_DB_STORE = "TerminalMapDBStore";
	public final static int TERMINAL_MAP_STORE_EVICTED_COUNT = 300;
}
