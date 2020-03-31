package ca.multicast.search.message;

// Created: 03/14/2020, Bing Li
public class SearchConfig
{
	public final static int CRAWL_DATA_NOTIFICATION = 10000;
	
	public final static int SEARCH_QUERY_REQUEST = 10001;
	public final static int SEARCH_QUERY_RESPONSE = 10002;
	
	public final static int CLIENT_SEARCH_QUERY_REQUEST = 10003;
	public final static int CLIENT_SEARCH_QUERY_RESPONSE = 10004;
	
	public final static String NULL_RESULT = "Null";
	
	public final static String TEST_DATA = "/home/libing/Temp/text_sample.txt";
	public final static long TIMEOUT = 2000;
}
