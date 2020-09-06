package org.greatfree.dsf.threading.mrtc.slave;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.concurrency.threading.ThreadTask;
import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.concurrency.threading.message.TaskRequest;
import org.greatfree.concurrency.threading.message.TaskResponse;
import org.greatfree.dsf.threading.MRStates;
import org.greatfree.dsf.threading.TaskConfig;
import org.greatfree.dsf.threading.message.MRFinalNotification;
import org.greatfree.dsf.threading.message.MRPartialNotification;
import org.greatfree.dsf.threading.message.MapNotification;
import org.greatfree.dsf.threading.message.ReduceNotification;
import org.greatfree.dsf.threading.mrtc.MRConfig;
import org.greatfree.dsf.threading.mrtc.NodeIDs;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Rand;

// Created: 09/22/2019, Bing Li
class MapTask implements ThreadTask
{

	@Override
	public String getKey()
	{
		return TaskConfig.MAP_TASK_KEY;
	}

	@Override
	public void processNotification(String threadKey, TaskNotification notification)
	{
		System.out.println("=> MAP_TASK: local ID = " + NodeIDs.ID().getLocalName());
		
		// Get the Map notification from one slave that just accomplishes its concurrent task. 01/10/2020, Bing Li
		MapNotification mn = (MapNotification)notification;
//		System.out.println("MAP_TASK: " + mn.getPath());
		System.out.println("=> MAP_TASK: CD = " + mn.getCD());
		System.out.println("=> MAP_TASK: current task = " + mn.getCurrentHop() + "/" + mn.getMaxHop());
		/*
		try
		{
			Thread.sleep(mn.getSleepTime());
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		*/

		// Accumulate the path, i.e., the result of one concurrent task, from the slave. 01/10/2020, Bing Li
		MRStates.CONCURRENCY().incrementPath(mn.getMRSessionKey(), mn.getPath());
		
		System.out.println(NodeIDs.ID().getLocalName() + " => MAP_TASK: path:");
		System.out.println("============================================");
		System.out.println(MRStates.CONCURRENCY().getPath(mn.getMRSessionKey()));
		System.out.println("============================================");

		// Increment the current CD after accumulating the result of one concurrent task from a slave. 01/10/2020, Bing Li
		MRStates.CONCURRENCY().incrementCD(mn.getMRSessionKey());
		// Detect whether the CD reaches the maximum CD or not. 01/10/2020, Bing Li
		if (MRStates.CONCURRENCY().isCDFulfilled(mn.getMRSessionKey(), mn.getCD()))
		{
			// Once if the maximum concurrency degree (CD) is reached, it indicates that the hop of the round is done. So the current CD is deprecated. 01/10/2020, Bing Li
			MRStates.CONCURRENCY().removeCD(mn.getMRSessionKey());
			// Accumulate the path to track the MR game. 01/10/2020, Bing Li
			MRStates.CONCURRENCY().incrementPath(mn.getMRSessionKey(), MRConfig.MAP_TASK + NodeIDs.ID().getLocalName());
			System.out.println(NodeIDs.ID().getLocalName() + " => MAP_TASK: CD = " + mn.getCD() + " is fulfilled ...");
			
			// Check whether the current hop is NOT greater than the predefined maximum hop. 01/10/2020, Bing Li
			if (mn.getCurrentHop() <= mn.getMaxHop())
			{
				System.out.println(NodeIDs.ID().getLocalName() + " => MAP_TASK: MAP continued ...");
				
				/* In the case, the MR game is still NOT done yet. The current/local slave is required to keep play the game for the next hop of the round.
				 * For that, the local slave gets slaves randomly from all of the potential participants. The local slave should NOT be one member of the newly selected slaves since it plays as the initiator of the next hop. 01/10/2020, Bing Li
				 */
				Set<String> slaveKeys = NodeIDs.ID().getSlavesExceptLocal();
				System.out.println(NodeIDs.ID().getLocalName() + " => MAP_TASK: slaveKeys size = " + slaveKeys.size());
//				String rp = NodeIDs.ID().getRandomSlaveKey();
				
				// Get the RP from the newly selected slaves. 01/10/2020, Bing Li
				String rp = NodeIDs.ID().getSlaveExceptFrom(slaveKeys);
				// Check whether the newly selected RP is the local slave or not. It seems it is NOT necessary to do that since the local slave is excluded from the slaves for the coming hop. 01/10/2020, Bing Li
				if (!rp.equals(NodeIDs.ID().getLocalKey()))
				{
					try
					{
						// If the RP is not the local slave, it is reasonable to start the new hop. Meanwhile, the local slave notifies the master about the partially completed concurrent task. 01/10/2020, Bing Li
						Slave.THREADING().asyncNotifyMaster(new MRPartialNotification(mn.getMRSessionKey(), MRStates.CONCURRENCY().getPath(mn.getMRSessionKey())));
						// Remove the path, which tracks the MR tasks of the previous hop, since it is deprecated. 01/10/2020, Bing Li
						MRStates.CONCURRENCY().removePath(mn.getMRSessionKey());
					}
					catch (IOException | InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				System.out.println(NodeIDs.ID().getLocalName() + " => MAP_TASK: RP = " + NodeIDs.ID().getSlaveName(rp));
				try
				{
//					String rpThreadKey = Slave.THREADING().obtainThread(rp);
					
					// Get the RP thread from the RP of the new hop. 01/10/2020, Bing Li
					String rpThreadKey = NodeIDs.ID().getThreadKey(rp);
					System.out.println(NodeIDs.ID().getLocalName() + " => MAP_TASK: RP ThreadKey = " + rpThreadKey);
					Set<String> threadKeys;
					Map<String, Set<String>> mrThreadKeys = new HashMap<String, Set<String>>();
					int cd = 0;
					
					// For each slave in the new hop, obtain the threads. 01/10/2020, Bing Li
					for (String entry : slaveKeys)
					{
						// Get the threads of a slave. 01/10/2020, Bing Li
						threadKeys = NodeIDs.ID().getThreadKeys(entry, Rand.getRandom(NodeIDs.ID().getThreadSize(entry)));
						System.out.println(NodeIDs.ID().getLocalName() + " => MAP_TASK: Slave = " + NodeIDs.ID().getSlaveName(entry) + " got " + threadKeys.size() + " threads for mapping ...");
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
						System.out.println(NodeIDs.ID().getLocalName() + " => MAP_TASK: MAP to Slave: " + NodeIDs.ID().getSlaveName(entry) + " with " + threadKeys.size() + " threads");
						// Assign the Reduce task to each thread. 01/10/2020, Bing Li
						for (String tKey : threadKeys)
						{
							// Check whether the thread is alive or not. 01/10/2020, Bing Li
							if (!Slave.THREADING().isAlive(entry, tKey))
							{
								// If the thread is NOT alive, execute the thread. 01/10/2020, Bing Li
								Slave.THREADING().execute(entry, tKey);
							}
						}
//						Actor.THREADING().assignTask(entry, new ReduceNotification(NodeIDs.ID().getThreadKey(entry), path, ThreadConfig.TIMEOUT, NodeIDs.ID().getRandomID(), mn.getCurrentHop() + 1, mn.getMaxHop(), mn.getFinalRendezvousPoint()));
//						Slave.THREADING().assignTask(entry, new ReduceNotification(mn.getMRKey(), NodeIDs.ID().getThreadKey(entry), MRStates.CONCURRENCY().getPath(mn.getMRKey()), ThreadConfig.TIMEOUT, rp, rpThreadKey, mn.getCurrentHop() + 1, mn.getMaxHop(), cd));
//						Slave.THREADING().assignTask(entry, new ReduceNotification(mn.getMRKey(), threadKeys, MRStates.CONCURRENCY().getPath(mn.getMRKey()), ThreadConfig.TIMEOUT, rp, rpThreadKey, mn.getCurrentHop() + 1, mn.getMaxHop(), cd));
//						Slave.THREADING().assignTask(entry, new ReduceNotification(mn.getMRKey(), threadKeys, MRStates.CONCURRENCY().getPath(mn.getMRKey()), ThreadConfig.TIMEOUT, rp, rpThreadKey, mn.getCurrentHop() + 1, mn.getMaxHop(), cd));
//						Slave.THREADING().assignTask(entry, new ReduceNotification(mn.getMRKey(), threadKeys, ThreadConfig.TIMEOUT, rp, rpThreadKey, mn.getCurrentHop() + 1, mn.getMaxHop(), cd));
						
						// Assign the Reduce task to the thread. 01/10/2020, Bing Li
						Slave.THREADING().assignTask(entry, new ReduceNotification(mn.getMRSessionKey(), threadKeys, ThreadConfig.TIMEOUT, rp, rpThreadKey, mn.getCurrentHop(), mn.getMaxHop(), cd));
//						Slave.THREADING().assignTask(entry, new ReduceNotification(mn.getMRKey(), threadKeys, MRStates.CONCURRENCY().getPath(mn.getMRKey()), ThreadConfig.TIMEOUT, rp, rpThreadKey, mn.getCurrentHop(), mn.getMaxHop(), cd, !rp.equals(NodeIDs.ID().getLocalKey())));
					}
				}
				catch (ClassNotFoundException | RemoteReadException | IOException | InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println(NodeIDs.ID().getLocalName() + " => MAP_TASK: DONE to Master: " + Slave.THREADING().getMasterName());
				try
				{
					// If the value of the maximum hop is reached, the entire MR game of this round is accomplished such that it is time to notify the master. 01/10/2020, Bing Li
					Slave.THREADING().syncNotifyMaster(new MRFinalNotification(MRStates.CONCURRENCY().getPath(mn.getMRSessionKey()), mn.getCurrentHop(), mn.getMaxHop(), mn.getCD(), mn.getMRSessionKey(), true));
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
	public void processNotification(String threadKey, TaskInvokeNotification notification)
	{
		// TODO Auto-generated method stub
		
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
