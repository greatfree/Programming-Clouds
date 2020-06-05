package ca.streaming.news.message;

import org.greatfree.message.multicast.MulticastMessage;

// Created: 03/31/2020, Bing Li
public class JournalistPostNotification extends MulticastMessage
{
	private static final long serialVersionUID = -8014990740444761762L;
	
	private Post post;

	public JournalistPostNotification(Post post)
	{
		super(NewsDataType.JOURNALIST_POST_NOTIFICATION);
		this.post = post;
	}

	public Post getNews()
	{
		return this.post;
	}
}
