package org.greatfree.framework.player.ttcm.master;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.concurrency.threading.Player;
import org.greatfree.concurrency.threading.PlayerSystem;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ThreadAssignmentException;
import org.greatfree.framework.threading.message.PingNotification;

// Created: 09/30/2019, Bing Li
class StartMaster
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, ThreadAssignmentException
	{
		System.out.println("Player master starting up ...");

		MasterTask task = new MasterTask();
		PlayerSystem.THREADING().startMaster(ThreadConfig.MASTER, ThreadConfig.THREAD_PORT, ThreadConfig.SLAVE, task);

		System.out.println("Player master started ...");

		Player p1 = PlayerSystem.THREADING().create();
		Player p2 = PlayerSystem.THREADING().create();

		task.setPlayers(p1, p2);

		p1.notifyThreads(new PingNotification(p1.getThreadKey(), "T1 PINGS", 1000));
		
		Scanner in = new Scanner(System.in);
		System.out.println("Press any key to exit ...");
		in.nextLine();

		PlayerSystem.THREADING().dispose(2000);
		in.close();
	}

}
