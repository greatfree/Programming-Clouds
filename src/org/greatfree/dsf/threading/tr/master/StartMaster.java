package org.greatfree.dsf.threading.tr.master;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.dsf.threading.ThreadInfo;
import org.greatfree.dsf.threading.message.AddRequest;
import org.greatfree.dsf.threading.message.AddResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ThreadAssignmentException;

/*
 * The TR/tr represents the thread reading. 09/28/2019, Bing Li
 */

// Created: 09/28/2019, Bing Li
class StartMaster
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, ThreadAssignmentException
	{
		System.out.println("Thread master starting up ...");

		Master.THREADING().start(new MasterTask());

		System.out.println("Thread master started ...");

		System.out.println("Thread master assigning tasks ...");
		Set<String> threadKeys = Master.THREADING().obtainThreadKeys(2);
		for (String entry : threadKeys)
		{
			Master.THREADING().execute(entry);
			ThreadInfo.ASYNC().init(entry);
		}

		AddResponse res = Master.THREADING().assignTask(new AddRequest(ThreadInfo.ASYNC().getThreadAKey(), 1, 1), 5000);
		System.out.println("1 + 1 = " + res.getSum());
		res = Master.THREADING().assignTask(new AddRequest(ThreadInfo.ASYNC().getThreadBKey(), 1, 2), 5000);
		System.out.println("1 + 2 = " + res.getSum());
		
		Scanner in = new Scanner(System.in);
		System.out.println("Press any key to exit ...");
		in.nextLine();
		
		Master.THREADING().stop(ThreadConfig.TIMEOUT);
		in.close();
	}

}
