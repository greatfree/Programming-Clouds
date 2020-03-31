package edu.greatfree.notification.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatConfig;
import org.greatfree.client.FreeClientPool;
import org.greatfree.client.SyncRemoteEventer;

import edu.greatfree.notification.message.Book;
import edu.greatfree.notification.message.MyNotification;

class StartClient
{

	public static void main(String[] args) throws IOException, InterruptedException
	{
		Scanner in = new Scanner(System.in);
		FreeClientPool clientPool = new FreeClientPool(10);
		SyncRemoteEventer<MyNotification> syncEventer = new SyncRemoteEventer<MyNotification>(clientPool);
		Book book = new Book("Java Programming", "Steve");
		syncEventer.notify("127.0.0.1", ChatConfig.CHAT_SERVER_PORT, new MyNotification(book, "Hello, World"));
		System.out.println("Press any key to quit! ");
		in.nextLine();
		in.close();
	}

}
