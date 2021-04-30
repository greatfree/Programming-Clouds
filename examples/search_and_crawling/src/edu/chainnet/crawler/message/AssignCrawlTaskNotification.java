package edu.chainnet.crawler.message;

import java.util.List;

import edu.chainnet.crawler.Hub;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 04/22/2021, Bing Li
public class AssignCrawlTaskNotification extends Notification
{
	private static final long serialVersionUID = 478390625837634093L;
	
	private List<Hub> urls;

	public AssignCrawlTaskNotification(List<Hub> urls)
	{
		super(MulticastMessageType.UNICAST_NOTIFICATION, CrawlingApplicationID.ASSIGN_CRAWL_TASK_NOTIFICATION);
		this.urls = urls;
	}

	public List<Hub> getURLs()
	{
		return this.urls;
	}
}

