package ca.streaming.news.message;

import org.greatfree.message.multicast.MulticastMessage;

// Created: 03/31/2020, Bing Li
public class NewsFeedNotification extends MulticastMessage
{
	private static final long serialVersionUID = 6479321058996702948L;
	
	private Post post;

	public NewsFeedNotification(Post post)
	{
		super(NewsDataType.NEWS_FEED_NOTIFICATION);
		this.post = post;
	}

	public Post getPost()
	{
		return this.post;
	}
}
