package org.greatfree.dip.player.mrtc.slave;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import org.greatfree.dip.threading.MRStates;
import org.greatfree.dip.threading.TaskConfig;
import org.greatfree.dip.threading.message.MRFinalNotification;
import org.greatfree.dip.threading.message.MRPartialNotification;
import org.greatfree.dip.threading.message.MapInvokeNotification;
import org.greatfree.dip.threading.message.ReduceInvokeNotification;
import org.greatfree.dip.threading.mrtc.MRConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Rand;

// Created: 10/02/2019, Bing Li
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processNotification(String threadKey, TaskInvokeNotification notification)
	{
		System.out.println("=> MAP_TASK: local ID = " + PlayerSystem.THREADING().getNickName());
		MapInvokeNotification mn = (MapInvokeNotification)notification;
		System.out.println("=> MAP_TASK: CD = " + mn.getCD());
		System.out.println("=> MAP_TASK: current task = " + mn.getCurrentHop() + "/" + mn.getMaxHop());

		MRStates.CONCURRENCY().incrementPath(mn.getMRSessionKey(), mn.getPath());

		System.out.println(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: path:");
		System.out.println("============================================");
		System.out.println(MRStates.CONCURRENCY().getPath(mn.getMRSessionKey()));
		System.out.println("============================================");

		MRStates.CONCURRENCY().incrementCD(mn.getMRSessionKey());
		if (MRStates.CONCURRENCY().isCDFulfilled(mn.getMRSessionKey(), mn.getCD()))
		{
			MRStates.CONCURRENCY().removeCD(mn.getMRSessionKey());
			MRStates.CONCURRENCY().incrementPath(mn.getMRSessionKey(), MRConfig.MAP_TASK + PlayerSystem.THREADING().getNickName());
			System.out.println(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: CD = " + mn.getCD() + " is fulfilled ...");
			if (mn.getCurrentHop() <= mn.getMaxHop())
			{
				System.out.println(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: MAP continued ...");
				Set<String> slaveKeys = PlayerSystem.THREADING().getSlavesExceptLocal(MRConfig.MINIMUM_MUTLI_SLAVE_SIZE);
				System.out.println(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: slaveKeys size = " + slaveKeys.size());
				Player rp = PlayerSystem.THREADING().getSlaveExceptFrom(slaveKeys);
				if (!rp.getSlaveKey().equals(PlayerSystem.THREADING().getNickKey()))
				{
					try
					{
						PlayerSystem.THREADING().asyncNotifyMaster(new MRPartialNotification(mn.getMRSessionKey(), MRStates.CONCURRENCY().getPath(mn.getMRSessionKey())));
						MRStates.CONCURRENCY().removePath(mn.getMRSessionKey());
					}
					catch (IOException | InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				System.out.println(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: RP = " + PlayerSystem.THREADING().getSlaveName(rp.getSlaveKey()));
				String rpThreadKey = Rand.getRandomStringInSet(rp.getThreadKeys());
				System.out.println(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: RP ThreadKey = " + rpThreadKey);
				Set<String> threadKeys;
				Map<String, Set<String>> mrThreadKeys = new HashMap<String, Set<String>>();
				int cd = 0;
				Player slave;
				for (String entry : slaveKeys)
				{
					slave = PlayerSystem.THREADING().retrievePlayerWithAllThreads(entry);
					threadKeys = Rand.getRandomSet(slave.getThreadKeys(), slave.getThreadKeys().size());
					System.out.println(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: Slave = " + PlayerSystem.THREADING().getSlaveName(entry) + " got " + threadKeys.size() + " threads for mapping ...");
					cd += threadKeys.size();
					mrThreadKeys.put(entry, threadKeys);
				}

				for (String entry : slaveKeys)
				{
					threadKeys = mrThreadKeys.get(entry);
					System.out.println(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: MAP to Slave: " + PlayerSystem.THREADING().getSlaveName(entry) + " with " + threadKeys.size() + " threads");
					slave = PlayerSystem.THREADING().retrievePlayerWithAllThreads(entry);
					try
					{
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
				System.out.println(PlayerSystem.THREADING().getNickName() + " => MAP_TASK: DONE to Master: " + PlayerSystem.THREADING().getMasterName());
				try
				{
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
