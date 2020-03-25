package org.greatfree.dip.player.mrtc.master;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.concurrency.threading.PlayerSystem;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.exceptions.RemoteReadException;

// Created: 09/30/2019, Bing Li
class StartMaster
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		System.out.println("Player master " + PlayerSystem.THREADING().getNickName() + " starting up ...");
		
		MRMasterTask task = new MRMasterTask();
		PlayerSystem.THREADING().startMaster(ThreadConfig.THREAD_PORT, task);
		
		System.out.println("Player master " + PlayerSystem.THREADING().getNickName() + " started ...");
		
		MRCoordinator.THREADING().createPlayer();
		MRCoordinator.THREADING().notifyAllSlaves();
		
		MRCoordinator.THREADING().initMR(1);
//		MRCoordinator.THREADING().initMR(10);

		Scanner in = new Scanner(System.in);
		System.out.println("Press any key to exit ...");
		in.nextLine();

		PlayerSystem.THREADING().dispose(2000);
		in.close();
	}

}
