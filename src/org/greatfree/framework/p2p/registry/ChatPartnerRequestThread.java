package org.greatfree.framework.p2p.registry;

import java.io.IOException;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.p2p.message.ChatPartnerRequest;
import org.greatfree.framework.p2p.message.ChatPartnerResponse;
import org.greatfree.framework.p2p.message.ChatPartnerStream;
import org.greatfree.server.PeerAccount;
import org.greatfree.server.PeerRegistry;
import org.greatfree.util.UtilConfig;

/*
 * The thread checks the retrieved user of chatting. 04/16/2017, Bing Li
 */

// Created: 04/30/2017, Bing Li
class ChatPartnerRequestThread extends RequestQueue<ChatPartnerRequest, ChatPartnerStream, ChatPartnerResponse>
{

	public ChatPartnerRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		ChatPartnerStream request;
		ChatPartnerResponse response;
		PeerAccount account;
		PeerChatAccount chatAccount;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				
				System.out.println("ChatPartnerRequestThread: partnerKey = " + request.getMessage().getPartnerKey());
	
				if (PeerRegistry.SYSTEM().isExisted(request.getMessage().getPartnerKey()))
				{
					// Get the account. 04/16/2017, Bing Li
					account = PeerRegistry.SYSTEM().get(request.getMessage().getPartnerKey());
					if (AccountRegistry.APPLICATION().isAccountExisted(request.getMessage().getPartnerKey()))
					{
						chatAccount = AccountRegistry.APPLICATION().getAccount(request.getMessage().getPartnerKey());
						// Generate the response. 04/16/2017, Bing Li
						response = new ChatPartnerResponse(account.getPeerKey(), account.getPeerName(), chatAccount.getDescription(), chatAccount.getPreference(), account.getIP(), account.getPeerPort());
					}
					else
					{
						// Generate the response. 04/16/2017, Bing Li
						response = new ChatPartnerResponse(account.getPeerKey(), account.getPeerName(), UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, account.getIP(), account.getPeerPort());
					}
				}
				else
				{
					// Generate the response. 04/16/2017, Bing Li
					response = new ChatPartnerResponse(UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, UtilConfig.EMPTY_STRING, UtilConfig.ZERO);
				}
				
				try
				{
					// Respond to the client. 04/16/2017, Bing Li
					this.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				this.disposeMessage(request, response);
			}
			try
			{
				// Wait for some time when the queue is empty. During the period and before the thread is killed, some new requests might be received. If so, the thread can keep working. 02/15/2016, Bing Li
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}
