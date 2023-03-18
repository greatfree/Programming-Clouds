package org.greatfree.framework.threading.mrtc.slave;

import java.io.IOException;
import java.util.logging.Logger;

import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.concurrency.threading.ThreadTask;
import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.concurrency.threading.message.TaskRequest;
import org.greatfree.concurrency.threading.message.TaskResponse;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.threading.TaskConfig;
import org.greatfree.framework.threading.message.MRFinalNotification;
import org.greatfree.framework.threading.message.MapNotification;
import org.greatfree.framework.threading.message.ReduceNotification;
import org.greatfree.framework.threading.mrtc.MRConfig;
import org.greatfree.framework.threading.mrtc.NodeIDs;
import org.greatfree.util.UtilConfig;

// Created: 09/22/2019, Bing Li
class ReduceTask implements ThreadTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.threading.mrtc.slave");
	
	@Override
	public String getKey()
	{
		return TaskConfig.REDUCE_TASK_KEY;
	}

	@Override
	public void processNotification(String threadKey, TaskNotification notification)
	{
		log.info(NodeIDs.ID().getLocalName() + " => REDUCE_TASK: local ID = " + NodeIDs.ID().getLocalName());
		
		// Get the reduce task. 01/08/2020, Bing Li
		ReduceNotification rn = (ReduceNotification)notification;
		log.info(NodeIDs.ID().getLocalName() + " => REDUCE_TASK: max hop = " + rn.getMaxHop());
//		log.info("1) REDUCE_TASK: path = " + rn.getPath());
		log.info(NodeIDs.ID().getLocalName() + " => REDUCE_TASK: current task = " + rn.getCurrentHop() + "/" + rn.getMaxHop());
		/*
		try
		{
			Thread.sleep(rn.getSleepTime());
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		*/

		// Initialize the path to track the MP process. The path can be regarded as the concurrent task accomplished by the current slave. 01/08/2020, Bing Li
		String path = UtilConfig.EMPTY_STRING;
		if (path.equals(UtilConfig.EMPTY_STRING))
		{
//			path = NodeIDs.ID().getLocalName() + UtilConfig.COMMA + MRConfig.THREAD_PREFIX + Rand.getRandomUpperString(MRConfig.ID_LENGTH);
			
			// Keep the reduce step in the current slave. 01/08/2020, Bing Li
			path = MRConfig.REDUCE_TASK + NodeIDs.ID().getLocalName() + UtilConfig.COMMA + MRConfig.THREAD_PREFIX + threadKey;
		}
		else
		{
//			path += UtilConfig.BAR + NodeIDs.ID().getLocalName() + UtilConfig.COMMA + MRConfig.THREAD_PREFIX + Rand.getRandomUpperString(MRConfig.ID_LENGTH);

			// The below line is NOT useful? I need to verify it. 01/08/2020, Bing Li
			path += UtilConfig.NEW_LINE + MRConfig.REDUCE_TASK + NodeIDs.ID().getLocalName() + UtilConfig.COMMA + MRConfig.THREAD_PREFIX + threadKey;
		}
		log.info(NodeIDs.ID().getLocalName() + " => REDUCE_TASK: path:");
		log.info("============================================");
		log.info(path);
		log.info("============================================");

		// Detect whether the slave number is larger than the minimum reasonable number, 1. 01/10/2020, Bing Li
		if (NodeIDs.ID().getSlaveSize() > MRConfig.MINIMUM_SLAVE_SIZE)
		{
			log.info(NodeIDs.ID().getLocalName() + " => REDUCE_TASK: REDUCE to Slave: " + NodeIDs.ID().getSlaveName(rn.getRPSlaveKey()));
			try
			{
				// Check whether RP thread is alive or not. 01/10/2020, Bing Li
				if (!Slave.THREADING().isAlive(rn.getRPSlaveKey(), rn.getRPThreadKey()))
				{
					// Execute the RP thread if it is not alive. 01/10/2020, Bing Li
					Slave.THREADING().execute(rn.getRPSlaveKey(), rn.getRPThreadKey());
				}
				// Assign one Map task to the RP thread such that a new hop of MR game for this round is started. 01/10/2020, Bing Li
				Slave.THREADING().assignTask(rn.getRPSlaveKey(), new MapNotification(rn.getMRSessionKey(), rn.getRPThreadKey(), path, ThreadConfig.TIMEOUT, rn.getCurrentHop() + 1, rn.getMaxHop(), rn.getCD()));
			}
			catch (ClassNotFoundException | RemoteReadException | IOException | InterruptedException | RemoteIPNotExistedException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
//			log.info("4) REDUCE_TASK: path = " + path);
			log.info(NodeIDs.ID().getLocalName() + " => REDUCE_TASK: DONE to Master: " + Slave.THREADING().getMasterName());
			try
			{
				// If the number of slaves is less than the minimum reasonable number, 2, it represents that only the master and the current slave participate this round of MR game. Then, the current slave has only one choice to send it result to the master, which has to play the role of the RP. 01/10/2020, Bing Li
				Slave.THREADING().syncNotifyMaster(new MRFinalNotification(path, rn.getCurrentHop() + 1, rn.getMaxHop(), rn.getCD(), rn.getMRSessionKey(), false));
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
