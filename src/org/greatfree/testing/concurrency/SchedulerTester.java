package org.greatfree.testing.concurrency;

import java.util.Scanner;
import java.util.concurrent.ScheduledFuture;

import org.greatfree.chat.ChatConfig;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.p2p.RegistryConfig;

/**
 * 
 * @author Bing Li
 * 
 * 02/03/2022, Bing Li
 *
 */
class SchedulerTester
{

	public static void main(String[] args) throws InterruptedException
	{
		Scanner in = new Scanner(System.in);

		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

		System.out.println("Start to say hello ...");
		in.nextLine();
		
		Perioder p = new Perioder();
		
		ScheduledFuture<?> s = Scheduler.GREATFREE().submit(p, ChatConfig.CHAT_POLLING_DELAY, 2000);

		System.out.println("Stop to say hello ...");
		in.nextLine();
//		Scheduler.GREATFREE().cancel(p);
		s.cancel(true);

		System.out.println("Start to say hello AGAIN ...");
		in.nextLine();

		Scheduler.GREATFREE().submit(p, ChatConfig.CHAT_POLLING_DELAY, ChatConfig.CHAT_POLLING_PERIOD);

		System.out.println("Stop to say hello AGAIN ...");
		in.nextLine();

		Scheduler.GREATFREE().shutdown(RegistryConfig.SCHEDULER_SHUTDOWN_TIMEOUT);
		in.close();
	}

}
