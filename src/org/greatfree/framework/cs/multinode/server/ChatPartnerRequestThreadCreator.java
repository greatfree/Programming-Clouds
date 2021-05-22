package org.greatfree.framework.cs.multinode.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.cs.multinode.message.ChatPartnerRequest;
import org.greatfree.framework.cs.multinode.message.ChatPartnerResponse;
import org.greatfree.framework.cs.multinode.message.ChatPartnerStream;

/*
 * The creator generates one instance of ChatPartnerRequestThread. It is invoked by the thread management mechanism, RequestDispatcher. 04/16/2017, Bing Li
 */

// Created: 04/16/2017, Bing Li
class ChatPartnerRequestThreadCreator implements RequestQueueCreator<ChatPartnerRequest, ChatPartnerStream, ChatPartnerResponse, ChatPartnerRequestThread>
{

	@Override
	public ChatPartnerRequestThread createInstance(int taskSize)
	{
		return new ChatPartnerRequestThread(taskSize);
	}

}
