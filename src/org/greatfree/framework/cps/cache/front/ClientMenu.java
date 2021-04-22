package org.greatfree.framework.cps.cache.front;

// Created: 07/06/2018, Bing Li
class ClientMenu
{
	public final static String TAB = "	";
	public final static String DELIMITER = "-----------------------------------------------";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	public final static String NOTIFY = ClientMenu.TAB + "1) Notify";
	public final static String REQUEST = ClientMenu.TAB + "2) Query";

	public final static String SAVE_DATA_INTO_DISTRIBUTED_MAP = ClientMenu.TAB + "3) Save My Data into DistributedMap";
	public final static String SAVE_MUCH_DATA_INTO_DISTRIBUTED_MAP = ClientMenu.TAB + "4) Save Much My Data into DistributedMap";
	public final static String LOAD_DATA_FROM_DISTRIBUTED_MAP = ClientMenu.TAB + "5) Load My Data from DistributedMap";
	public final static String LOAD_DATA_BY_KEYS_FROM_DISTRIBUTED_MAP = ClientMenu.TAB + "6) Load My Data by keys from DistributedMap";

	public final static String SAVE_DATA_INTO_POINTING_DISTRIBUTED_LIST = ClientMenu.TAB + "7) Save My Pointing into PointingDistributedList";
	public final static String SAVE_MUCH_DATA_INTO_POINTING_DISTRIBUTED_LIST = ClientMenu.TAB + "8) Save My Pointings into PointingDistributedList";
	public final static String LOAD_DATA_FROM_POINTING_DISTRIBUTED_LIST = ClientMenu.TAB + "9) Load My Pointing from PointingDistributedList";
	public final static String LOAD_TOP_DATA_FROM_POINTING_DISTRIBUTED_LIST = ClientMenu.TAB + "10) Load TOP My Pointing from PointingDistributedList";
	public final static String LOAD_RANGE_DATA_FROM_POINTING_DISTRIBUTED_LIST = ClientMenu.TAB + "11) Load RANGE My Pointing from PointingDistributedList";
	public final static String LOAD_SPECIFIC_DATA_FROM_POINTING_DISTRIBUTED_LIST = ClientMenu.TAB + "12) Load Specific My Pointing from PointingDistributedList";
	public final static String LOAD_SPECIFIC_TOP_DATA_FROM_POINTING_DISTRIBUTED_LIST = ClientMenu.TAB + "13) Load Specific Top My Pointing from PointingDistributedList";
	public final static String LOAD_SPECIFIC_RANGE_DATA_FROM_POINTING_DISTRIBUTED_LIST = ClientMenu.TAB + "14) Load Specific Range My Pointing from PointingDistributedList";

	public final static String SAVE_DATA_INTO_POINTING_DISTRIBUTED_MAP = ClientMenu.TAB + "15) Save My Pointing into PointingDistributedMap";
	public final static String SAVE_MUCH_DATA_INTO_POINTING_DISTRIBUTED_MAP = ClientMenu.TAB + "16) Save My Pointings into PointingDistributedMap";
	public final static String LOAD_MIN_DATA_FROM_POINTING_DISTRIBUTED_MAP = ClientMenu.TAB + "17) Load MIN My Pointing from PointingDistributedMap";
	public final static String LOAD_MAX_DATA_FROM_POINTING_DISTRIBUTED_MAP = ClientMenu.TAB + "18) Load MAX My Pointing from PointingDistributedMap";
	public final static String LOAD_DATA_FROM_POINTING_DISTRIBUTED_MAP = ClientMenu.TAB + "19) Load My Pointing from PointingDistributedMap";

	public final static String LOAD_DATA_FROM_POST_DISTRIBUTED_MAP = ClientMenu.TAB + "20) Load My Data from PostDistributedMap";
	public final static String LOAD_DATA_BY_KEYS_FROM_POST_DISTRIBUTED_MAP = ClientMenu.TAB + "21) Load My Data by keys from PostDistributedMap";

	public final static String SAVE_DATA_INTO_POINTING_DISTRIBUTED_CACHE_STORE = ClientMenu.TAB + "22) Save Data into PointingDistributedCacheStore";
	public final static String SAVE_MUCH_DATA_INTO_POINTING_DISTRIBUTED_CACHE_STORE = ClientMenu.TAB + "23) Save Much Data into PointingDistributedCacheStore";
	public final static String SAVE_ORDERED_DATA_INTO_POINTING_DISTRIBUTED_CACHE_STORE = ClientMenu.TAB + "24) Save Ordered Data into PointingDistributedCacheStore";
	public final static String CONTAINS_KEY_FROM_POINTING_DISTRIBUTED_CACHE_STORE = ClientMenu.TAB + "25) Contains Key from PointingDistributedCacheStore";
	public final static String LOAD_MAX_FROM_POINTING_DISTRIBUTED_CACHE_STORE = ClientMenu.TAB + "26) Load Max Data from PointingDistributedCacheStore";
	public final static String LOAD_DATA_BY_KEY_FROM_POINTING_DISTRIBUTED_CACHE_STORE = ClientMenu.TAB + "27) Load Data by Key from PointingDistributedCacheStore";
	public final static String IS_CACHE_EXISTED_FROM_POINTING_DISTRIBUTED_CACHE_STORE = ClientMenu.TAB + "28) Is Cache existed from PointingDistributedCacheStore";
	public final static String IS_CACHE_EMPTY_FROM_POINTING_DISTRIBUTED_CACHE_STORE = ClientMenu.TAB + "29) Is Cache empty from PointingDistributedCacheStore";
	public final static String LOAD_DATA_INDEX_FROM_POINTING_DISTRIBUTED_CACHE_STORE = ClientMenu.TAB + "30) Load Data by Index from PointingDistributedCacheStore";
	public final static String LOAD_TOP_DATA_FROM_POINTING_DISTRIBUTED_CACHE_STORE = ClientMenu.TAB + "31) Load Top Data from PointingDistributedCacheStore";
	public final static String LOAD_RANGE_DATA_FROM_POINTING_DISTRIBUTED_CACHE_STORE = ClientMenu.TAB + "32) Load Range Data from PointingDistributedCacheStore";

	public final static String LOAD_DATA_INDEX_FROM_POINTING_PREFETCH_LIST_STORE = ClientMenu.TAB + "33) Load Data by Index from PointingPrefetchListStore";
	public final static String LOAD_TOP_DATA_FROM_POINTING_PREFETCH_LIST_STORE = ClientMenu.TAB + "34) Load Top Data from PointingPrefetchListStore";
	public final static String LOAD_RANGE_DATA_FROM_POINTING_DPREFETCH_LIST_STORE = ClientMenu.TAB + "35) Load Range Data from PointingPrefetchListStore";

	public final static String IS_CACHE_EXISTED_FROM_POINTING_DISTRIBUTED_READ_CACHE_STORE = ClientMenu.TAB + "36) Is Cache existed from PointingDistributedReadCacheStore";
	public final static String LOAD_TOP_DATA_FROM_POINTING_DISTRIBUTED_READ_CACHE_STORE = ClientMenu.TAB + "37) Load Top Data from PointingDistributedReadCacheStore";
	public final static String LOAD_RANGE_DATA_FROM_POINTING_DISTRIBUTED_READ_CACHE_STORE = ClientMenu.TAB + "38) Load Range Data from PointingDistributedReadCacheStore";

	public final static String PUSH_INTO_ONE_STACK_STORE = ClientMenu.TAB + "39) Push into One DistributedStackStore";
	public final static String POP_FROM_ONE_STACK_STORE = ClientMenu.TAB + "40) Pop from One DistributedStackStore";
	public final static String POP_MUCH_FROM_ONE_STACK_STORE = ClientMenu.TAB + "41) Pop much from One DistributedStackStore";
	public final static String PUSH_INTO_STACK_STORE = ClientMenu.TAB + "42) Push into DistributedStackStore";
	public final static String PUSH_MUCH_INTO_STACK_STORE = ClientMenu.TAB + "43) Push much data into DistributedStackStore";
	public final static String POP_FROM_STACK_STORE = ClientMenu.TAB + "44) Pop from DistributedStackStore";
	public final static String POP_ALL_FROM_STACK_STORE = ClientMenu.TAB + "45) Pop all from DistributedStackStore";
	public final static String PEEK_FROM_STACK_STORE = ClientMenu.TAB + "46) Peek from DistributedStackStore";
	public final static String PEEK_ALL_FROM_STACK_STORE = ClientMenu.TAB + "47) Peek all from DistributedStackStore";
	public final static String PEEK_RANGE_FROM_STACK_STORE = ClientMenu.TAB + "48) Peek range from DistributedStackStore";
	public final static String GET_FROM_STACK_STORE = ClientMenu.TAB + "49) Get from DistributedStackStore";

	public final static String ENQUEUE_INTO_ONE_QUEUE_STORE = ClientMenu.TAB + "50) Enqueue into One DistributedQueueStore";
	public final static String DEQUEUE_FROM_ONE_QUEUE_STORE = ClientMenu.TAB + "51) Dequeue from One DistributedQueueStore";
	public final static String DEQUEUE_MUCH_FROM_ONE_QUEUE_STORE = ClientMenu.TAB + "52) Dequeue much from One DistributedQueueStore";
	public final static String ENQUEUE_INTO_QUEUE_STORE = ClientMenu.TAB + "53) Enqueue into DistributedQueueStore";
	public final static String ENQUEUE_MUCH_INTO_QUEUE_STORE = ClientMenu.TAB + "54) Enqueue much data into DistributedQueueStore";
	public final static String DEQUEUE_FROM_QUEUE_STORE = ClientMenu.TAB + "55) Dequeue from DistributedQueueStore";
	public final static String DEQUEUE_ALL_FROM_QUEUE_STORE = ClientMenu.TAB + "56) Dequeue all from DistributedQueueStore";
	public final static String PEEK_FROM_QUEUE_STORE = ClientMenu.TAB + "57) Peek from DistributedQueueStore";
	public final static String PEEK_ALL_FROM_QUEUE_STORE = ClientMenu.TAB + "58) Peek all from DistributedQueueStore";
	public final static String PEEK_RANGE_FROM_QUEUE_STORE = ClientMenu.TAB + "59) Peek range from DistributedQueueStore";
	public final static String GET_FROM_QUEUE_STORE = ClientMenu.TAB + "60) Get range from DistributedQueueStore";

	public final static String POP_FROM_READ_STACK_STORE = ClientMenu.TAB + "61) Pop from DistributedReadStackStore";
	public final static String POP_ALL_FROM_READ_STACK_STORE = ClientMenu.TAB + "62) Pop all from DistributedReadStackStore";
	public final static String PEEK_FROM_READ_STACK_STORE = ClientMenu.TAB + "63) Peek from DistributedReadStackStore";
	public final static String PEEK_ALL_FROM_READ_STACK_STORE = ClientMenu.TAB + "64) Peek all from DistributedReadStackStore";
	public final static String PEEK_RANGE_FROM_READ_STACK_STORE = ClientMenu.TAB + "65) Peek range from DistributedReadStackStore";
	public final static String GET_FROM_READ_STACK_STORE = ClientMenu.TAB + "66) Get range from DistributedReadStackStore";

	public final static String SAVE_DATA_INTO_DISTRIBUTED_MAP_STORE = ClientMenu.TAB + "67) Save My Data into DistributedMapStore";
	public final static String SAVE_MUCH_DATA_INTO_DISTRIBUTED_MAP_STORE = ClientMenu.TAB + "68) Save Much My Data into DistributedMapStore";
	public final static String LOAD_DATA_FROM_DISTRIBUTED_MAP_STORE = ClientMenu.TAB + "69) Load My Data from DistributedMapStore";
	public final static String LOAD_DATA_BY_KEYS_FROM_DISTRIBUTED_MAP_STORE = ClientMenu.TAB + "70) Load My Data by keys from DistributedMapStore";
	public final static String LOAD_DATA_KEYS_FROM_DISTRIBUTED_MAP_STORE = ClientMenu.TAB + "71) Load My Data Keys from DistributedMapStore";

	public final static String SAVE_DATA_INTO_DISTRIBUTED_LIST = ClientMenu.TAB + "72) Save My UK into DistributedList";
	public final static String SAVE_MUCH_DATA_INTO_DISTRIBUTED_LIST = ClientMenu.TAB + "73) Save My UKs into DistributedList";
	public final static String LOAD_DATA_FROM_DISTRIBUTED_LIST = ClientMenu.TAB + "74) Load My UK from DistributedList";
	public final static String LOAD_TOP_DATA_FROM_DISTRIBUTED_LIST = ClientMenu.TAB + "75) Load TOP My UKs from DistributedList";
	public final static String LOAD_RANGE_DATA_FROM_DISTRIBUTED_LIST = ClientMenu.TAB + "76) Load RANGE My UKs from DistributedList";

	public final static String QUIT = ClientMenu.TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";

	public final static String WRONG_OPTION = "Wrong option!";
}
