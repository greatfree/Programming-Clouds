package com.greatfree.testing.coordinator;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import com.greatfree.concurrency.Runner;
import com.greatfree.testing.coordinator.admin.AdminIORegistry;
import com.greatfree.testing.coordinator.admin.AdminMulticastor;
import com.greatfree.testing.coordinator.admin.AdminListener;
import com.greatfree.testing.coordinator.admin.AdminListenerDisposer;
import com.greatfree.testing.coordinator.crawling.CrawlIORegistry;
import com.greatfree.testing.coordinator.crawling.CrawlListener;
import com.greatfree.testing.coordinator.crawling.CrawlListenerDisposer;
import com.greatfree.testing.coordinator.crawling.CrawlServerClientPool;
import com.greatfree.testing.coordinator.memorizing.MemoryIORegistry;
import com.greatfree.testing.coordinator.memorizing.MemoryListener;
import com.greatfree.testing.coordinator.memorizing.MemoryListenerDisposer;
import com.greatfree.testing.coordinator.memorizing.MemoryServerClientPool;
import com.greatfree.testing.coordinator.searching.CoordinatorMulticastReader;
import com.greatfree.testing.coordinator.searching.SearchClientPool;
import com.greatfree.testing.coordinator.searching.SearchIORegistry;
import com.greatfree.testing.coordinator.searching.SearchListener;
import com.greatfree.testing.coordinator.searching.SearchListenerDisposer;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.util.TerminateSignal;

/*
 * This is a sample of the coordinator which is responsible for managing all of the nodes in the form of a cluster. 11/25/2014, Bing Li
 * 
 * The class is responsible for starting up and shutting down the coordinator. Since it is the unique entry and exit of the coordinator, it is implemented in the form of a singleton. 11/25/2014, Bing Li
 */

// Created: 11/24/2014, Bing Li
public class Coordinator
{
	// The ServerSocket waits for crawlers' connecting. The socket serves the coordinator in the sense that it not only responds to crawlers' requests but also notifies crawlers to manage the crawling process. 11/25/2014, Bing Li
	private ServerSocket crawlServerSocket;
	// The port number for the crawling socket. 11/25/2014, Bing Li
	private int crawlPort;
	
	// The ServerSocket waits for memory servers' connecting. The socket serves the coordinator in the sense that it not only responds to memory servers' requests but also notifies memory servers to manage the storing process. 11/28/2014, Bing Li
	private ServerSocket memServerSocket;
	// The port number for the memory socket. 11/29/2014, Bing Li
	private int memPort;
	
	// The ServerSocket waits for the administrator's connecting. 11/29/2014, Bing Li
	private ServerSocket adminServerSocket;
	// The port number for the administration socket. 11/29/2014, Bing Li
	private int adminPort;
	
	// The ServerSocket waits for the searchers' connecting. 11/29/2014, Bing Li
	private ServerSocket searchServerSocket;
	// The port number for the search socket. 11/29/2014, Bing Li
	private int searchPort;

	// The list keeps all of the threads that listen to connecting from crawlers of the cluster. When the coordinator is shutdown, those threads can be killed to avoid possible missing. 11/25/2014, Bing Li
	private List<Runner<CrawlListener, CrawlListenerDisposer>> crawlListenerRunnerList;
	// The list keeps all of the threads that listen to connecting from memory servers of the cluster. When the coordinator is shutdown, those threads can be killed to avoid possible missing. 11/28/2014, Bing Li
	private List<Runner<MemoryListener, MemoryListenerDisposer>> memListenerRunnerList;

	// Declare one runner for the administration. Since the load is lower, it is not necessary to initialize multiple threads to listen to potential connections. 11/29/2014, Bing Li
	private Runner<AdminListener, AdminListenerDisposer> adminListenerRunner;

	// The list keeps all of the threads that listen to connecting from search clients. When the coordinator is shutdown, those threads can be killed to avoid possible missing. 11/29/2014, Bing Li
	private List<Runner<SearchListener, SearchListenerDisposer>> searchListenerRunnerList;

	/*
	 * A singleton is designed for the coordinator's startup and shutdown interface. 11/25/2014, Bing Li
	 */
	private Coordinator()
	{
	}
	
	private static Coordinator instance = new Coordinator();
	
	public static Coordinator COORDINATOR()
	{
		if (instance == null)
		{
			instance = new Coordinator();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	/*
	 * Start the coordinator and relevant listeners with concurrent threads for potential busy connecting. 11/25/2014, Bing Li
	 */
	public void start(int crawlPort, int memPort, int adminPort, int searchPort)
	{
		// Initialize and start the crawling socket. 11/25/2014, Bing Li
		this.crawlPort = crawlPort;
		try
		{
			this.crawlServerSocket = new ServerSocket(this.crawlPort);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Initialize and start the memory socket. 11/28/2014, Bing Li
		this.memPort = memPort;
		try
		{
			this.memServerSocket = new ServerSocket(this.memPort);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Initialize and start administration socket. 11/29/2014, Bing Li
		this.adminPort = adminPort;
		try
		{
			this.adminServerSocket = new ServerSocket(this.adminPort);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Initialize and start search socket. 11/29/2014, Bing Li
		this.searchPort = searchPort;
		try
		{
			this.searchServerSocket = new ServerSocket(this.searchPort);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Initialize a disposer which collects the crawler listener. 11/25/2014, Bing Li
		CrawlListenerDisposer crawlDisposer = new CrawlListenerDisposer();
		// Initialize the runner list. 11/25/2014, Bing Li
		this.crawlListenerRunnerList = new ArrayList<Runner<CrawlListener, CrawlListenerDisposer>>();
		// Start up the threads to listen to connecting from crawlers which send and receive messages from the coordinator. 11/25/2014, Bing Li
		Runner<CrawlListener, CrawlListenerDisposer> crawlRunner;
		for (int i = 0; i < ServerConfig.LISTENING_THREAD_COUNT; i++)
		{
			crawlRunner = new Runner<CrawlListener, CrawlListenerDisposer>(new CrawlListener(this.crawlServerSocket, ServerConfig.LISTENER_THREAD_POOL_SIZE, ServerConfig.LISTENER_THREAD_ALIVE_TIME), crawlDisposer, true);
			this.crawlListenerRunnerList.add(crawlRunner);
			crawlRunner.start();
		}
		
		// Initialize a disposer which collects the memory listener. 11/28/2014, Bing Li
		MemoryListenerDisposer memDisposer = new MemoryListenerDisposer();
		// Initialize the runner list. 11/28/2014, Bing Li
		this.memListenerRunnerList = new ArrayList<Runner<MemoryListener, MemoryListenerDisposer>>();
		// Start up the threads to listen to connecting from memory servers which send and receive messages from the coordinator. 11/28/2014, Bing Li
		Runner<MemoryListener, MemoryListenerDisposer> memRunner;
		for (int i = 0; i < ServerConfig.LISTENING_THREAD_COUNT; i++)
		{
			memRunner = new Runner<MemoryListener, MemoryListenerDisposer>(new MemoryListener(this.memServerSocket, ServerConfig.LISTENER_THREAD_POOL_SIZE, ServerConfig.LISTENER_THREAD_ALIVE_TIME), memDisposer, true);
			this.memListenerRunnerList.add(memRunner);
			memRunner.start();
		}

		// Initialize a disposer which collects the administration listener. 11/29/2014, Bing Li
		AdminListenerDisposer adminDisposer = new AdminListenerDisposer();
		// Initialize the runner to listen to connecting from the administrator which sends and notifications to the coordinator. 11/29/2014, Bing Li
		this.adminListenerRunner = new Runner<AdminListener, AdminListenerDisposer>(new AdminListener(this.memServerSocket, ServerConfig.LISTENER_THREAD_POOL_SIZE, ServerConfig.LISTENER_THREAD_ALIVE_TIME), adminDisposer, true);
		// Start up the runner. 11/29/2014, Bing Li
		this.adminListenerRunner.start();

		
		// Initialize a disposer which collects the search listener. 11/29/2014, Bing Li
		SearchListenerDisposer searchDisposer = new SearchListenerDisposer();
		// Initialize the runner list. 11/29/2014, Bing Li
		this.searchListenerRunnerList = new ArrayList<Runner<SearchListener, SearchListenerDisposer>>();
		// Start up the threads to listen to connecting from search clients which send messages from the coordinator. 11/29/2014, Bing Li
		Runner<SearchListener, SearchListenerDisposer> searchRunner;
		for (int i = 0; i < ServerConfig.LISTENING_THREAD_COUNT; i++)
		{
			searchRunner = new Runner<SearchListener, SearchListenerDisposer>(new SearchListener(this.memServerSocket, ServerConfig.LISTENER_THREAD_POOL_SIZE, ServerConfig.LISTENER_THREAD_ALIVE_TIME), searchDisposer, true);
			this.searchListenerRunnerList.add(searchRunner);
			searchRunner.start();
		}
		
		// Initialize the profile. 11/25/2014, Bing Li
		Profile.CONFIG().init(CoorConfig.CSERVER_CONFIG);
		
		// Initialize the crawling IO registry. 11/25/2014, Bing Li
		CrawlIORegistry.REGISTRY().init();
		// Initialize a crawling client pool, which is used by the coordinator to connect to the remote crawler. 11/25/2014, Bing Li
		CrawlServerClientPool.COORDINATE().init();

		// Initialize the memory IO registry. 11/28/2014, Bing Li
		MemoryIORegistry.REGISTRY().init();
		// Initialize a memory client pool, which is used by the coordinator to connect to the remote memory server. 11/25/2014, Bing Li
		MemoryServerClientPool.COORDINATE().init();

		// Initialize the administration IO registry. 11/29/2014, Bing Li
		AdminIORegistry.REGISTRY().init();
		
		// Initialize the search IO registry. 11/29/2014, Bing Li
		SearchIORegistry.REGISTRY().init();
		// Initialize a search client pool, which is used by the coordinator to connect to the remote search client. 11/29/2014, Bing Li
		SearchClientPool.COORDINATE().init();

		// Initialize the multicastor for the clusters, crawlers and memory nodes. 11/27/2014, Bing Li
		CoordinatorMulticastor.COORDINATE().init();
		// Initialize the administration multicastor. 11/27/2014, Bing Li
		AdminMulticastor.ADMIN().init();

		// Initialize the memory multicast reader. 11/29/2014, Bing Li
		CoordinatorMulticastReader.COORDINATE().init();
	}
	
	/*
	 * Shutdown the coordinator. 11/25/2014, Bing Li
	 */
	public void stop() throws IOException, InterruptedException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();
		// Close the sockets for the coordinator. 11/25/2014, Bing Li
		this.crawlServerSocket.close();
		this.memServerSocket.close();
		this.adminServerSocket.close();
		this.searchServerSocket.close();
		
		// Stop all of the threads that listen to crawlers' connecting to the coordinator. 11/25/2014, Bing Li
		for (Runner<CrawlListener, CrawlListenerDisposer> runner : this.crawlListenerRunnerList)
		{
			runner.stop();
		}

		// Stop all of the threads that listen to memory servers' connecting to the coordinator. 11/28/2014, Bing Li
		for (Runner<MemoryListener, MemoryListenerDisposer> runner : this.memListenerRunnerList)
		{
			runner.stop();
		}

		// Stop the administration runner. 11/29/2014, Bing Li
		this.adminListenerRunner.stop();

		// Stop all of the threads that listen to search clients' connecting to the coordinator. 11/29/2014, Bing Li
		for (Runner<SearchListener, SearchListenerDisposer> runner : this.searchListenerRunnerList)
		{
			runner.stop();
		}

		// Dispose the profile. 11/25/2014, Bing Li
		Profile.CONFIG().dispose();
		
		// Shutdown the IO registry. 11/25/2014, Bing Li
		CrawlIORegistry.REGISTRY().dispose();
		
		// Shutdown the client pool. 11/25/2014, Bing Li
		CrawlServerClientPool.COORDINATE().dispose();
		
		// Shutdown the IO registry. 11/28/2014, Bing Li
		MemoryIORegistry.REGISTRY().dispose();
		
		// Shutdown the client pool. 11/28/2014, Bing Li
		MemoryServerClientPool.COORDINATE().dispose();
		
		// Shutdown the IO registry. 11/29/2014, Bing Li
		AdminIORegistry.REGISTRY().dispose();

		// Shutdown the client pool. 11/29/2014, Bing Li
		SearchClientPool.COORDINATE().dispose();
		// Shutdown the IO registry. 11/29/2014, Bing Li
		SearchIORegistry.REGISTRY().dispose();

		// Dispose the multicastor for crawlers and memory nodes. 11/27/2014, Bing Li
		CoordinatorMulticastor.COORDINATE().dispose();
		// Dispose the administration multicastor. 11/27/2014, Bing Li
		AdminMulticastor.ADMIN().dispose();

		// Dispose the memory multicast reader. 11/29/2014, Bing Li
		CoordinatorMulticastReader.COORDINATE().dispose();
	}
}
