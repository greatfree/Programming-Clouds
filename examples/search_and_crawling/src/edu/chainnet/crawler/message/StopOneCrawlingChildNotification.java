package edu.chainnet.crawler.message;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 04/25/2021, Bing Li
public class StopOneCrawlingChildNotification extends Notification
{
	private static final long serialVersionUID = -4851020036679387504L;

	public StopOneCrawlingChildNotification()
	{
		super(MulticastMessageType.UNICAST_NOTIFICATION, CrawlingApplicationID.STOP_ONE_CRAWLING_CHILD_NOTIFICATION);
	}
}
