package org.greatfree.dip.cs.multinode.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.dip.cs.multinode.message.ChatPartnerRequest;
import org.greatfree.dip.cs.multinode.message.ChatPartnerResponse;
import org.greatfree.dip.cs.multinode.message.ChatPartnerStream;

/*
 * The creator generates one instance of ChatPartnerRequestThread. It is invoked by the thread management mechanism, RequestDispatcher. 04/16/2017, Bing Li
 */

// Created: 04/16/2017, Bing Li
class ChatPartnerRequestThreadCreator implements RequestThreadCreatable<ChatPartnerRequest, ChatPartnerStream, ChatPartnerResponse, ChatPartnerRequestThread>
{

	@Override
	public ChatPartnerRequestThread createRequestThreadInstance(int taskSize)
	{
		return new ChatPartnerRequestThread(taskSize);
	}

}
