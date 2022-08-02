package org.greatfree.testing.server;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.chat.ChatConfig;
import org.greatfree.chat.message.ShutdownServerNotification;
import org.greatfree.client.CSClient;
import org.greatfree.data.ClientConfig;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.testing.message.TNNotification;
import org.greatfree.util.Rand;

// Created: 04/10/2020, Bing Li
class StartTNClient
{

	public static void main(String[] args) throws IOException, InterruptedException
	{
		Scanner in = new Scanner(System.in);

		CSClient client = new CSClient.CSClientBuilder()
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.clientIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.clientIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.clientMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventQueueWaitTime(RegistryConfig.ASYNC_EVENT_QUEUE_WAIT_TIME)
//				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.asyncSchedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.build();
		
		TNNotification n;
		for (int i = 0; i < 1000; i++)
		{
			n = new TNNotification("TN" + Rand.getNextLong());
			client.syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, n);
			System.out.println("One notification is sent ..." + n.getMessage());
			Thread.sleep(2000);
		}
		
		client.syncNotify(ChatConfig.CHAT_SERVER_ADDRESS, ChatConfig.CHAT_SERVER_PORT, new ShutdownServerNotification());
		
		System.out.println("Press enter to exit ...");
		in.nextLine();
		
		client.dispose();
		in.close();
	}

}
