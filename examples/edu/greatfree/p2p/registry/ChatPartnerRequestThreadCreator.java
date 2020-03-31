package edu.greatfree.p2p.registry;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import edu.greatfree.p2p.message.ChatPartnerRequest;
import edu.greatfree.p2p.message.ChatPartnerResponse;
import edu.greatfree.p2p.message.ChatPartnerStream;

/*
 * The creator generates one instance of ChatPartnerRequestThread. It is invoked by the thread management mechanism, RequestDispatcher. 04/30/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
class ChatPartnerRequestThreadCreator implements RequestThreadCreatable<ChatPartnerRequest, ChatPartnerStream, ChatPartnerResponse, ChatPartnerRequestThread>
{

	@Override
	public ChatPartnerRequestThread createRequestThreadInstance(int taskSize)
	{
		return new ChatPartnerRequestThread(taskSize);
	}

}
