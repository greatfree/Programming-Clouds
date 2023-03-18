package org.greatfree.concurrency.threading;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;

import org.greatfree.concurrency.threading.message.AllSlaveIPsNotification;
import org.greatfree.concurrency.threading.message.AllSlavesNotification;
import org.greatfree.concurrency.threading.message.ExecuteNotification;
import org.greatfree.concurrency.threading.message.ATMNotification;
import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.concurrency.threading.message.IsAliveRequest;
import org.greatfree.concurrency.threading.message.IsAliveResponse;
import org.greatfree.concurrency.threading.message.KillAllNotification;
import org.greatfree.concurrency.threading.message.KillNotification;
import org.greatfree.concurrency.threading.message.MasterNotification;
import org.greatfree.concurrency.threading.message.ATMThreadRequest;
import org.greatfree.concurrency.threading.message.ATMThreadResponse;
import org.greatfree.concurrency.threading.message.ShutdownSlaveNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.concurrency.threading.message.TaskResponse;
import org.greatfree.concurrency.threading.message.ATMMessageType;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.IPPort;
import org.greatfree.util.ServerStatus;

/*
 * The class is used at the slave side only when no extended PlayerTask is designed by programmers. 09/30/2019, Bing Li
 * 
 * This is an upgraded version to replace SlaveTask, which is abandoned. 09/30/2019, Bing Li
 */

// Created: 09/29/2019, Bing Li
class DistributerTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.concurrency.threading");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ATMMessageType.ALL_SLAVES_NOTIFICATION:
				log.info("ALL_SLAVES_NOTIFICATION received @" + Calendar.getInstance().getTime());
				AllSlavesNotification ann = (AllSlavesNotification)notification;
				PlayerSystem.THREADING().setMasterName(ann.getMasterName());
				DistributerIDs.ID().setAllSlaveIDs(ann.getAllSlaveKeys());
				DistributerIDs.ID().setThreadKeys(ann.getThreadKeys());
				for (Map.Entry<String, String> entry : ann.getAllSlaveNames().entrySet())
				{
					DistributerIDs.ID().addSlaveName(entry.getKey(), entry.getValue());
				}
				break;
	
			case ATMMessageType.ALL_SLAVE_IPS_NOTIFICATION:
				log.info("ALL_SLAVE_IPS_NOTIFICATION received @" + Calendar.getInstance().getTime());
				AllSlaveIPsNotification asin = (AllSlaveIPsNotification)notification;
				PlayerSystem.THREADING().addSlaveIPs(asin.getIPs());
				PlayerSystem.THREADING().setMasterIP(asin.getRendezvousPoint());
				break;
				
			case ATMMessageType.MASTER_NOTIFICATION:
				log.info("MASTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				MasterNotification mn = (MasterNotification)notification;
				log.info("DistributerTask-processNotification(): master name = " + mn.getMasterName());
				PlayerSystem.THREADING().setMasterName(mn.getMasterName());
				PlayerSystem.THREADING().setMasterIP(new IPPort(mn.getMasterIP(), mn.getMasterPort()));
				PlayerSystem.THREADING().addSlaveIP(mn.getMasterKey(), new IPPort(mn.getMasterIP(), mn.getMasterPort()));
				break;
	
			case ATMMessageType.EXECUTE_NOTIFICATION:
				log.info("EXECUTE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ExecuteNotification en = (ExecuteNotification)notification;
				DistributerPool.POOL().execute(en.getThreadKey());
				break;
				
			case ATMMessageType.KILL_NOTIFICATION:
				log.info("KILL_NOTIFICATION received @" + Calendar.getInstance().getTime());
				KillNotification kn = (KillNotification)notification;
				try
				{
					DistributerPool.POOL().kill(kn.getThreadKey(), kn.getTimeout());
				}
				catch (InterruptedException e)
				{
					ServerStatus.FREE().printException(e);
				}
				break;

			case ATMMessageType.KILL_ALL_NOTIFICATION:
				log.info("KILL_ALL_NOTIFICATION received @" + Calendar.getInstance().getTime());
				KillAllNotification kan = (KillAllNotification)notification;
				try
				{
					DistributerPool.POOL().killAll(kan.getTimeout());
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
				
			case ATMMessageType.SHUTDOWN_SLAVE_NOTIFICATION:
				log.info("SHUTDOWN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ShutdownSlaveNotification sn = (ShutdownSlaveNotification)notification;
				try
				{
//					TerminateSignal.SIGNAL().notifyAllTermination();
					Worker.ATM().shutdown(sn.getTimeout());
				}
				catch (ClassNotFoundException | InterruptedException | IOException | RemoteReadException | RemoteIPNotExistedException e)
				{
					ServerStatus.FREE().printException(e);
				}
				break;
				
			case ATMMessageType.TASK_RESPONSE:
				log.info("TASK_RESPONSE received @" + Calendar.getInstance().getTime());
				Worker.ATM().addResponse((TaskResponse)notification);
				break;
				
			case ATMMessageType.TASK_INVOKE_NOTIFICATION:
				log.info("TASK_INVOKE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				DistributerPool.POOL().invoke((TaskInvokeNotification)notification);
				break;
				
			case ATMMessageType.TASK_INVOKE_REQUEST:
				log.info("TASK_INVOKE_REQUEST received @" + Calendar.getInstance().getTime());
				DistributerPool.POOL().invoke((TaskInvokeRequest)notification);
				break;

			case ATMMessageType.TASK_NOTIFICATION:
				log.info("TASK_NOTIFICATION received @" + Calendar.getInstance().getTime());
				DistributerPool.POOL().enqueueInstruction((ATMNotification)notification);
				break;
				
			case ATMMessageType.TASK_REQUEST:
				log.info("TASK_REQUEST received @" + Calendar.getInstance().getTime());
				DistributerPool.POOL().enqueueInstruction((ATMNotification)notification);
				break;
				
			case ATMMessageType.INTERACT_NOTIFICATION:
				log.info("INTERACT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				DistributerPool.POOL().invoke((InteractNotification)notification);
				break;
				
			case ATMMessageType.INTERACT_REQUEST:
				log.info("INTERACT_REQUEST received @" + Calendar.getInstance().getTime());
				DistributerPool.POOL().invoke((InteractRequest)notification);
				break;
		}
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case ATMMessageType.ATM_THREAD_REQUEST:
				log.info("NOTIFICATION_THREAD_REQUEST received @" + Calendar.getInstance().getTime());
				ATMThreadRequest ntr = (ATMThreadRequest)request;
				if (ntr.getCount() <= 1)
				{
					return new ATMThreadResponse(DistributerPool.POOL().generateThread());
				}
				else
				{
					return new ATMThreadResponse(DistributerPool.POOL().generateThreads(ntr.getCount()));
				}
				
			case ATMMessageType.IS_ALIVE_REQUEST:
				log.info("IS_ALIVE_REQUEST received @" + Calendar.getInstance().getTime());
				IsAliveRequest iar = (IsAliveRequest)request;
				return new IsAliveResponse(DistributerPool.POOL().isAlive(iar.getThreadKey()));
		}
		return null;
	}
}
