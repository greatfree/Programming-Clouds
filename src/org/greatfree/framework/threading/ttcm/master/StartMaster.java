package org.greatfree.framework.threading.ttcm.master;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.threading.ThreadInfo;
import org.greatfree.framework.threading.message.PingNotification;

/*
 * The "TTCM/ttcm" represent "Two-Thread Collaboration for Multiple-tasks". 09/13/2019, Bing Li
 */

// Created: 09/13/2019, Bing Li
class StartMaster
{
	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		System.out.println("Thread master starting up ...");

//		ActorMaster.THREADING().start(ThreadConfig.MASTER, ThreadConfig.THREAD_PORT, ThreadConfig.SLAVE, new MasterTask());
		Master.THREADING().start(new MasterTask());

		System.out.println("Thread master started ...");

//		ThreadMaster.MASTER().assignTasks();
		System.out.println("Thread master assigning tasks ...");
//		Set<String> threadKeys = ActorMaster.THREADING().obtainThreads(2);
		Set<String> threadKeys = Master.THREADING().obtainThreadKeys(2);
		for (String entry : threadKeys)
		{
//			ActorMaster.THREADING().execute(entry);
			Master.THREADING().execute(entry);
			ThreadInfo.ASYNC().init(entry);
		}

//		ActorMaster.THREADING().assignTask(new PingNotification(ThreadInfo.ASYNC().getThreadAKey(), ThreadInfo.ASYNC().getThreadA() + " PINGS", 1000));
		Master.THREADING().assignTask(new PingNotification(ThreadInfo.ASYNC().getThreadAKey(), ThreadInfo.ASYNC().getThreadA() + " PINGS", 1000));
//		ActorMaster.CLIENT().assignTask(new PongNotification(ThreadInfo.ASYNC().getThreadBKey(), ThreadInfo.ASYNC().getThreadB() + " PONGS", 1000));
		
//		ActorMaster.CLIENT().execute(ThreadInfo.ASYNC().getThreadAKey());
		
		Scanner in = new Scanner(System.in);
		System.out.println("Press any key to exit ...");
		in.nextLine();
		
//		ActorMaster.THREADING().killAll(ThreadConfig.TIMEOUT);
//		ActorMaster.THREADING().shutdown(ThreadConfig.TIMEOUT);
//		ActorMaster.THREADING().stop(ThreadConfig.TIMEOUT);
		Master.THREADING().stop(ThreadConfig.TIMEOUT);
		in.close();
	}
}
