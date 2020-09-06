package org.greatfree.dsf.player.tcc.master;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.concurrency.threading.Player;
import org.greatfree.concurrency.threading.PlayerSystem;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.dsf.threading.message.PrintTaskNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ThreadAssignmentException;

// Created: 09/30/2019, Bing Li
class StartMaster
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, ThreadAssignmentException
	{
		System.out.println("Player master starting up ...");

		MasterTask task = new MasterTask();
		PlayerSystem.THREADING().startMaster(ThreadConfig.MASTER, ThreadConfig.THREAD_PORT, ThreadConfig.SLAVE, task);

		System.out.println("Player master started ...");

//		Player printerA = PlayerSystem.THREADING().reuse();
//		Player printerB = PlayerSystem.THREADING().reuse();
		Player printerA = PlayerSystem.THREADING().create();
		Player printerB = PlayerSystem.THREADING().create();
		
		task.setPlayers(printerA, printerB);
		
		printerA.notifyThreads(new PrintTaskNotification(printerA.getThreadKey(), "A: Hello!", 1000));
		
		Scanner in = new Scanner(System.in);
		System.out.println("Press any key to exit ...");
		in.nextLine();

		PlayerSystem.THREADING().dispose(2000);
		in.close();
	}

}
