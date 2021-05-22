package org.greatfree.chat.message.cs.business;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.MessageStream;

// Created: 12/04/2017, Bing Li
public class PostMerchandiseStream extends MessageStream<PostMerchandiseNotification>
{

	public PostMerchandiseStream(ObjectOutputStream out, Lock lock, PostMerchandiseNotification message)
	{
		super(out, lock, message);
	}

}
