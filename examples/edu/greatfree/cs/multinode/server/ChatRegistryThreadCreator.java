package edu.greatfree.cs.multinode.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import edu.greatfree.cs.multinode.message.ChatRegistryRequest;
import edu.greatfree.cs.multinode.message.ChatRegistryResponse;
import edu.greatfree.cs.multinode.message.ChatRegistryStream;

//Created: 04/15/2017, Bing Li
class ChatRegistryThreadCreator implements RequestThreadCreatable<ChatRegistryRequest, ChatRegistryStream, ChatRegistryResponse, ChatRegistryThread>
{

	@Override
	public ChatRegistryThread createRequestThreadInstance(int taskSize)
	{
		return new ChatRegistryThread(taskSize);
	}

}
