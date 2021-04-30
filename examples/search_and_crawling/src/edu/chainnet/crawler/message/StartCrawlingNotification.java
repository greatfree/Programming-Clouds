package edu.chainnet.crawler.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 04/22/2021, Bing Li
public class StartCrawlingNotification extends Notification
{
	private static final long serialVersionUID = 174434329700566398L;

	public StartCrawlingNotification()
	{
		super(MulticastMessageType.BROADCAST_NOTIFICATION, CrawlingApplicationID.START_CRAWLING_NOTIFICATION);
	}
}

