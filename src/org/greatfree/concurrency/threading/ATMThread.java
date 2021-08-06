package org.greatfree.concurrency.threading;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.RejectedExecutionException;

import org.greatfree.concurrency.threading.message.ATMNotification;
import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.concurrency.threading.message.TaskRequest;
import org.greatfree.concurrency.threading.message.ATMMessageType;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.ServerStatus;

// Created: 09/13/2019, Bing Li
class ATMThread extends NotificationQueue<ATMNotification>
{
	public ATMThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		ATMNotification notification;
//		TimeoutNotification tmn;
//		TaskNotification tn;
//		TaskRequest tr;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.getNotification();
					// The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
					/*
					switch (notification.getApplicationID())
					{
						case ThreadingMessageType.TIMEOUT_NOTIFICATION:
								System.out.println("THREAD: TIMEOUT_NOTIFICATION received @" + Calendar.getInstance().getTime());
								tmn = (TimeoutNotification)notification;
								super.holdOn(tmn.getTime());
							break;
							
						case ThreadingMessageType.WAIT_NOTIFICATION:
								System.out.println("THREAD: WAIT_NOTIFICATION received @" + Calendar.getInstance().getTime());
								super.holdOn();
							break;
							
						case ThreadingMessageType.TASK_NOTIFICATION:
								System.out.println("THREAD: TASK_NOTIFICATION received @" + Calendar.getInstance().getTime());
								tn = (TaskNotification)notification;
								Worker.THREADING().processNotification(tn);
							break;
					}
					*/
					switch (notification.getApplicationID())
					{
						case ATMMessageType.TASK_NOTIFICATION:
							System.out.println("THREAD: TASK_NOTIFICATION received @" + Calendar.getInstance().getTime());
//						tn = (TaskNotification)notification;
							Worker.ATM().processNotification(super.getKey(), (TaskNotification)notification);
							break;
							
						case ATMMessageType.TASK_INVOKE_NOTIFICATION:
							System.out.println("THREAD: TASK_INVOKE_NOTIFICATION received @" + Calendar.getInstance().getTime());
							Worker.ATM().processNotification(super.getKey(), (TaskInvokeNotification)notification);
							break;
							
						case ATMMessageType.TASK_REQUEST:
							System.out.println("THREAD: TASK_REQUEST received @" + Calendar.getInstance().getTime());
							Worker.ATM().processRequest(super.getKey(), (TaskRequest)notification);
							break;
							
						case ATMMessageType.TASK_INVOKE_REQUEST:
							System.out.println("THREAD: TASK_INVOKE_REQUEST received @" + Calendar.getInstance().getTime());
							Worker.ATM().processRequest(super.getKey(), (TaskInvokeRequest)notification);
							break;
							
						case ATMMessageType.INTERACT_NOTIFICATION:
							System.out.println("THREAD: INTERACT_NOTIFICATION received @" + Calendar.getInstance().getTime());
							Worker.ATM().processNotification(super.getKey(), (InteractNotification)notification);
							break;
							
						case ATMMessageType.INTERACT_REQUEST:
							System.out.println("THREAD: INTERACT_REQUEST received @" + Calendar.getInstance().getTime());
							Worker.ATM().processRequest(super.getKey(), (InteractRequest)notification);
							break;
					}
					super.disposeMessage(notification);
				}
				catch (InterruptedException | ClassNotFoundException | RemoteReadException | IOException | RejectedExecutionException e)
				{
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
