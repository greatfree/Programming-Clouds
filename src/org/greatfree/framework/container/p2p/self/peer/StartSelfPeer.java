package org.greatfree.framework.container.p2p.self.peer;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.container.p2p.message.SelfNotification;
import org.greatfree.framework.container.p2p.message.SelfRequest;
import org.greatfree.framework.container.p2p.message.SelfResponse;

// Created: 10/03/2019, Bing Li
class StartSelfPeer
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		SelfPeer.CONTAINER().start("greatfree", ChatConfig.CHAT_SERVER_PORT, new SelfTask(), true);
		
		SelfPeer.CONTAINER().selfSyncNotify(new SelfNotification("hello"));

		SelfResponse response = (SelfResponse)SelfPeer.CONTAINER().selfRead(new SelfRequest("query"));
		System.out.println(response.getResponse());
		
		System.out.println("Press enter to exit ...");
		Scanner in = new Scanner(System.in);
		in.nextLine();

		SelfPeer.CONTAINER().stop(1000);
		in.close();
	}

}
