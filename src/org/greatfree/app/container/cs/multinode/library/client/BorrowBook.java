package org.greatfree.app.container.cs.multinode.library.client;

import java.io.IOException;

import org.greatfree.app.container.cs.multinode.library.message.BorrowBookRequest;
import org.greatfree.app.container.cs.multinode.library.message.BorrowBookResponse;
import org.greatfree.chat.ChatConfig;
import org.greatfree.client.StandaloneClient;
import org.greatfree.exceptions.RemoteReadException;

// Created: 12/19/2018, Bing Li
class BorrowBook
{

	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
//		ChatClient.CS_FRONT().init();
		StandaloneClient.CS().init();

//		BorrowBookResponse res = (BorrowBookResponse)ChatClient.CS_FRONT().read(new BorrowBookRequest("Java"));
		BorrowBookResponse res = (BorrowBookResponse)StandaloneClient.CS().read(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new BorrowBookRequest("Java"));
		
		System.out.println("Book Title = " + res.getBookTitle());
		System.out.println("Book Author = " + res.getAuthor());
	
//		ChatClient.CS_FRONT().dispose();
		StandaloneClient.CS().dispose();
	}

}
