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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processNotification(String threadKey, TaskInvokeNotification notification)
	{
		System.out.println(PlayerSystem.THREADING().getNickName() + " => REDUCE_TASK: local ID = " + PlayerSystem.THREADING().getNickName());
		ReduceInvokeNotification rn = (ReduceInvokeNotification)notification;
		System.out.println(PlayerSystem.THREADING().getNickName() + " => REDUCE_TASK: max hop = " + rn.getMaxHop());
		System.out.println(PlayerSystem.THREADING().getNickName() + " => REDUCE_TASK: current task = " + rn.getCurrentHop() + "/" + rn.getMaxHop());
		
		String path = UtilConfig.EMPTY_STRING;
		if (path.equals(UtilConfig.EMPTY_STRING))
		{
			path = MRConfig.REDUCE_TASK + PlayerSystem.THREADING().getNickName() + UtilConfig.COMMA + MRConfig.THREAD_PREFIX + threadKey;
		}
		else
		{
			path += UtilConfig.NEW_LINE + MRConfig.REDUCE_TASK + PlayerSystem.THREADING().getNickName() + UtilConfig.COMMA + MRConfig.THREAD_PREFIX + threadKey;
		}
		System.out.println(PlayerSystem.THREADING().getNickName() + " => REDUCE_TASK: path:");
		System.out.println("============================================");
		System.out.println(path);
		System.out.println("============================================");
		
		if (PlayerSystem.THREADING().getSlaveSize() > MRConfig.MINIMUM_SLAVE_SIZE)
		{
			System.out.println(PlayerSystem.THREADING().getNickName() + " => REDUCE_TASK: REDUCE to Slave: " + PlayerSystem.THREADING().getSlaveName(rn.getRPSlaveKey()));
			Player rpPlayer = PlayerSystem.THREADING().retrievePlayerWithAllThreads(rn.getRPSlaveKey());
			try
			{
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
