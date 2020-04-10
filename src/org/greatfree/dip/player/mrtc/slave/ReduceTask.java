package org.greatfree.dip.player.mrtc.slave;

import java.io.IOException;

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
import org.greatfree.dip.threading.TaskConfig;
import org.greatfree.dip.threading.message.MRFinalNotification;
import org.greatfree.dip.threading.message.MapInvokeNotification;
import org.greatfree.dip.threading.message.ReduceInvokeNotification;
import org.greatfree.dip.threading.mrtc.MRConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.UtilConfig;

// Created: 10/02/2019, Bing Li
class ReduceTask implements ThreadTask
{

	@Override
	public String getKey()
	{
		return TaskConfig.REDUCE_TASK_KEY;
	}

	@Override
	public void processNotification(String threadKey, TaskNotification notification)
	{
	}

	@Override
	public void processNotification(String threadKey, TaskInvokeNotification notification)
	{
		System.out.println(PlayerSystem.THREADING().getNickName() + " => REDUCE_TASK: local ID = " + PlayerSystem.THREADING().getNickName());
		// Get the reduce task. 01/08/2020, Bing Li
		ReduceInvokeNotification rn = (ReduceInvokeNotification)notification;
		System.out.println(PlayerSystem.THREADING().getNickName() + " => REDUCE_TASK: max hop = " + rn.getMaxHop());
		System.out.println(PlayerSystem.THREADING().getNickName() + " => REDUCE_TASK: current task = " + rn.getCurrentHop() + "/" + rn.getMaxHop());
		
		// Initialize the path to track the MP process. The path can be regarded as the concurrent task accomplished by the current slave. 01/08/2020, Bing Li
		String path = UtilConfig.EMPTY_STRING;
		if (path.equals(UtilConfig.EMPTY_STRING))
		{
			// Keep the reduce step in the current slave. 01/08/2020, Bing Li
			path = MRConfig.REDUCE_TASK + PlayerSystem.THREADING().getNickName() + UtilConfig.COMMA + MRConfig.THREAD_PREFIX + threadKey;
		}
		else
		{
			// The below line is NOT useful? I need to verify it. 01/08/2020, Bing Li
			path += UtilConfig.NEW_LINE + MRConfig.REDUCE_TASK + PlayerSystem.THREADING().getNickName() + UtilConfig.COMMA + MRConfig.THREAD_PREFIX + threadKey;
		}
		System.out.println(PlayerSystem.THREADING().getNickName() + " => REDUCE_TASK: path:");
		System.out.println("============================================");
		System.out.println(path);
		System.out.println("============================================");
		
		// Detect whether the slave number is larger than the minimum reasonable number, 1. 01/10/2020, Bing Li
		if (PlayerSystem.THREADING().getSlaveSize() > MRConfig.MINIMUM_SLAVE_SIZE)
		{
			System.out.println(PlayerSystem.THREADING().getNickName() + " => REDUCE_TASK: REDUCE to Slave: " + PlayerSystem.THREADING().getSlaveName(rn.getRPSlaveKey()));
			Player rpPlayer = PlayerSystem.THREADING().retrievePlayerWithAllThreads(rn.getRPSlaveKey());
			try
			{
				// Assign one Map task to the RP thread such that a new hop of MR game for this round is started. 01/10/2020, Bing Li
				rpPlayer.notifyThreads(new MapInvokeNotification(rn.getMRSessionKey(), rn.getRPThreadKey(), path, rn.getCurrentHop() + 1, rn.getMaxHop(), rn.getCD()));
			}
			catch (ClassNotFoundException | RemoteReadException | IOException | InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			System.out.println(PlayerSystem.THREADING().getNickName() + " => REDUCE_TASK: DONE to Master: " + PlayerSystem.THREADING().getMasterName());
			try
			{
				// If the number of slaves is less than the minimum reasonable number, 2, it represents that only the master and the current slave participate this round of MR game. Then, the current slave has only one choice to send it result to the master, which has to play the role of the RP. 01/10/2020, Bing Li
				PlayerSystem.THREADING().syncNotifyMaster(new MRFinalNotification(path, rn.getCurrentHop() + 1, rn.getMaxHop(), rn.getCD(), rn.getMRSessionKey(), false));
			}
			catch (IOException | InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
	}

	@Override
	public TaskResponse processRequest(String threadKey, TaskRequest request)
	{
		return null;
	}

	@Override
	public TaskResponse processRequest(String threadKey, TaskInvokeRequest request)
	{
		return null;
	}

	@Override
	public void processNotification(String threadKey, InteractNotification notification)
	{
	}

	@Override
	public TaskResponse processRequest(String threadKey, InteractRequest request)
	{
		return null;
	}

}
