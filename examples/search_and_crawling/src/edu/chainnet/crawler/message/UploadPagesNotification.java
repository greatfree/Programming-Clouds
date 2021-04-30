package edu.chainnet.crawler.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

import edu.chainnet.crawler.AuthoritySolrValue;

// Created: 04/24/2021, Bing Li
public class UploadPagesNotification extends Notification
{
	private static final long serialVersionUID = 5876469021191672888L;
	
	private List<AuthoritySolrValue> pages;

	public UploadPagesNotification(List<AuthoritySolrValue> pages)
	{
		super(MulticastMessageType.UNICAST_NOTIFICATION, CrawlingApplicationID.UPLOAD_PAGES_NOTIFICATION);
		this.pages = pages;
	}

	public List<AuthoritySolrValue> getPages()
	{
		return this.pages;
	}
}
