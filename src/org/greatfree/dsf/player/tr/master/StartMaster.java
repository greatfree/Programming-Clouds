package org.greatfree.dsf.player.tr.master;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;

import org.greatfree.concurrency.threading.Player;
import org.greatfree.concurrency.threading.PlayerSystem;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.concurrency.threading.message.TaskResponse;
import org.greatfree.dsf.threading.message.AddRequest;
import org.greatfree.dsf.threading.message.AddResponse;
import org.greatfree.dsf.threading.message.MultiplyRequest;
import org.greatfree.dsf.threading.message.MultiplyResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ThreadAssignmentException;
import org.greatfree.util.Time;

// Created: 09/30/2019, Bing Li
class StartMaster
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, ThreadAssignmentException
	{
		System.out.println("Player master starting up ...");

		MasterTask task = new MasterTask();
		PlayerSystem.THREADING().startMaster(ThreadConfig.MASTER, ThreadConfig.THREAD_PORT, ThreadConfig.SLAVE, task);

		System.out.println("Player master started ...");

		Player asker = PlayerSystem.THREADING().create();
		Player askers = PlayerSystem.THREADING().create(10);

		Date startTime = Calendar.getInstance().getTime();
		AddResponse ar = (AddResponse)asker.readThread(new AddRequest(asker.getThreadKey(), 1, 1));
		Date endTime = Calendar.getInstance().getTime();
		
		System.out.println("It took " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms");
		
		System.out.println("1 + 1 = " + ar.getSum());
		startTime = Calendar.getInstance().getTime();
		ar = (AddResponse)asker.readThread(new AddRequest(asker.getThreadKey(), 1, 2));
		endTime = Calendar.getInstance().getTime();
		System.out.println("1 + 2 = " + ar.getSum());

		System.out.println("It took " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms");

		System.out.println("============================");
		
		startTime = Calendar.getInstance().getTime();
		Set<TaskResponse> ars = askers.readThreads(new AddRequest(askers.getThreadKeys(), 1, 1));
		endTime = Calendar.getInstance().getTime();
		
		for (TaskResponse entry : ars)
		{
			ar = (AddResponse)entry;
			System.out.println("1 + 1 = " + ar.getSum());
		}

		System.out.println("It took " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms");

		System.out.println("============================");
		
		startTime = Calendar.getInstance().getTime();
		MultiplyResponse mr = (MultiplyResponse)asker.readThread(new MultiplyRequest(asker.getThreadKey(), 7, 7));
		endTime = Calendar.getInstance().getTime();
		System.out.println("7 * 7 = " + mr.getResult());
		System.out.println("It took " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms");

		startTime = Calendar.getInstance().getTime();
		ars = askers.readThreads(new MultiplyRequest(askers.getThreadKeys(), 99, 99));
		endTime = Calendar.getInstance().getTime();
		
		for (TaskResponse entry : ars)
		{
			mr = (MultiplyResponse)entry;
			System.out.println("9 * 9 = " + mr.getResult());
		}

		System.out.println("It took " + Time.getTimespanInMilliSecond(endTime, startTime) + "ms");

		System.out.println("============================");

		Scanner in = new Scanner(System.in);
		System.out.println("Press any key to exit ...");
		in.nextLine();

		PlayerSystem.THREADING().dispose(2000);
		in.close();
	}

}
