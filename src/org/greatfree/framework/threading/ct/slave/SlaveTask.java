package org.greatfree.framework.threading.ct.slave;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.concurrency.threading.message.ExecuteNotification;
import org.greatfree.concurrency.threading.message.IsAliveRequest;
import org.greatfree.concurrency.threading.message.IsAliveResponse;
import org.greatfree.concurrency.threading.message.KillNotification;
import org.greatfree.concurrency.threading.message.ATMThreadResponse;
import org.greatfree.concurrency.threading.message.ShutdownSlaveNotification;
import org.greatfree.concurrency.threading.message.ATMMessageType;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.threading.message.PrintTaskNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.ServerStatus;

// Created: 09/12/2019, Bing Li
class SlaveTask implements ServerTask
{
	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
//			case TaskMessageType.PRINT_TASK_NOTIFICATION:
			case ATMMessageType.TASK_NOTIFICATION:
				System.out.println("PRINT_TASK_NOTIFICATION received @" + Calendar.getInstance().getTime());
				PrintTaskNotification ptn = (PrintTaskNotification)notification;
				DistributedThreadPool.POOL().enqueueInstruction(ptn);
				break;
				
			case ATMMessageType.EXECUTE_NOTIFICATION:
				System.out.println("EXECUTE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ExecuteNotification en = (ExecuteNotification)notification;
				DistributedThreadPool.POOL().execute(en.getThreadKey());
				break;

				// The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
				/*
			case ThreadingMessageType.TIMEOUT_NOTIFICATION:
				System.out.println("TIMEOUT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				TimeoutNotification tn = (TimeoutNotification)notification;
				DistributedThreadPool.POOL().enqueueInstruction(tn);
				break;
				*/

				/*
				 * The case is not necessary since the thread is signaled when new messages are received. 09/18/2019, Bing Li
				 */
				/*
			case ThreadingMessageType.SIGNAL_NOTIFICATION:
				System.out.println("SIGNAL_NOTIFICATION received @" + Calendar.getInstance().getTime());
				SignalNotification gan = (SignalNotification)notification;
				System.out.println("SlaveTask-SIGNAL_NOTIFICATION: started ...");
				DistributedThreadPool.POOL().signal(gan.getThreadKey());
				System.out.println("SlaveTask-SIGNAL_NOTIFICATION: ended ...");
//				DistributedThreadPool.POOL().enqueueInstruction(gan);
				break;
				*/

				// The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
				/*
			case ThreadingMessageType.WAIT_NOTIFICATION:
				System.out.println("WAIT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				WaitNotification wn = (WaitNotification)notification;
				DistributedThreadPool.POOL().enqueueInstruction(wn);
				break;
				*/
				
			case ATMMessageType.KILL_NOTIFICATION:
				System.out.println("KILL_NOTIFICATION received @" + Calendar.getInstance().getTime());
				KillNotification kn = (KillNotification)notification;
				try
				{
					DistributedThreadPool.POOL().kill(kn.getThreadKey(), kn.getTimeout());
				}
				catch (InterruptedException e)
				{
					ServerStatus.FREE().printException(e);
				}
				break;
				
			case ATMMessageType.SHUTDOWN_SLAVE_NOTIFICATION:
				System.out.println("SHUTDOWN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ShutdownSlaveNotification sn = (ShutdownSlaveNotification)notification;
				ServerStatus.FREE().setShutdown();
				try
				{
					Slave.SLAVE().stop(sn.getTimeout());
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					ServerStatus.FREE().printException(e);
				}
				break;
		}
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case ATMMessageType.ATM_THREAD_REQUEST:
				System.out.println("NOTIFICATION_THREAD_REQUEST received @" + Calendar.getInstance().getTime());
				/*
				String threadKey = DistributedThreadPool.POOL().generateThread();
				if (threadKey != null)
				{
					return new NotificationThreadResponse(threadKey, true);
				}
				else
				{
					return new NotificationThreadResponse(threadKey, false);
				}
				*/
				return new ATMThreadResponse(DistributedThreadPool.POOL().generateThread());
				
			case ATMMessageType.IS_ALIVE_REQUEST:
				System.out.println("IS_ALIVE_REQUEST received @" + Calendar.getInstance().getTime());
				IsAliveRequest iar = (IsAliveRequest)request;
				return new IsAliveResponse(DistributedThreadPool.POOL().isAlive(iar.getThreadKey()));
		}
		return null;
	}
}
