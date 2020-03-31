package edu.greatfree.cs.multinode.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;

import edu.greatfree.container.cs.multinode.message.ChatMessage;
import edu.greatfree.cs.multinode.message.PollNewChatsRequest;
import edu.greatfree.cs.multinode.message.PollNewChatsResponse;
import edu.greatfree.cs.multinode.message.PollNewChatsStream;

/*
 * The thread checks whether new chats are available. If so, the chats should be responded to the sender of the request. If not, a null message is responded as well. 04/24/2017, Bing Li
 */

// Created: 04/24/2017, Bing Li
class PollNewChatsThread extends RequestQueue<PollNewChatsRequest, PollNewChatsStream, PollNewChatsResponse>
{

	public PollNewChatsThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	@Override
	public void run()
	{
		PollNewChatsStream request;
		PollNewChatsResponse response;
		List<ChatMessage> chatMessages;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				request = this.getRequest();
				
				System.out.println("PollNewChatsThread: " + request.getMessage().getReceiverName() + " is checking new chatting messages ...");

				// Check whether new chatting messages are available or not. 05/25/2017, Bing Li
				chatMessages = PrivateChatSessions.HUNGARY().getNewMessages(request.getMessage().getChatSessionKey(), request.getMessage().getReceiverKey());
				if (chatMessages != null)
				{
					System.out.println("PollNewChatsThread: " + request.getMessage().getReceiverName() + " chatting messages are detected ...");
					response = new PollNewChatsResponse(chatMessages);
				}
				else
				{
					System.out.println("PollNewChatsThread: " + request.getMessage().getReceiverName() + " NO chatting messages are existed ...");
					response = new PollNewChatsResponse(new ArrayList<ChatMessage>());
				}
				
				try
				{
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
