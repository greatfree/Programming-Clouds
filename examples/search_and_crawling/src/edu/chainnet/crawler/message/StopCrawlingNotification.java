package edu.chainnet.crawler.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 04/25/2021, Bing Li
public class StopCrawlingNotification extends Notification
{
	private static final long serialVersionUID = 6051141175608376233L;

	public StopCrawlingNotification()
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, CrawlingApplicationID.STOP_CRAWLING_NOTIFICATION);
	}
}


