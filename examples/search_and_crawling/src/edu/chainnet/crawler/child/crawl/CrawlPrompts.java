package edu.chainnet.crawler.child.crawl;

import java.util.logging.Logger;

// Created: 11/22/2017, Bing Li
class CrawlPrompts
{
	private final static Logger log = Logger.getLogger("edu.chainnet.crawler.child.crawl");

	public final static String CONNECTION_RESET = "Remote server refuses the crawling: ";
	public final static String HTTP_ERROR = "Remote server gets errors: ";
	public static final String BAD_URL_FORMAT = "A bad URL cannot be accessed!";
	public static final String WORKER_INTERRUPTED = "Worker is interrupted!";
	public static final String CRAWLING_TASK_RECEIVER_CLOSED = "Crawling task receiver closed!";
	public static final String ONE_CRAWLING_TASK_RECEIVER_ENTERED = "One crawling task receiver entered!";
	public static final String ONE_CRAWLING_TASK_RECEIVER_CLOSED = "One crawling task receiver closed!";
	public static final String CANNOT_ALLOW_MORE_CRAWLING_TASK_RECEIVER = "Cannot allow more crawling task receivers to enter!";

	public static final String SUPER_TASK_SCHEDULER_EXITED_BY_SLEEP_INTERRUPTION = "Super task scheduler exited by sleeping interruption!";
	public static final String CRAWLING_POOL_EXITED_BY_INTERRUPTION = "Crawling pool exited by interruption!";
	public static final String AUTHORITY_COLLECTOR_EXITED_BY_IO_EXCEPTION = "Authority collector exited by IO exception";
	public static final String CRAWLING_POOL_SHUTDOWN_CAUSES_NULL = "Crawling pool shutdowning causes null exception";
	public static final String SHUTDOWN_SUPER_TASK_RECEIVERS_CAUSES_NULL = "Shutdowning super task receivers causes null exception";
	public static final String SHUTDOWN_AUTHORITY_COLLECTOR_CAUSES_NULL = "Shutdowning authority collectors causes null exception";
	
	public static final String HUB_CRAWLER_DISPOSING_CAUSES_NULL = "Hub crawler disposing causes null exception";
	public final static String UPDATE_RAW_HUB_GETS_EXCETIONS = "UpdateRawHub gets exception when sending data!";
	public final static String IDLE_WORKRE_DISPOSING_INTERRUPTED = "Idle worker disposing interrupted!";
	public final static String IDLE_WORKER_INTERRUPTED = "Idle worker interrupted!";
	
	public final static String CRAWL_THREAD_INTERRUPTED = "CrawlThread interrupted!";
	public final static String HTML_TEXT_BEAN_STOPPED = "HTMLTextBean stopped!";
	public final static String HTTP_PROTOCOL_HOST_NULL = "Http protocol has no host";
	public final static String ACCESS_IMAGE_URL_EXCEPTION = "Accessing image URL exception!";

	public final static String IMAGE_CRAWL_SERVER_IS_NOT_AVAILABLE = "NO image crawling server is available such that the crawling server is NOT ready!";
	
	static void print(Exception e)
	{
		if (CrawlStates.CRAWL().isReady())
		{
			log.info(HTTP_ERROR);
		}
	}
}
