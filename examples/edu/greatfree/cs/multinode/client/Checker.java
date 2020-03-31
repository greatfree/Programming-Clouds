package edu.greatfree.cs.multinode.client;

/*
 * The class periodically checks whether new sessions and new messages are available on the chatting server. 04/24/2017, Bing Li
 */

// Created: 04/24/2017, Bing Li
class Checker implements Runnable
{

	@Override
	public void run()
	{
//		System.out.println("\nChecking starting ...");
		ChatMaintainer.CS().checkNewSessions();
		ChatMaintainer.CS().checkNewChats();
//		System.out.println("Checking done ...\n");
	}

}
