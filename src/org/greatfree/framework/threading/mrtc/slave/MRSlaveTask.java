package org.greatfree.framework.threading.mrtc.slave;

import java.util.Calendar;
import java.util.Map;

import org.greatfree.concurrency.threading.PlayerTask;
import org.greatfree.concurrency.threading.message.AllSlavesNotification;
import org.greatfree.concurrency.threading.message.AllSlaveIPsNotification;
import org.greatfree.concurrency.threading.message.ThreadingMessageType;
import org.greatfree.framework.threading.mrtc.NodeIDs;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

// Created: 09/22/2019, Bing Li
class MRSlaveTask extends PlayerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ThreadingMessageType.TASK_STATE_NOTIFICATION:
				System.out.println("TASK_STATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				return;
				
			/*
			 * The message is sent from the master when the MR is initialized. It let all of the slaves know about the threads of each slave such that they can perform Map/Reduce among them. 12/19/2019, Bing Li
			 */
			case ThreadingMessageType.ALL_SLAVES_NOTIFICATION:
				System.out.println("ALL_SLAVES_NOTIFICATION received @" + Calendar.getInstance().getTime());
				AllSlavesNotification ann = (AllSlavesNotification)notification;
				// Keep the master name. 01/08/2020, Bing Li
				Slave.THREADING().setMasterName(ann.getMasterName());
				// Keep all of the slave keys. 01/08/2020, Bing Li
				NodeIDs.ID().setAllSlaveIDs(ann.getAllSlaveKeys());
				// Keep all of threads of each slave. 01/08/2020, Bing Li
				NodeIDs.ID().setThreadKeys(ann.getThreadKeys());
				
				// Keep the names of all of the slaves. 01/08/2020, Bing Li
				for (Map.Entry<String, String> entry : ann.getAllSlaveNames().entrySet())
				{
					NodeIDs.ID().addSlaveName(entry.getKey(), entry.getValue());
				}
				return;

				/*
				 * The message is sent by Distributer. When it is started as a master, the slaves IPs are obtained from the registry server and sent to the slaves for possible concurrent tasks. 01/08/2020, Bing Li
				 */
			case ThreadingMessageType.ALL_SLAVE_IPS_NOTIFICATION:
				System.out.println("ALL_SLAVE_IPS_NOTIFICATION received @" + Calendar.getInstance().getTime());
				AllSlaveIPsNotification asin = (AllSlaveIPsNotification)notification;
				// Keep the IP addresses. 01/08/2020, Bing Li
				Slave.THREADING().addIPs(asin.getIPs());
				// Keep the IP address of the master. 01/08/2020, Bing Li
				Slave.THREADING().setMasterIP(asin.getRendezvousPoint());
				return;
		}		
		super.processNotify(notification);
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		return super.processRead(request);
	}

}
