package org.greatfree.framework.threading.ttc.master;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.exceptions.RemoteReadException;

/*
 * The "TTC/ttc" represent "Two-Thread Collaboration". 09/13/2019, Bing Li
 */

// Created: 09/11/2019, Bing Li
class StartMaster
{
	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		System.out.println("Thread master starting up ...");

		Master.MASTER().start(ThreadConfig.MASTER, ThreadConfig.SLAVE, new MasterTask());

		System.out.println("Thread master started ...");

		Master.MASTER().assignTasks();
//		Master.MASTER().execute(ThreadInfo.ASYNC().getThreadAKey());
		
		Scanner in = new Scanner(System.in);
		System.out.println("Press any key to exit ...");
		in.nextLine();
		
		Master.MASTER().killAll();
		Master.MASTER().shutdown();
		Master.MASTER().stop(1000);
		in.close();
	}
}

