package org.greatfree.dip.threading.ttc.dt.master;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.dip.threading.ThreadInfo;
import org.greatfree.dip.threading.message.PrintTaskNotification;
import org.greatfree.exceptions.RemoteReadException;

/*
 * TTC/ttc represents "Two-Thread Collaboration". 11/17/2019, Bing Li
 */

// Created: 09/14/2019, Bing Li
class StartMaster
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		System.out.println("Thread master starting up ...");

		/*
		Free master = new Free.FreeBuilder()
				.actorName(ThreadConfig.MASTER)
				.actorPort(ThreadConfig.THREAD_PORT)
				.build();
				*/

//		ActorMaster.THREADING().start(ThreadConfig.MASTER, ThreadConfig.THREAD_PORT, ThreadConfig.SLAVE, new MasterTask());
		
//		master.startAsMaster(ThreadConfig.SLAVE, new MasterTask());
		
		Master.THREADING().start(new MasterTask());

		System.out.println("Thread master started ...");

//		Set<String> threadKeys = ActorMaster.THREADING().obtainThreads(2);
//		Set<String> threadKeys = master.obtainThreads(2);
		Set<String> threadKeys = Master.THREADING().obtainThreadKeys(2);
		for (String entry : threadKeys)
		{
//			ActorMaster.THREADING().execute(entry);
//			master.execute(entry);
			Master.THREADING().execute(entry);
			ThreadInfo.ASYNC().init(entry);
		}

		/*
		for (String entry : threadKeys)
		{
//			ActorMaster.THREADING().assignTask(new PrintTaskNotification(entry, "I am " + ThreadInfo.ASYNC().getThreadName(entry), ThreadConfig.TIMEOUT));
//			master.assignTask(new PrintTaskNotification(entry, "I am " + ThreadInfo.ASYNC().getThreadName(entry), ThreadConfig.TIMEOUT));
			Master.THREADING().assignTask(new PrintTaskNotification(entry, "I am " + ThreadInfo.ASYNC().getThreadName(entry), ThreadConfig.TIMEOUT));
		}
		*/
		Master.THREADING().assignTask(new PrintTaskNotification(ThreadInfo.ASYNC().getThreadAKey(), "I am " + ThreadInfo.ASYNC().getThreadA(), ThreadConfig.TIMEOUT));

//		ActorMaster.CLIENT().execute(ThreadInfo.ASYNC().getThreadAKey());
		
		Scanner in = new Scanner(System.in);
		System.out.println("Press any key to exit ...");
		in.nextLine();
		
//		ActorMaster.THREADING().killAll(1000);
//		ActorMaster.THREADING().shutdown(1000);
//		ActorMaster.THREADING().stop(1000);
		
//		master.killAll(ThreadConfig.TIMEOUT);
//		master.shutdownSlave(ThreadConfig.TIMEOUT);
//		master.stop(ThreadConfig.TIMEOUT);
		Master.THREADING().stop(ThreadConfig.TIMEOUT);
		in.close();
	}

}
