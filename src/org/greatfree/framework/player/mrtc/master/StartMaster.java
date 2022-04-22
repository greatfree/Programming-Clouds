package org.greatfree.framework.player.mrtc.master;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.concurrency.threading.PlayerSystem;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created: 09/30/2019, Bing Li
class StartMaster
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		System.out.println("Player master " + PlayerSystem.THREADING().getNickName() + " starting up ...");
		
		// Define the map/reduce task. 04/03/2020, Bing Li
		MRMasterTask task = new MRMasterTask();
		// Start the master. 04/03/2020, Bing Li
		PlayerSystem.THREADING().startMaster(ThreadConfig.THREAD_PORT, task);
		
		System.out.println("Player master " + PlayerSystem.THREADING().getNickName() + " started ...");
		
		// Create the threads. 04/03/2020, Bing Li
		MRCoordinator.THREADING().createPlayer();
		// Let all threads know about all of other threads in the game. 04/03/2020, Bing Li
		MRCoordinator.THREADING().notifyAllSlaves();
		
//		MRCoordinator.THREADING().initMR(1);
		// Start the map/reduce game. 04/03/2020, Bing Li
		MRCoordinator.THREADING().initMR(10);

		Scanner in = new Scanner(System.in);
		System.out.println("Press any key to exit ...");
		in.nextLine();

		PlayerSystem.THREADING().dispose(2000);
		TerminateSignal.SIGNAL().notifyAllTermination();
		in.close();
	}

}
