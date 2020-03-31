package us.treez.inventory;

// Created: 02/05/2020, Bing Li
public class TreezConfig
{
	public final static long DB_CACHE_SIZE = 1000000;
	public final static long LOCK_TIME_OUT = 0;
	public final static int DB_POOL_SIZE = 100;
	
	public final static String INVENTORY_STORE = "InventoryStore";
	public final static String ORDER_STORE = "OrderStore";
	
	public final static String MERCHANDISE_KEY = "merchandiseKey";
	
	public final static String SERVER_IP = "127.0.0.1";
	public final static int SERVER_PORT = 3000;
	public final static String INVENTORY_DB_PATH = "/home/libing/Temp/InventoryDB/";
	public final static String ORDER_DB_PATH = "/home/libing/Temp/OrderDB/";
	public final static long TERMINATE_SLEEP = 2000;
}
