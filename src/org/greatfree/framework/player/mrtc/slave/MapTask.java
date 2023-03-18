package org.greatfree.framework.player.mrtc.slave;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.greatfree.concurrency.threading.Player;
import org.greatfree.concurrency.threading.PlayerSystem;
import org.greatfree.concurrency.threading.ThreadTask;
import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.concurrency.threading.message.TaskRequest;
import org.greatfree.concurrency.threading.message.TaskResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.threading.MRStates;
import org.greatfree.framework.threading.TaskConfig;
import org.greatfree.framework.threading.message.MRFinalNotification;
import org.greatfree.framework.threading.message.MRPartialNotification;
import org.greatfree.framework.threading.message.MapInvokeNotification;
import org.greatfree.framework.threading.message.ReduceInvokeNotification;
import org.greatfree.framework.threading.mrtc.MRConfig;
import org.greatfree.util.Rand;

// Created: 10/02/2019, Bing Li
class MapTask implements ThreadTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.player.mrtc.slave");

	@Override
	public String getKey()
	{
		return TaskConfig.MAP_TASK_KEY;
	}

	@Override
	public void processNotification(String threadKey, TaskNotification notification)
	{
	}

	@Override
	public void processNotification(String threadKey, TaskInvokeNotification notification)
	{
		log.info("=> MAP_TASK: local ID = " + PlayerSystem.THREADING().getNickName());
		// Get the Map notification from one slave that just accomplishes its concurrent task. 01/10/2020, Bing Li
		MapInvokeNotification mn = (MapInvokeNotification)notification;
		log.info("=> MAP_TASK: CD = " + mn.getCD());
		log.info("=> MAP_TASK: current task = " + mn.getCurrentHop() + "/" + mn.getMaxHop());

		// Accumulate the path, i.e., the result of one concurrent task, from the slave. 01/10/2020, Bing Li
		MRStates.CONCURRENCY().incrementPath(mn.getMRSessionKey(), mn.getPath());

		log.info(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: path:");
		log.info("============================================");
		log.info(MRStates.CONCURRENCY().getPath(mn.getMRSessionKey()));
		log.info("============================================");

		// Increment the current CD after accumulating the result of one concurrent task from a slave. 01/10/2020, Bing Li
		MRStates.CONCURRENCY().incrementCD(mn.getMRSessionKey());
		// Detect whether the CD reaches the maximum CD or not. 01/10/2020, Bing Li
		if (MRStates.CONCURRENCY().isCDFulfilled(mn.getMRSessionKey(), mn.getCD()))
		{
			// Once if the maximum concurrency degree (CD) is reached, it indicates that the hop of the round is done. So the current CD is deprecated. 01/10/2020, Bing Li
			MRStates.CONCURRENCY().removeCD(mn.getMRSessionKey());
			// Accumulate the path to track the MR game. 01/10/2020, Bing Li
			MRStates.CONCURRENCY().incrementPath(mn.getMRSessionKey(), MRConfig.MAP_TASK + PlayerSystem.THREADING().getNickName());
			log.info(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: CD = " + mn.getCD() + " is fulfilled ...");

			// Check whether the current hop is NOT greater than the predefined maximum hop. 01/10/2020, Bing Li
			if (mn.getCurrentHop() <= mn.getMaxHop())
			{
				log.info(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: MAP continued ...");

				/* In the case, the MR game is still NOT done yet. The current/local slave is required to keep play the game for the next hop of the round.
				 * For that, the local slave gets slaves randomly from all of the potential participants. The local slave should NOT be one member of the newly selected slaves since it plays as the initiator of the next hop. 01/10/2020, Bing Li
				 */
				Set<String> slaveKeys = PlayerSystem.THREADING().getSlavesExceptLocal(MRConfig.MINIMUM_MUTLI_SLAVE_SIZE);
				log.info(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: slaveKeys size = " + slaveKeys.size());

				// Get the RP from the newly selected slaves. 01/10/2020, Bing Li
				Player rp = PlayerSystem.THREADING().getSlaveExceptFrom(slaveKeys);
				// Check whether the newly selected RP is the local slave or not. It seems it is NOT necessary to do that since the local slave is excluded from the slaves for the coming hop. 01/10/2020, Bing Li
				if (!rp.getSlaveKey().equals(PlayerSystem.THREADING().getNickKey()))
				{
					try
					{
						// If the RP is not the local slave, it is reasonable to start the new hop. Meanwhile, the local slave notifies the master about the partially completed concurrent task. 01/10/2020, Bing Li
						PlayerSystem.THREADING().asyncNotifyMaster(new MRPartialNotification(mn.getMRSessionKey(), MRStates.CONCURRENCY().getPath(mn.getMRSessionKey())));
						// Remove the path, which tracks the MR tasks of the previous hop, since it is deprecated. 01/10/2020, Bing Li
						MRStates.CONCURRENCY().removePath(mn.getMRSessionKey());
					}
					catch (IOException | InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				log.info(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: RP = " + PlayerSystem.THREADING().getSlaveName(rp.getSlaveKey()));
				// Get the RP thread from the RP of the new hop. 01/10/2020, Bing Li
				String rpThreadKey = Rand.getRandomStringInSet(rp.getThreadKeys());
				log.info(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: RP ThreadKey = " + rpThreadKey);
				Set<String> threadKeys;
				Map<String, Set<String>> mrThreadKeys = new HashMap<String, Set<String>>();
				int cd = 0;
				Player slave;

				// For each slave in the new hop, obtain the threads. 01/10/2020, Bing Li
				for (String entry : slaveKeys)
				{
					slave = PlayerSystem.THREADING().retrievePlayerWithAllThreads(entry);
					// Get the threads of a slave. 01/10/2020, Bing Li
					threadKeys = Rand.getRandomSet(slave.getThreadKeys(), slave.getThreadKeys().size());
					log.info(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: Slave = " + PlayerSystem.THREADING().getSlaveName(entry) + " got " + threadKeys.size() + " threads for mapping ...");
					// Calculate the maximum concurrent degree of the hop. 01/10/2020, Bing Li
					cd += threadKeys.size();
					// Keep the threads participating the MR game of the new hop in the round. 01/10/2020, Bing Li
					mrThreadKeys.put(entry, threadKeys);
				}

				// Assign the Reduce task to each thread of the participating slaves. 01/10/2020, Bing Li
				for (String entry : slaveKeys)
				{
					// Get the threads of a slave. 01/10/2020, Bing Li
					threadKeys = mrThreadKeys.get(entry);
					log.info(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: MAP to Slave: " + PlayerSystem.THREADING().getSlaveName(entry) + " with " + threadKeys.size() + " threads");
					slave = PlayerSystem.THREADING().retrievePlayerWithAllThreads(entry);
					try
					{
						// Assign the Reduce task to the thread. 01/10/2020, Bing Li
						slave.notifyThreads(new ReduceInvokeNotification(mn.getMRSessionKey(), threadKeys, rp.getSlaveKey(), rpThreadKey, mn.getCurrentHop(), mn.getMaxHop(), cd));
					}
					catch (ClassNotFoundException | RemoteReadException | IOException | InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
			else
			{
				log.info(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: DONE to Master: " + PlayerSystem.THREADING().getMasterName());
				try
				{
					// If the value of the maximum hop is reached, the entire MR game of this round is accomplished such that it is time to notify the master. 01/10/2020, Bing Li
					PlayerSystem.THREADING().syncNotifyMaster(new MRFinalNotification(MRStates.CONCURRENCY().getPath(mn.getMRSessionKey()), mn.getCurrentHop(), mn.getMaxHop(), mn.getCD(), mn.getMRSessionKey(), true));
				}
				catch (IOException | InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public TaskResponse processRequest(String threadKey, TaskRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TaskResponse processRequest(String threadKey, TaskInvokeRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processNotification(String threadKey, InteractNotification notification)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public TaskResponse processRequest(String threadKey, InteractRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
