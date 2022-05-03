package org.greatfree.concurrency.threading;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.concurrency.threading.message.ExecuteNotification;
import org.greatfree.concurrency.threading.message.ATMNotification;
import org.greatfree.concurrency.threading.message.IsAliveRequest;
import org.greatfree.concurrency.threading.message.IsAliveResponse;
import org.greatfree.concurrency.threading.message.KillAllNotification;
import org.greatfree.concurrency.threading.message.KillNotification;
import org.greatfree.concurrency.threading.message.ATMThreadRequest;
import org.greatfree.concurrency.threading.message.ATMThreadResponse;
import org.greatfree.concurrency.threading.message.ShutdownSlaveNotification;
import org.greatfree.concurrency.threading.message.ATMMessageType;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.ServerStatus;

/*
 * This is an intermediate version at the slave side. Now it is replaced with DistributerTask. So the class is abandoned. 09/30/2019, Bing Li
 */

// Created: 09/13/2019, Bing Li
class SlaveTask implements ServerTask
{
	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			// The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
			/*
			case ThreadingMessageType.TIMEOUT_NOTIFICATION:
				System.out.println("TIMEOUT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				TimeoutNotification tn = (TimeoutNotification)notification;
				ActorThreadPool.POOL().enqueueInstruction(tn);
				break;
				*/
				
			// The method is not necessary since the thread is signaled when new messages are received. 09/18/2019, Bing Li
				/*
			case ThreadingMessageType.SIGNAL_NOTIFICATION:
				System.out.println("SIGNAL_NOTIFICATION received @" + Calendar.getInstance().getTime());
				SignalNotification gan = (SignalNotification)notification;
//				ActorThreadPool.POOL().signal(gan.getThreadKey());
				break;
				*/
				
			case ATMMessageType.EXECUTE_NOTIFICATION:
				System.out.println("EXECUTE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ExecuteNotification en = (ExecuteNotification)notification;
				DistributerPool.POOL().execute(en.getThreadKey());
				break;
				
			case ATMMessageType.KILL_NOTIFICATION:
				System.out.println("KILL_NOTIFICATION received @" + Calendar.getInstance().getTime());
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

				// The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
				/*
			case ThreadingMessageType.WAIT_NOTIFICATION:
				System.out.println("WAIT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				WaitNotification wn = (WaitNotification)notification;
				ActorThreadPool.POOL().enqueueInstruction(wn);
				break;
				*/

			case ATMMessageType.KILL_ALL_NOTIFICATION:
				System.out.println("KILL_ALL_NOTIFICATION received @" + Calendar.getInstance().getTime());
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
				System.out.println("SHUTDOWN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				ShutdownSlaveNotification sn = (ShutdownSlaveNotification)notification;
				try
				{
//					TerminateSignal.SIGNAL().notifyAllTermination();
					Worker.ATM().shutdown(sn.getTimeout());
				}
				catch (ClassNotFoundException | InterruptedException | IOException | RemoteReadException e)
				{
					ServerStatus.FREE().printException(e);
				}
				break;
				
			default:
				DistributerPool.POOL().enqueueInstruction((ATMNotification)notification);
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
				ATMThreadRequest ntr = (ATMThreadRequest)request;
				if (ntr.getCount() <= 1)
				{
					/*
					String threadKey = ActorThreadPool.POOL().generateThread();
					if (threadKey != null)
					{
						return new NotificationThreadResponse(threadKey, true);
					}
					*/
					return new ATMThreadResponse(DistributerPool.POOL().generateThread());
				}
				else
				{
					return new ATMThreadResponse(DistributerPool.POOL().generateThreads(ntr.getCount()));
				}
				
			case ATMMessageType.IS_ALIVE_REQUEST:
				System.out.println("IS_ALIVE_REQUEST received @" + Calendar.getInstance().getTime());
				IsAliveRequest iar = (IsAliveRequest)request;
				return new IsAliveResponse(DistributerPool.POOL().isAlive(iar.getThreadKey()));
		}
		return null;
	}
}
