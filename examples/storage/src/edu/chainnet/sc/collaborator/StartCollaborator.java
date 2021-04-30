package edu.chainnet.sc.collaborator;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.chainnet.sc.SCConfig;
import edu.chainnet.sc.collaborator.child.db.DBConfig;

/*
 * The program starts the collaborator of block-chain systems. 10/16/2020, Bing Li
 */

// Created: 10/16/2020, Bing Li
class StartCollaborator
{

	public static void main(String[] args)
	{
		System.out.println("Collaborator root starting up ...");
		
		try
		{
			Collaborator.BC().start(SCConfig.REGISTRY_SERVER_IP, SCConfig.REGISTRY_SERVER_PORT, DBConfig.DB_HOME, new CollaboratorTask());
		}
		catch (ClassNotFoundException | IOException | RemoteReadException | DistributedNodeFailedException e)
		{
			e.printStackTrace();
		}
		
		System.out.println("Collaborator root started ...");
		
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

	}

}
