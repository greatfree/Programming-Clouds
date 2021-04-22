package org.greatfree.framework.player.tnti.master;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import org.greatfree.concurrency.threading.Player;
import org.greatfree.concurrency.threading.PlayerSystem;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.concurrency.threading.message.TaskResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ThreadAssignmentException;
import org.greatfree.framework.threading.message.AddInterNotification;
import org.greatfree.framework.threading.message.AddInterRequest;
import org.greatfree.framework.threading.message.AddInterResponse;

/*
 * The "TTI/tti" represents Two-Node-Thread-Interaction. 10/07/2019, Bing Li
 */

// Created: 10/05/2019, Bing Li
class StartMaster
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, ThreadAssignmentException
	{
		System.out.println("Player master starting up ...");
		MasterTask task = new MasterTask();
		PlayerSystem.THREADING().startMaster(ThreadConfig.MASTER, ThreadConfig.THREAD_PORT, ThreadConfig.SLAVE, task);
		System.out.println("Player master started ...");

		Scanner in = new Scanner(System.in);

		Player pa = PlayerSystem.THREADING().create();
		Player pb = PlayerSystem.THREADING().create();
		Player pc = PlayerSystem.THREADING().create(10);

		pa.notifyThreads(new AddInterNotification(pb.getSlaveKey(), pb.getThreadKey(), 1, 1));
		System.out.println("Notification: pa NOTIFIES the thread, " + pb.getThreadKey() + " of pb\n");

		pa.notifyThreads(new AddInterNotification(pc.getSlaveKey(), pc.getThreadKeys(), 1, 1));
		System.out.println("Notification: pa NOTIFIES the " + pc.getThreadKeys().size() + " threads of pc\n");

		AddInterResponse ar = (AddInterResponse)pa.readThread(new AddInterRequest(PlayerSystem.THREADING().getPlayerSystemKey(), pb.getSlaveKey(), pb.getThreadKey(), 1, 1));
		System.out.println("1 + 1 = " + ar.getSum());
		System.out.println("Request: pa READS the thread, " + pb.getThreadKey() + " of pb\n");

		Set<TaskResponse> ars = pa.readThreads(new AddInterRequest(PlayerSystem.THREADING().getPlayerSystemKey(), pc.getSlaveKey(), pc.getThreadKeys(), 1, 1));
		for (TaskResponse entry : ars)
		{
			ar = (AddInterResponse)entry;
			System.out.println("1 + 1 = " + ar.getSum());
		}
		System.out.println("Request: pa READS the " + pc.getThreadKeys().size() + " threads of pc\n");

		System.out.println("Press any key to exit ...");
		in.nextLine();

		PlayerSystem.THREADING().dispose(2000);
		in.close();
	}

}
