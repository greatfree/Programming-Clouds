package ca.streaming.news.message;

import org.greatfree.message.multicast.MulticastMessage;

// Created: 03/31/2020, Bing Li
public class CommentNotification extends MulticastMessage
{
	private static final long serialVersionUID = -2088123770615468103L;
	
	private Post post;

	public CommentNotification(Post post)
	{
		super(NewsDataType.COMMENT_NOTIFICATION);
		this.post = post;
	}

	public Post getPost()
	{
		return this.post;
	}
}
