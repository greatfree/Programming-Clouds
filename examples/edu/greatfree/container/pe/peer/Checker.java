package edu.greatfree.container.pe.peer;

import java.io.IOException;

import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.UtilConfig;

import edu.greatfree.container.pe.message.PollChatRequest;
import edu.greatfree.container.pe.message.PollChatResponse;
import edu.greatfree.cs.multinode.ChatConfig;

// Created: 06/05/2020, Bing Li
class Checker implements Runnable
{
	private String partnerName;
	private String partnerKey;
	
	public Checker(String partnerName)
	{
		this.partnerName = partnerName;
		this.partnerKey = ChatConfig.generateUserKey(partnerName);
	}

	@Override
	public void run()
	{
		try
		{
//			System.out.println("Checker-run() ......");
			PollChatResponse res = (PollChatResponse)ChatPeer.PE().read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new PollChatRequest(this.partnerKey));
			if (!res.getMessage().equals(UtilConfig.EMPTY_STRING))
			{
				System.out.println(partnerName + " says, " + res.getMessage());
			}
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
	}

}
