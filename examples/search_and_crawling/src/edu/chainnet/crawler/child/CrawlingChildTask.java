package edu.chainnet.crawler.child;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.message.ClusterApplicationID;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

import edu.chainnet.crawler.child.crawl.CrawlingBoss;
import edu.chainnet.crawler.child.crawl.HubRegistry;
import edu.chainnet.crawler.message.AssignCrawlTaskNotification;
import edu.chainnet.crawler.message.CrawlingApplicationID;

// Created: 04/22/2021, Bing Li
class CrawlingChildTask implements ChildTask
{
	private final static Logger log = Logger.getLogger("edu.chainnet.crawler.child");

	@Override
	public InterChildrenNotification prepareNotification(IntercastNotification arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InterChildrenRequest prepareRequest(IntercastRequest arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case CrawlingApplicationID.ASSIGN_CRAWL_TASK_NOTIFICATION:
				log.info("ASSIGN_CRAWL_TASK_NOTIFICATION received @" + Calendar.getInstance().getTime());
				AssignCrawlTaskNotification actn = (AssignCrawlTaskNotification)notification;
				HubRegistry.WWW().addHubs(actn.getURLs());
//				log.info("2) ASSIGN_CRAWL_TASK_NOTIFICATION received @" + Calendar.getInstance().getTime());
				break;

			case CrawlingApplicationID.START_CRAWLING_NOTIFICATION:
				log.info("START_CRAWLING_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					CrawlingBoss.CRAWL().startCrawl();
				}
				catch (ClassNotFoundException | RemoteReadException | IOException e)
				{
					e.printStackTrace();
				}
				break;

			case CrawlingApplicationID.STOP_CRAWLING_NOTIFICATION:
				log.info("STOP_CRAWLING_NOTIFICATION received @" + Calendar.getInstance().getTime());
				CrawlingBoss.CRAWL().stopCrawl();
				break;
				
			case ClusterApplicationID.STOP_CHAT_CLUSTER_NOTIFICATION:
				log.info("STOP_CHAT_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					CrawlChild.CRAWL().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
				
			case CrawlingApplicationID.STOP_ONE_CRAWLING_CHILD_NOTIFICATION:
				log.info("STOP_ONE_CRAWLING_CHILD_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					CrawlChild.CRAWL().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public MulticastResponse processRequest(Request arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MulticastResponse processRequest(InterChildrenRequest arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processResponse(Response arg0)
	{
		// TODO Auto-generated method stub
		
	}

}
