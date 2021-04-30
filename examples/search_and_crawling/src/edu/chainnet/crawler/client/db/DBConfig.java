package edu.chainnet.crawler.client.db;

import edu.chainnet.center.CenterConfig;

// Created: 04/23/2021, Bing Li
public class DBConfig
{
	public final static long LOCK_TIME_OUT = 1000;
	public final static long DB_CACHE_SIZE = 1000000;
	public final static String RAW_HUB_STORE = "RawHubStore";
	
	public final static String RAW_HUB_DB_PATH = CenterConfig.CENTER_HOME + "RawHubDB/";

	public final static String HUB_SOURCE_TERMINAL_STORE = "HubSourceTerminalStore";
	public final static String HUB_KEYS_TERMINAL_STORE = "HubKeysTerminalStore";

	public final static String RAW_HUBS_PATH = CenterConfig.CENTER_HOME + "Data/RawHubs/raw_hubs.txt";
	public final static String HUB_DB_PATH = CenterConfig.CENTER_HOME + "Data/HubDB/";
}

