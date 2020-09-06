package org.greatfree.dsf.player.mnti.master;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import org.greatfree.concurrency.threading.Player;
import org.greatfree.concurrency.threading.PlayerSystem;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.concurrency.threading.message.TaskResponse;
import org.greatfree.dsf.threading.message.AddInterNotification;
import org.greatfree.dsf.threading.message.AddInterRequest;
import org.greatfree.dsf.threading.message.AddInterResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ThreadAssignmentException;
import org.greatfree.util.Rand;

import com.google.common.collect.Sets;

// Created: 10/07/2019, Bing Li
class StartMaster
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, ThreadAssignmentException
	{
		System.out.println("Player master starting up ...");
		MasterTask task = new MasterTask();
		PlayerSystem.THREADING().startMaster(ThreadConfig.THREAD_PORT, task);
		System.out.println("Player master started ...");
		
		Set<String> slaves = PlayerSystem.THREADING().getAllSlaves();
		System.out.println("slaves size = " + slaves.size());

		String slaveA = Rand.getRandomStringInSet(slaves);
		System.out.println("slaveA = " + slaveA);
		String slaveB = Rand.getRandomSetElementExcept(slaves, slaveA);
		System.out.println("slaveB = " + slaveB);
		Set<String> selectedKeys = Sets.newHashSet();
		selectedKeys.add(slaveA);
		selectedKeys.add(slaveB);
		String slaveC = Rand.getRandomSetElementExcept(slaves, selectedKeys);
		System.out.println("slaveC = " + slaveC);
		
		Player pa = PlayerSystem.THREADING().create(slaveA);
		Player pb = PlayerSystem.THREADING().create(slaveB);
		Player pc = PlayerSystem.THREADING().create(slaveC, 10);
		
		pa.notifyThreads(new AddInterNotification(pb.getSlaveKey(), pb.getThreadKey(), 1, 1));
		System.out.println("Notification: pa NOTIFIES the thread, " + pb.getThreadKey() + " of pb\n");

		pb.notifyThreads(new AddInterNotification(pa.getSlaveKey(), pa.getThreadKey(), 1, 1));
		System.out.println("Notification: pb NOTIFIES the thread, " + pb.getThreadKey() + " of pa\n");
		
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

		Scanner in = new Scanner(System.in);
		System.out.println("Press any key to exit ...");
		in.nextLine();

		PlayerSystem.THREADING().dispose(2000);
		in.close();
	}

}
