package edu.chainnet.crawler.message;

// Created: 04/22/2021, Bing Li
public class CrawlingApplicationID
{
	public final static int ASSIGN_CRAWL_TASK_NOTIFICATION = 80002;
	public final static int START_CRAWLING_NOTIFICATION = 80001;
	public final static int STOP_CRAWLING_NOTIFICATION = 80005;

	public final static int SOLR_AUTHORITIES_NOTIFICATION = 80003;

	public final static int UPLOAD_PAGES_NOTIFICATION = 80004;
	
	public final static int STOP_ONE_CRAWLING_CHILD_NOTIFICATION = 80006;
	public final static int STOP_CRAWLING_CHILDREN_NOTIFICATION = 80007;
	public final static int STOP_CRAWLING_COORDINATOR_NOTIFICATION = 80008;
	
	public final static int STOP_ONE_DATA_CENTER_CHILD_NOTIFICATION = 80009;
	public final static int STOP_DATA_CENTER_CHILDREN_NOTIFICATION = 80010;
	public final static int STOP_DATA_CENTER_COORDINATOR = 80011;
	
	public final static int CRAWL_PATHS_REQUEST = 80012;
	public final static int CRAWL_PATHS_RESPONSE = 80013;
}
