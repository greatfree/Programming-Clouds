package org.greatfree.framework.player.mrtc.master;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.concurrency.threading.ATMTask;
import org.greatfree.concurrency.threading.message.ATMMessageType;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.threading.MRStates;
import org.greatfree.framework.threading.message.MRFinalNotification;
import org.greatfree.framework.threading.message.MRPartialNotification;
import org.greatfree.framework.threading.message.TaskApplicationID;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

// Created: 09/30/2019, Bing Li
class MRMasterTask extends ATMTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			// The state of task execution sent from slaves. For the game, the notification is not necessarily processed. 04/03/2020, Bing Li
			case ATMMessageType.TASK_STATE_NOTIFICATION:
				System.out.println("TASK_STATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				return;

			// Partial states of the game is sent from slaves. 04/03/2020, Bing Li
			case TaskApplicationID.MR_PARTIAL_NOTIFICATION:
				System.out.println("MR_PARTICAL_NOTIFICATION received @" + Calendar.getInstance().getTime());
				MRPartialNotification mrpn = (MRPartialNotification)notification;
				MRStates.CONCURRENCY().incrementPath(mrpn.getMRKey(), mrpn.getPath());
				System.out.println("============================================");
				System.out.println("Partial: The partial path:");
				System.out.println(MRStates.CONCURRENCY().getPath(mrpn.getMRKey()));
				System.out.println("============================================");
				return;

			// Final state of the game is sent from slaves. 04/03/2020, Bing Li
			case TaskApplicationID.MR_FINAL_NOTIFICATION:
				System.out.println("MR_FINAL_NOTIFICATION received @" + Calendar.getInstance().getTime());
				MRFinalNotification mrfn = (MRFinalNotification)notification;
				// Detect whether the game is done. 04/03/2020, Bing Li
				if (mrfn.isDone())
				{
					MRStates.CONCURRENCY().incrementPath(mrfn.getMRKey(), mrfn.getPath());
					System.out.println("============================================");
					System.out.println("Done: The final path:");
					System.out.println(MRStates.CONCURRENCY().getPath(mrfn.getMRKey()));
					System.out.println("============================================");
				}
				else
				{
					// If the game is not done yet, the master is required to keep the game. 04/03/2020, Bing L
					MRStates.CONCURRENCY().incrementPath(mrfn.getMRKey(), mrfn.getPath());
					MRStates.CONCURRENCY().incrementCD(mrfn.getMRKey());
					// Check whether the currency degree (CD) meets the requirement. 04/03/2020, Bing Li
					if (MRStates.CONCURRENCY().isCDFulfilled(mrfn.getMRKey(), mrfn.getCD()))
					{
						System.out.println("============================================");
						System.out.println("Interrupted: The current final path:");
						System.out.println(MRStates.CONCURRENCY().getPath(mrfn.getMRKey()));
						System.out.println("============================================");
						// Check whether the current hops reach the predefined one. 04/03/2020, Bing Li
						if (mrfn.getCurrentHop() < mrfn.getMaxHop())
						{
							try
							{
								// Keep the game. 04/03/2020, Bing Li
								MRCoordinator.THREADING().continueMR(mrfn.getMRKey(), mrfn.getCurrentHop(), mrfn.getMaxHop());
							}
							catch (ClassNotFoundException | RemoteReadException | IOException | InterruptedException e)
							{
								e.printStackTrace();
							}
						}
						else
						{
							
						}
					}
				}
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
