package org.greatfree.dsf.p2p.registry;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dsf.p2p.message.ChatRegistryRequest;
import org.greatfree.dsf.p2p.message.ChatRegistryResponse;
import org.greatfree.dsf.p2p.message.ChatRegistryStream;

/*
 * The creator generates one instance of ChatRegistryThread. It is invoked by the thread management mechanism, RequestDispatcher. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
class ChatRegistryThreadCreator implements RequestThreadCreatable<ChatRegistryRequest, ChatRegistryStream, ChatRegistryResponse, ChatRegistryThread>
{

	@Override
	public ChatRegistryThread createRequestThreadInstance(int taskSize)
	{
		return new ChatRegistryThread(taskSize);
	}

}
