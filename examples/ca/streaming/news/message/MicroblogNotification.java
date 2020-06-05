package ca.streaming.news.message;

import org.greatfree.message.multicast.MulticastMessage;

// Created: 03/31/2020, Bing Li
public class MicroblogNotification extends MulticastMessage
{
	private static final long serialVersionUID = 4997760590573347581L;
	
	private Post post;

	public MicroblogNotification(Post post)
	{
		super(NewsDataType.MICROBLOG_NOTIFICATION);
		this.post = post;
	}

	public Post getPost()
	{
		return this.post;
	}
}
