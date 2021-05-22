package org.greatfree.framework.p2p.registry;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.p2p.message.ChatRegistryRequest;
import org.greatfree.framework.p2p.message.ChatRegistryResponse;
import org.greatfree.framework.p2p.message.ChatRegistryStream;

/*
 * The creator generates one instance of ChatRegistryThread. It is invoked by the thread management mechanism, RequestDispatcher. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
class ChatRegistryThreadCreator implements RequestQueueCreator<ChatRegistryRequest, ChatRegistryStream, ChatRegistryResponse, ChatRegistryThread>
{

	@Override
	public ChatRegistryThread createInstance(int taskSize)
	{
		return new ChatRegistryThread(taskSize);
	}

}
