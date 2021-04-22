package org.greatfree.framework.threading.mrtc.master;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.concurrency.threading.message.AllSlavesNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.threading.mrtc.MRConfig;
import org.greatfree.framework.threading.mrtc.NodeIDs;

// Created: 09/22/2019, Bing Li
class StartMaster
{
	public static void main(String[] args) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		System.out.println("MR Master, " + NodeIDs.ID().getLocalName() + ", starting up ...");

		// Start the master. 01/08/2020, Bing Li
		Master.THREADING().start();
		
		System.out.println("MR Master, " + NodeIDs.ID().getLocalName() + ", started ...");

		/*
		 * Get all of the slaves. 12/19/2019, Bing Li
		 */
		Set<String> slaveKeys = Master.THREADING().getSlaveKeys();
		System.out.println("slaveKeys size = " + slaveKeys.size());
		NodeIDs.ID().setAllSlaveIDs(slaveKeys);

		/*
		 * Show the slaves' names. 12/19/2019, Bing Li
		 */
		Map<String, String> names = Master.THREADING().getSlaveNames();
		System.out.println("slaveKeys names = " + names.size());
		System.out.println("--------------------------");
		for (Map.Entry<String, String> entry : names.entrySet())
		{
			System.out.println(entry.getValue());
			NodeIDs.ID().addSlaveName(entry.getKey(), entry.getValue());
		}
		System.out.println("--------------------------");

		/*
		 * Generate the threads requirements for each slave. 12/19/2019, Bing Li
		 */
		Map<String, Integer> threadCounts = new HashMap<String, Integer>();
		for (String entry : slaveKeys)
		{
			threadCounts.put(entry, MRConfig.THREAD_COUNT_PER_SLAVE);
		}

		/*
		 * Create threads on those slaves. 12/19/2019, Bing Li
		 */
		Map<String, Set<String>> threadKeys = Master.THREADING().obtainThreads(threadCounts);
		NodeIDs.ID().setThreadKeys(threadKeys);

		/*
		 * Let all of the slaves know about the threads of each slave such that they can perform Map/Reduce among them. 12/19/2019, Bing Li
		 */
		for (String entry : slaveKeys)
		{
			Master.THREADING().notifySlave(entry, new AllSlavesNotification(NodeIDs.ID().getLocalName(), slaveKeys, threadKeys, names));
		}

		/*
		 * Initiate the distributed Map/Reduce with the max hop count. 12/19/2019, Bing Li
		 */
//		Master.THREADING().initMR(UtilConfig.EMPTY_STRING, 0, 1);
//		Master.THREADING().initMR(UtilConfig.EMPTY_STRING, 0, 2);
//		Master.THREADING().initMR(0, 1);
//		Master.THREADING().initMR(0, 2);
//		Master.THREADING().initMR(0, 3);
//		Master.THREADING().initMR(0, 4);
		Master.THREADING().initMR(10);
//		Master.THREADING().initMR(0, 100);

		Scanner in = new Scanner(System.in);
		System.out.println("Press any key to exit ...");
		in.nextLine();

		Master.THREADING().stop(ThreadConfig.TIMEOUT);
		in.close();
	}
}
