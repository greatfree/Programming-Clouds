package edu.chainnet.crawler.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 04/25/2021, Bing Li
public class StopCrawlingCoordinatorNotification extends Notification
{
	private static final long serialVersionUID = -4549931447439367336L;

	public StopCrawlingCoordinatorNotification()
	{
		super(MulticastMessageType.LOCAL_NOTIFICATION, CrawlingApplicationID.STOP_CRAWLING_COORDINATOR_NOTIFICATION);
	}
}

