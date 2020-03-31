package edu.greatfree.p2p.registry;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import edu.greatfree.p2p.message.ChatRegistryRequest;
import edu.greatfree.p2p.message.ChatRegistryResponse;
import edu.greatfree.p2p.message.ChatRegistryStream;

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
