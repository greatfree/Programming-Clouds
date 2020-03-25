package org.greatfree.dip.threading.message;

import org.greatfree.message.container.Notification;

// Created: 09/26/2019, Bing Li
public class MRPartialNotification extends Notification
{
	private static final long serialVersionUID = 6548805067086084063L;
	
	private String mrKey;
	private String path;

	public MRPartialNotification(String mrKey, String path)
	{
		super(TaskApplicationID.MR_PARTIAL_NOTIFICATION);
		this.mrKey = mrKey;
		this.path = path;
	}
	
	public String getMRKey()
	{
		return this.mrKey;
	}

	public String getPath()
	{
		return this.path;
	}
}
