package org.greatfree.app.business.cs.multinode.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cs.multinode.message.ChatRegistryRequest;
import org.greatfree.framework.cs.multinode.message.ChatRegistryResponse;
import org.greatfree.framework.cs.multinode.message.ChatRegistryStream;

/*
 * The creator generates one instance of ChatRegistryThread. It is invoked by the thread management mechanism, RequestDispatcher. 04/15/2017, Bing Li
 */

// Created: 04/15/2017, Bing Li
public class ChatRegistryThreadCreator implements RequestQueueCreator<ChatRegistryRequest, ChatRegistryStream, ChatRegistryResponse, ChatRegistryThread>
{

	@Override
	public ChatRegistryThread createInstance(int taskSize)
	{
		return new ChatRegistryThread(taskSize);
	}

}
