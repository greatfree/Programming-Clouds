package org.greatfree.framework.threading.ct.slave;

import java.io.IOException;
import java.util.concurrent.RejectedExecutionException;

import org.greatfree.concurrency.threading.NotificationQueue;
import org.greatfree.concurrency.threading.message.InstructNotification;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.threading.message.PrintTaskNotification;
import org.greatfree.util.ServerStatus;

// Created: 09/11/2019, Bing Li
//class FreeThread extends NotificationQueue<SyncInstructNotification>
class NotificationThread extends NotificationQueue<InstructNotification>
{
	public NotificationThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		InstructNotification notification;
//		SyncInstructNotification notification;
		PrintTaskNotification ptn;
//		TimeoutNotification tmn;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.getNotification();
					/*
					switch (notification.getApplicationID())
					{
//						case TaskMessageType.PRINT_TASK_NOTIFICATION:
						case ThreadingMessageType.TASK_NOTIFICATION:
							System.out.println("NotificationThread-TASK_NOTIFICATION: started ...");
							ptn = (PrintTaskNotification)notification;
//							notification.getLock().lock();
							System.out.println(ptn.getMessage());
							Thread.sleep(ptn.getSleepTime());
//							notification.getLock().unlock();
							Slave.SLAVE().notifyState(super.getKey(), ptn.getTaskKey(), notification.getApplicationID(), notification.getInstructKey(), true);
							System.out.println("NotificationThread-TASK_NOTIFICATION: ended ...");
							break;

							// The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
						case ThreadingMessageType.TIMEOUT_NOTIFICATION:
							tmn = (TimeoutNotification)notification;
//							notification.getLock().lock();
//							super.holdOn(tmn.getTime());
							super.setWait(tmn.getTime());
//							notification.getLock().unlock();
							break;

							// The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
						case ThreadingMessageType.WAIT_NOTIFICATION:
//							System.out.println("NotificationThread-WAIT_NOTIFICATION: started ...");
//							notification.getLock().lock();
//							super.holdOn();
							super.setWaitForEver();
//							notification.getLock().unlock();
//							System.out.println("NotificationThread-WAIT_NOTIFICATION: ended ...");
							break;

						case ThreadingMessageType.SIGNAL_NOTIFICATION:
							System.out.println("FreeThread-SIGNAL_NOTIFICATION: started ...");
							super.signal();
							System.out.println("FreeThread-SIGNAL_NOTIFICATION: ended ...");
							break;
					}
					*/
					
					System.out.println("NotificationThread-TASK_NOTIFICATION: started ...");
					ptn = (PrintTaskNotification)notification;
//					notification.getLock().lock();
					System.out.println(ptn.getMessage());
					Thread.sleep(ptn.getSleepTime());
//					notification.getLock().unlock();
//					Slave.SLAVE().notifyState(super.getKey(), ptn.getTaskKey(), notification.getApplicationID(), notification.getInstructKey(), true);
					Slave.SLAVE().notifyState(super.getKey(), ptn.getTaskKey(), notification.getApplicationID(), notification.getKey(), true);
					System.out.println("NotificationThread-TASK_NOTIFICATION: ended ...");
					this.disposeMessage(notification);
				}
				catch (InterruptedException | ClassNotFoundException | RemoteReadException | IOException | RejectedExecutionException e)
				{
					/*
					try
					{
						ThreadSlave.SLAVE().notifyState(super.getKey(), ThreadConfig.WRONG_NOTIFICATION_TYPE, ThreadConfig.WRONG_NOTIFICATION_KEY, false);
					}
					catch (ClassNotFoundException | RemoteReadException | IOException ex)
					{
						ServerStatus.FREE().printException(e);
					}
					*/
					ServerStatus.FREE().printException(e);
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 01/20/2016, Bing Li
				super.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				ServerStatus.FREE().printException(e);
			}
		}
	}
}


