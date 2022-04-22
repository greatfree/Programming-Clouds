package org.greatfree.framework.threading.mrtc.master;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.greatfree.concurrency.threading.Distributer;
import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.threading.MRStates;
import org.greatfree.framework.threading.message.ReduceNotification;
import org.greatfree.framework.threading.mrtc.MRConfig;
import org.greatfree.framework.threading.mrtc.NodeIDs;
import org.greatfree.message.container.Notification;
import org.greatfree.util.Rand;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

// Created: 09/24/2019, Bing Li
class Master
{
	private Distributer master;

	private Master()
	{
	}
	
	private static Master instance = new Master();
	
	public static Master THREADING()
	{
		if (instance == null)
		{
			instance = new Master();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		Set<String> slaveKeys = NodeIDs.ID().getAllSlaves();
		for (String entry : slaveKeys)
		{
			this.master.killAll(entry, timeout);
			this.master.shutdownSlave(entry, timeout);
		}
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.master.stop(timeout);
	}
	
	public void start() throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		this.master = new Distributer.DistributerBuilder()
				.name(NodeIDs.ID().getLocalName())
				.port(ThreadConfig.THREAD_PORT)
				.isMaster(true)
				.task(new MRMasterTask())
				.build();
//		this.master.start(NodeIDs.ID().getLocalKey(), new MRMasterTask(), true);
//		this.master.start(new MRMasterTask(), true);
		this.master.start();
	}
	
	/*
	 * Get all of the slaves. 12/19/2019, Bing Li
	 */
	public Set<String> getSlaveKeys()
	{
		return this.master.getSlaveKeys();
	}
	
	/*
	 * Get the slaves' names. 12/19/2019, Bing Li
	 */
	public Map<String, String> getSlaveNames() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Map<String, String> names = this.master.getSlaveNames();
		names.remove(NodeIDs.ID().getLocalKey());
		return names;
	}
	
	/*
	 * Create a certain number of threads on the specified slaves. 12/19/2019, Bing Li
	 */
	public Map<String, Set<String>> obtainThreads(Map<String, Integer> threadCounts) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.master.reuseThreads(threadCounts);
	}
	
	public String obtainThread(String slaveKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.master.reuseThread(slaveKey);
	}
	
	public void notifySlave(String slaveKey, Notification notification) throws IOException, InterruptedException
	{
		this.master.syncNotifySlave(slaveKey, notification);
	}

	/*
	 * Initiate the distributed Map/Reduce with the max hop count. 12/19/2019, Bing Li
	 */
//	public void initMR(String path, int currentHop, int maxHop) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
//	public void initMR(int currentHop, int maxHop) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	public void initMR(int maxHop) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
//		System.out.println("1) Master-initMR(): maxHop = " + maxHop);

		 // As a common sense, the minimum number of slaves for a concurrency game is 2. Select the random number, which should be NOT less than 2, of slaves from the available slaves. 01/08/2020, Bing Li
		Set<String> initMRSlaveKeys = NodeIDs.ID().getInitMRSlaves();
		
		System.out.println("Master-initMR(): initMRSlaveKeys' size = " + initMRSlaveKeys.size());

		// Select one slave randomly as the rendezvous point (RP) from the slaves. 01/08/2020, Bing Li
		String rp = NodeIDs.ID().getRandomSlaveKey();
//		String rpThreadKey = this.master.obtainThread(rp);
		System.out.println("Master-initMR(): RP = " + NodeIDs.ID().getSlaveName(rp));
		
		// Select one thread randomly on the RP as the RP thread. 01/08/2020, Bing Li
		String rpThreadKey = NodeIDs.ID().getThreadKey(rp);
//		System.out.println("RP = " + rp);

		/*
		 * Perform the initial mapping operation. 09/24/2019, Bing Li
		 */
		
		// If the RP is one member of the initial slaves, it should be removed. 01/08/2020, Bing Li
		initMRSlaveKeys.remove(rp);
		System.out.println("Master-initMR(): removed initMRSlaveKeys' size = " + initMRSlaveKeys.size());

		Map<String, Set<String>> mrThreadKeys = new HashMap<String, Set<String>>();
		Set<String> threadKeys;
		
		// The CD stands for the Concurrency Degree of the MR game. 01/08/2020, Bing Li
		int cd = 0;
		
		// Detect whether the number of slaves reaches in the minimum size (1). 01/08/2020, Bing Li
		if (initMRSlaveKeys.size() >= MRConfig.MINIMUM_SLAVE_SIZE)
		{
			for (String entry : initMRSlaveKeys)
			{
				// Obtain a random number of threads from each initial slave. 01/08/2020, Bing Li
				threadKeys = NodeIDs.ID().getThreadKeys(entry, Rand.getRandom(NodeIDs.ID().getThreadSize(entry)));
				// Accumulate the CD on the thread numbers. 01/08/2020, Bing Li
				cd += threadKeys.size();
				// Initialize the starting up threads on each slave. 01/08/2020, Bing Li
				mrThreadKeys.put(entry, threadKeys);
			}
		}
		else
		{
			// If the slave number is less than 1, it indicates that only the selected RP participates the MR game. So it is required to obtain a random number of threads on the RP. 01/08/2020, Bing Li
			threadKeys = NodeIDs.ID().getThreadKeys(rp, Rand.getRandom(NodeIDs.ID().getThreadSize(rp)));
			// Initialize the starting up threads on the RP. 01/08/2020, Bing Li
			mrThreadKeys.put(rp, threadKeys);
			// The number of threads is equal to the CD in the case. 01/08/2020, Bing Li
			cd = threadKeys.size();
		}

		// Generate an unique key to represent the round of the MR game. 01/08/2020, Bing Li
		String mrSessionKey = Tools.generateUniqueKey();
		// Detect whether the number of slaves is NOT less then the minimum slave size (1). 01/08/2020, Bing Li
		if (initMRSlaveKeys.size() >= MRConfig.MINIMUM_SLAVE_SIZE)
		{
			// For each selected initial slave, start the Map and send them the Reduce instructions such that their results can be merged. 01/08/2020, Bing Li
			for (String entry : initMRSlaveKeys)
			{
				// Obtain the threads from one slave. 01/08/2020, Bing Li
				threadKeys = mrThreadKeys.get(entry);
				System.out.println("Map to Slave: " + NodeIDs.ID().getSlaveName(entry) + " with " + threadKeys.size() + " threads are selected for the computing ...");
				
				// Detect the state of each thread and execute them if applicable. 01/08/2020, Bing Li
				for (String tKey : threadKeys)
				{
					// Detect whether one thread is alive. 01/08/2020, Bing Li
					if (!this.master.isAlive(entry, tKey))
					{
						// Execute the thread if it is NOT alive. 01/08/2020, Bing Li
						this.master.execute(entry, tKey);
					}
				}
//				System.out.println("2) Master-initMR(): maxHop = " + maxHop);
//				this.master.assignTask(entry, new ReduceNotification(Tools.generateUniqueKey(), threadKeys, UtilConfig.EMPTY_STRING, ThreadConfig.TIMEOUT, rp, rpThreadKey, currentHop, maxHop, cd));
				
				// For each initial MR slave, start the Map and assign Reduce task to them. 01/08/2020, Bing Li
				this.master.assignTask(entry, new ReduceNotification(mrSessionKey, threadKeys, ThreadConfig.TIMEOUT, rp, rpThreadKey, 0, maxHop, cd));
			}
		}
		else
		{
			// If the number of initial slaves is less than 2, it indicates that only the selected RP participates the MR for this round. 01/08/2020, Bing Li
			threadKeys = mrThreadKeys.get(rp);
			System.out.println(NodeIDs.ID().getSlaveName(rp) + ": " + threadKeys.size() + " threads are selected for the computing ...");

			// Detect the state of each thread and execute them if applicable. 01/08/2020, Bing Li
			for (String tKey : threadKeys)
			{
				// Detect whether one thread is alive. 01/08/2020, Bing Li
				if (!this.master.isAlive(rp, tKey))
				{
					// Execute the thread if it is NOT alive. 01/08/2020, Bing Li
					this.master.execute(rp, tKey);
				}
			}
//			this.master.assignTask(rp, new ReduceNotification(Tools.generateUniqueKey(), threadKeys, UtilConfig.EMPTY_STRING, ThreadConfig.TIMEOUT, rp, rpThreadKey, currentHop, maxHop, cd));

			// Since only the selected RP participates this round of MR, start the Map and assign Reduce task to it only. 01/08/2020, Bing Li
			this.master.assignTask(rp, new ReduceNotification(mrSessionKey, threadKeys, ThreadConfig.TIMEOUT, rp, rpThreadKey, 0, maxHop, cd));
		}
	}

	/*
	 * Start a new hop for the round of the MR game. 01/10/2020, Bing Li
	 */
//	public void continueMR(String mrKey, String path, int currentHop, int maxHop) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	public void continueMR(String mrKey, int currentHop, int maxHop) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		// Clear the current MR concurrency degree since it is out of date. 01/20/2020, Bing Li
		MRStates.CONCURRENCY().removeCD(mrKey);
		// Get the initial slave randomly. 01/10/2020, Bing Li
		Set<String> initMRSlaveKeys = NodeIDs.ID().getInitMRSlaves();
		
		System.out.println("initMRSlaveKeys' size = " + initMRSlaveKeys.size());

		// Select one slave randomly as the RP. 01/10/2020, Bing Li
		String rp = NodeIDs.ID().getRandomSlaveKey();
//		String rpThreadKey = this.master.obtainThread(rp);
		
		// Select one thread as the RP thread from the RP. 01/10/2020, Bing Li
		String rpThreadKey = NodeIDs.ID().getThreadKey(rp);
//		System.out.println("RP = " + rp);

		/*
		 * Perform the initial mapping operation. 09/24/2019, Bing Li
		 */
		
		// If the RP is one member of the initial slaves for Map operation, it should be removed. 01/10/2020, Bing Li
		initMRSlaveKeys.remove(rp);
		
		Map<String, Set<String>> mrThreadKeys = new HashMap<String, Set<String>>();
		Set<String> threadKeys;
		int cd = 0;
		// Detect whether the number of the initial slaves reaches the minimum reasonable number of MR. 01/10/2020, Bing Li
		if (initMRSlaveKeys.size() >= MRConfig.MINIMUM_SLAVE_SIZE)
		{
			// If the number of the initial slaves is large enough, select threads from the slaves randomly and calculate the maximum CD for the hop. 01/10/2020, Bing Li
			for (String entry : initMRSlaveKeys)
			{
				// Select threads from each slave randomly for the hop. 01/10/2020, Bing Li
				threadKeys = NodeIDs.ID().getThreadKeys(entry, Rand.getRandom(NodeIDs.ID().getThreadSize(entry)));
				// Calculate the maximum CD for the hop. 01/10/2020, Bing Li
				cd += threadKeys.size();
				// Keep the threads participating the hop. 01/10/2020, Bing Li
				mrThreadKeys.put(entry, threadKeys);
			}
		}
		else
		{
			// If the number of the initial slaves is less than the minimum reasonable number, it represents that only the master and the RP participate the hop of the round. So, it is required to get threads from the RP. 01/10/2020, Bing Li
			threadKeys = NodeIDs.ID().getThreadKeys(rp, Rand.getRandom(NodeIDs.ID().getThreadSize(rp)));
			// Keep the threads participating the hop. 01/10/2020, Bing Li
			mrThreadKeys.put(rp, threadKeys);
			// The maximum CD for the hop is identical to the number of selected threads of the RP. 01/10/2020, Bing Li
			cd = threadKeys.size();
		}

		// Check whether the number of the initial slaves of the hop in the round is larger than the minimum reasonable number of MR or not. 01/10/2020, Bing Li
		if (initMRSlaveKeys.size() >= MRConfig.MINIMUM_SLAVE_SIZE)
		{
			// For each participant, assign tasks. 01/10/2020, Bing Li
			for (String entry : initMRSlaveKeys)
			{
				// Get the threads of one participating slave. 01/10/2020, Bing Li
				threadKeys = mrThreadKeys.get(entry);
				System.out.println(NodeIDs.ID().getSlaveName(entry) + ": " + threadKeys.size() + " threads are selected for the computing ...");
				// Assign the Reduce task to each thread of one slave. 01/10/2020, Bing Li
				for (String tKey : threadKeys)
				{
					// Check whether the thread is alive. 01/10/2020, Bing Li
					if (!this.master.isAlive(entry, tKey))
					{
						// Execute the thread if it is NOT alive. 01/10/2020, Bing Li
						this.master.execute(entry, tKey);
					}
				}
//				this.master.assignTask(entry, new ReduceNotification(mrKey, threadKeys, UtilConfig.EMPTY_STRING, ThreadConfig.TIMEOUT, rp, rpThreadKey, currentHop, maxHop, cd));
				
				// Assign the Reduce task to the alive thread. 01/10/2020, Bign Li
				this.master.assignTask(entry, new ReduceNotification(mrKey, threadKeys, ThreadConfig.TIMEOUT, rp, rpThreadKey, currentHop, maxHop, cd));
			}
		}
		else
		{
			// In the case, only the master and the RP participate the hop of the round. Get threads of the RP. 01/10/2020, Bing Li
			threadKeys = mrThreadKeys.get(rp);
			System.out.println(NodeIDs.ID().getSlaveName(rp) + ": " + threadKeys.size() + " threads are selected for the computing ...");
			// For each thread of the RP, assign the Reduce task. 01/10/2020, Bing Li
			for (String tKey : threadKeys)
			{
				// Check whether the thread is alive or not. 01/10/2020, Bing Li
				if (!this.master.isAlive(rp, tKey))
				{
					// If the thread is NOT alive, execute the thread. 01/10/2020, Bing Li
					this.master.execute(rp, tKey);
				}
			}
//			this.master.assignTask(rp, new ReduceNotification(mrKey, threadKeys, UtilConfig.EMPTY_STRING, ThreadConfig.TIMEOUT, rp, rpThreadKey, currentHop, maxHop, cd));
			
			// Assign the Reduce task to the thread. 01/10/2020, Bing Li
			this.master.assignTask(rp, new ReduceNotification(mrKey, threadKeys, ThreadConfig.TIMEOUT, rp, rpThreadKey, currentHop, maxHop, cd));
		}
	}
}
