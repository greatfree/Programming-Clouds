package org.greatfree.dip.cs.twonode.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cs.multinode.message.ChatRegistryRequest;
import org.greatfree.dip.cs.multinode.message.ChatRegistryResponse;
import org.greatfree.dip.cs.multinode.message.ChatRegistryStream;

/*
 * The creator generates one instance of ChatRegistryThread. It is invoked by the thread management mechanism, RequestDispatcher. 04/15/2017, Bing Li
 */

// Created: 04/15/2017, Bing Li
public class ChatRegistryThreadCreator implements RequestThreadCreatable<ChatRegistryRequest, ChatRegistryStream, ChatRegistryResponse, ChatRegistryThread>
{

	@Override
	public ChatRegistryThread createRequestThreadInstance(int taskSize)
	{
		return new ChatRegistryThread(taskSize);
	}

}
