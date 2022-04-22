package org.greatfree.framework.cs.multinode.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

/*
 * The class encloses the output stream to send the response of ChatPartnerResponse to the client. 04/16/2017, Bing Li
 */

// Created: 04/16/2017, Bing Li
public class ChatPartnerStream extends MessageStream<ChatPartnerRequest>
{

	public ChatPartnerStream(ObjectOutputStream out, Lock lock, ChatPartnerRequest message)
	{
		super(out, lock, message);
	}

}
