package edu.chainnet.crawler.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import org.greatfree.cluster.StandaloneClusterClient;

import edu.chainnet.crawler.Hub;

import edu.chainnet.crawler.CrawlConfig;
import edu.chainnet.crawler.client.db.DBConfig;
import edu.chainnet.crawler.client.db.HubDB;
import edu.chainnet.crawler.message.AssignCrawlTaskNotification;
import edu.chainnet.crawler.message.StartCrawlingNotification;
import edu.chainnet.crawler.message.StopCrawlingNotification;

// Created: 04/22/2021, Bing Li
class ClientUI
{
	private final static Logger log = Logger.getLogger("edu.chainnet.crawler.coordinator");

	private Scanner in = new Scanner(System.in);
	private HubDB db;

	private ClientUI()
	{
		this.db = new HubDB(DBConfig.HUB_DB_PATH);
	}

	private static ClientUI instance = new ClientUI();
	
	public static ClientUI CRAWL()
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
		this.db.dispose();
		this.in.close();
	}

	public void printMenu()
	{
		System.out.println(ClientMenu.MENU_HEAD);
		System.out.println(ClientMenu.ASSIGN_CRAWLING_TASK);
		System.out.println(ClientMenu.START_CRAWLING);
		System.out.println(ClientMenu.STOP_CRAWLING);
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}
	
	public void send(int option) throws IOException, InterruptedException
	{
		switch (option)
		{
			case MenuOptions.ASSIGN_TASK:
//				List<Hub> hubs = this.db.loadRawHubs();
				List<Hub> hubs = this.db.loadUnassignedRawHubs();
				List<Hub> assignedHubs = new ArrayList<Hub>();
				for (Hub entry : hubs)
				{
					assignedHubs.add(entry);
					if (assignedHubs.size() >= CrawlConfig.ASSIGNMENT_SIZE)
					{
						log.info("assignedHubs size = " + assignedHubs.size());
						StandaloneClusterClient.CONTAINER().syncNotifyRoot(new AssignCrawlTaskNotification(assignedHubs));
						assignedHubs.clear();
					}
				}
				StandaloneClusterClient.CONTAINER().syncNotifyRoot(new AssignCrawlTaskNotification(assignedHubs));
				this.db.saveHubs(hubs, true);
				break;

			case MenuOptions.START_CRAWL:
				StandaloneClusterClient.CONTAINER().syncNotifyRoot(new StartCrawlingNotification());
				break;
				
			case MenuOptions.STOP_CRAWL:
				StandaloneClusterClient.CONTAINER().syncNotifyRoot(new StopCrawlingNotification());
				break;
				
			case MenuOptions.QUIT:
				break;
		}
	}
}
