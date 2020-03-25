package org.greatfree.dip.player.mrtc.master;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.greatfree.concurrency.threading.Player;
import org.greatfree.concurrency.threading.PlayerSystem;
import org.greatfree.dip.threading.MRStates;
import org.greatfree.dip.threading.message.ReduceInvokeNotification;
import org.greatfree.dip.threading.mrtc.MRConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Rand;
import org.greatfree.util.Tools;

import com.google.common.collect.Sets;

// Created: 10/01/2019, Bing Li
class MRCoordinator
{
	private Map<String, Player> players;

	private MRCoordinator()
	{
	}
	
	private static MRCoordinator instance = new MRCoordinator();
	
	public static MRCoordinator THREADING()
	{
		if (instance == null)
		{
			instance = new MRCoordinator();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void createPlayer() throws ClassNotFoundException, RemoteReadException, IOException
	{
		Set<String> slaveKeys = PlayerSystem.THREADING().getAllSlaves();
//		System.out.println("MRCoordinator-createPlayer(): slaveKeys size = " + slaveKeys.size());
		Map<String, Integer> playerRequirements = new HashMap<String, Integer>();
		for (String entry : slaveKeys)
		{
			playerRequirements.put(entry, MRConfig.THREAD_COUNT_PER_SLAVE);
		}
		this.players = PlayerSystem.THREADING().create(playerRequirements);
	}
	
	public void notifyAllSlaves() throws IOException, InterruptedException
	{
		PlayerSystem.THREADING().notifyAllSlaves(this.players);
	}
	
	/*
	 * Choose initial MR slaves. 10/02/2019, Bing Li
	 */
	private Set<String> getInitialMRSlaves()
	{
		// Choose initial MR slaves. 10/02/2019, Bing Li
		Set<String> initMRSlaveKeys = Sets.newHashSet();
		if (this.players.size() <= MRConfig.MINIMUM_MUTLI_SLAVE_SIZE)
		{
			initMRSlaveKeys.addAll(this.players.keySet());
		}
		else
		{
			do
			{
				initMRSlaveKeys.addAll(Rand.getRandomSet(this.players.keySet(), Rand.getRandom(this.players.size())));
			}
			while (initMRSlaveKeys.size() < MRConfig.MINIMUM_MUTLI_SLAVE_SIZE);
		}
		return initMRSlaveKeys;
	}
	
	public void initMR(int maxHop) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		// Choose initial MR slaves. 10/02/2019, Bing Li
		Set<String> initMRSlaveKeys = this.getInitialMRSlaves();

		System.out.println("Master-initMR(): initMRSlaveKeys' size = " + initMRSlaveKeys.size());
		
		// Choose the Rendezvous Point: the slave and the thread key. 10/02/2019, Bing Li
		String rp = Rand.getRandomSetElementExcept(this.players.keySet(), PlayerSystem.THREADING().getNickKey());
		System.out.println("Master-initMR(): RP = " + PlayerSystem.THREADING().getSlaveName(rp));
		String rpThreadKey = Rand.getRandomStringInSet(this.players.get(rp).getThreadKeys());
		initMRSlaveKeys.remove(rp);
		System.out.println("Master-initMR(): removed initMRSlaveKeys' size = " + initMRSlaveKeys.size());

		// Initialize the slaves and the threads on them for the 1st hop. 10/02/2019, Bing Li
		Map<String, Set<String>> mrThreadKeys = new HashMap<String, Set<String>>();
		Set<String> threadKeys;
		// CD - Concurrency Degree. 10/02/2019, Bing Li
		int cd = 0;
		int size;
		// If more than one slaves participate in the 1st hop, choose threads randomly on each of them. 10/02/2019, Bing Li
		if (initMRSlaveKeys.size() >= MRConfig.MINIMUM_SLAVE_SIZE)
		{
			for (String entry : initMRSlaveKeys)
			{
				threadKeys = Sets.newHashSet();
				size = Rand.getRandom(this.players.get(entry).getThreadKeys().size());
				if (this.players.get(entry).getThreadKeys().size() > size)
				{
					for (String tKey : this.players.get(entry).getThreadKeys())
					{
						threadKeys.add(tKey);
						if (threadKeys.size() >= size)
						{
							break;
						}
					}
				}
				cd += threadKeys.size();
				mrThreadKeys.put(entry, threadKeys);
			}
		}
		else
		{
			// If only the RP is the slave in the 1st hop, choose threads randomly on the RP. 10/02/2019, Bing Li
			threadKeys = Sets.newHashSet();
			size = Rand.getRandom(this.players.get(rp).getThreadKeys().size());
			if (this.players.get(rp).getThreadKeys().size() > size)
			{
				for (String tKey : this.players.get(rp).getThreadKeys())
				{
					threadKeys.add(tKey);
					if (threadKeys.size() >= size)
					{
						break;
					}
				}
			}
			cd = threadKeys.size();
			mrThreadKeys.put(rp, threadKeys);
		}

		// Create a unique to represent the 1st hop RP session. 10/02/2019, Bing Li
		String mrSessionKey = Tools.generateUniqueKey();
		
		// If more than one slaves participate in the 1st hop, invoke those threads on each of them. 10/02/2019, Bing Li
		if (initMRSlaveKeys.size() >= MRConfig.MINIMUM_SLAVE_SIZE)
		{
			for (String entry : initMRSlaveKeys)
			{
				threadKeys = mrThreadKeys.get(entry);
				System.out.println("Map to Slave: " + PlayerSystem.THREADING().getSlaveName(entry) + " with " + threadKeys.size() + " threads are selected for the computing ...");
				this.players.get(entry).notifyThreads(new ReduceInvokeNotification(mrSessionKey, threadKeys, rp, rpThreadKey, 0, maxHop, cd));
			}
		}
		else
		{
			// If only the RP is the slave in the 1st hop, invoke those threads on the RP. 10/02/2019, Bing Li
			threadKeys = mrThreadKeys.get(rp);
			System.out.println(PlayerSystem.THREADING().getSlaveName(rp) + ": " + threadKeys.size() + " threads are selected for the computing ...");
			this.players.get(rp).notifyThreads(new ReduceInvokeNotification(mrSessionKey, threadKeys, rp, rpThreadKey, 0, maxHop, cd));
		}
	}

	public void continueMR(String mrSessionKey, int currentHop, int maxHop) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		MRStates.CONCURRENCY().removeCD(mrSessionKey);
		Set<String> initMRSlaveKeys = this.getInitialMRSlaves();
		
		System.out.println("initMRSlaveKeys' size = " + initMRSlaveKeys.size());
		
		// Choose the Rendezvous Point: the slave and the thread key. 10/02/2019, Bing Li
		String rp = Rand.getRandomSetElementExcept(this.players.keySet(), PlayerSystem.THREADING().getNickKey());
		System.out.println("Master-initMR(): RP = " + PlayerSystem.THREADING().getSlaveName(rp));
		String rpThreadKey = Rand.getRandomStringInSet(this.players.get(rp).getThreadKeys());
		initMRSlaveKeys.remove(rp);
		System.out.println("Master-initMR(): removed initMRSlaveKeys' size = " + initMRSlaveKeys.size());
		
		// Initialize the slaves and the threads on them for the 1st hop. 10/02/2019, Bing Li
		Map<String, Set<String>> mrThreadKeys = new HashMap<String, Set<String>>();
		Set<String> threadKeys;
		// CD - Concurrency Degree. 10/02/2019, Bing Li
		int cd = 0;
		int size;
		// If more than one slaves participate in the 1st hop, choose threads randomly on each of them. 10/02/2019, Bing Li
		if (initMRSlaveKeys.size() >= MRConfig.MINIMUM_SLAVE_SIZE)
		{
			for (String entry : initMRSlaveKeys)
			{
				threadKeys = Sets.newHashSet();
				size = Rand.getRandom(this.players.get(entry).getThreadKeys().size());
				if (this.players.get(entry).getThreadKeys().size() > size)
				{
					for (String tKey : this.players.get(entry).getThreadKeys())
					{
						threadKeys.add(tKey);
						if (threadKeys.size() >= size)
						{
							break;
						}
					}
				}
				cd += threadKeys.size();
				mrThreadKeys.put(entry, threadKeys);
			}
		}
		else
		{
			// If only the RP is the slave in the next hop, choose threads randomly on the RP. 10/02/2019, Bing Li
			threadKeys = Sets.newHashSet();
			size = Rand.getRandom(this.players.get(rp).getThreadKeys().size());
			if (this.players.get(rp).getThreadKeys().size() > size)
			{
				for (String tKey : this.players.get(rp).getThreadKeys())
				{
					threadKeys.add(tKey);
					if (threadKeys.size() >= size)
					{
						break;
					}
				}
			}
			cd = threadKeys.size();
			mrThreadKeys.put(rp, threadKeys);
		}
		
		// If more than one slaves participate in the next hop, invoke those threads on each of them. 10/02/2019, Bing Li
		if (initMRSlaveKeys.size() >= MRConfig.MINIMUM_SLAVE_SIZE)
		{
			for (String entry : initMRSlaveKeys)
			{
				threadKeys = mrThreadKeys.get(entry);
				System.out.println("Map to Slave: " + PlayerSystem.THREADING().getSlaveName(entry) + " with " + threadKeys.size() + " threads are selected for the computing ...");
				this.players.get(entry).notifyThreads(new ReduceInvokeNotification(mrSessionKey, threadKeys, rp, rpThreadKey, currentHop, maxHop, cd));
			}
		}
		else
		{
			// If only the RP is the slave in the next hop, invoke those threads on the RP. 10/02/2019, Bing Li
			threadKeys = mrThreadKeys.get(rp);
			System.out.println(PlayerSystem.THREADING().getSlaveName(rp) + ": " + threadKeys.size() + " threads are selected for the computing ...");
			this.players.get(rp).notifyThreads(new ReduceInvokeNotification(mrSessionKey, threadKeys, rp, rpThreadKey, currentHop, maxHop, cd));
		}
	}
}
