package ca.dp.mncs.circle.message;

import org.greatfree.message.ServerMessage;

// Created: 02/25/2020, Bing Li
public class LikeNotification extends ServerMessage
{
	private static final long serialVersionUID = 4556117322742994232L;
	
	private String friendName;

	public LikeNotification(String friendName)
	{
		super(CircleConfig.LIKE_NOTIFICATION);
		this.friendName = friendName;
	}

	public String getFriendName()
	{
		return this.friendName;
	}
}
