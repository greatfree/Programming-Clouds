package org.greatfree.framework.p2p.registry;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.framework.p2p.message.ChatPartnerRequest;
import org.greatfree.framework.p2p.message.ChatPartnerResponse;
import org.greatfree.framework.p2p.message.ChatPartnerStream;

/*
 * The creator generates one instance of ChatPartnerRequestThread. It is invoked by the thread management mechanism, RequestDispatcher. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
class ChatPartnerRequestThreadCreator implements RequestQueueCreator<ChatPartnerRequest, ChatPartnerStream, ChatPartnerResponse, ChatPartnerRequestThread>
{

	@Override
	public ChatPartnerRequestThread createInstance(int taskSize)
	{
		return new ChatPartnerRequestThread(taskSize);
	}

}
