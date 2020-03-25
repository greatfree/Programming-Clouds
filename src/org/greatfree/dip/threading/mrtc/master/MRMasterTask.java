package org.greatfree.dip.threading.mrtc.master;

import java.io.IOException;
import java.util.Calendar;

import org.greatfree.concurrency.threading.PlayerTask;
import org.greatfree.concurrency.threading.message.ThreadingMessageType;
import org.greatfree.dip.threading.MRStates;
import org.greatfree.dip.threading.message.MRFinalNotification;
import org.greatfree.dip.threading.message.MRPartialNotification;
import org.greatfree.dip.threading.message.TaskApplicationID;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;

// Created: 09/22/2019, Bing Li
class MRMasterTask extends PlayerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			// The case is not used in the MR game. 01/08/2020, Bing Li
			case ThreadingMessageType.TASK_STATE_NOTIFICATION:
				System.out.println("TASK_STATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				return;
				
			case TaskApplicationID.MR_PARTIAL_NOTIFICATION:
				System.out.println("MR_PARTIAL_NOTIFICATION received @" + Calendar.getInstance().getTime());
				
				// Get the partially completed notification from the slave, which starts a new hop of this round of the MR game. 01/10/2020, Bing Li
				MRPartialNotification mrpn = (MRPartialNotification)notification;
				// Accumulate the path of the hop which is just finished. 01/10/2020, Bing Li
				MRStates.CONCURRENCY().incrementPath(mrpn.getMRKey(), mrpn.getPath());
				System.out.println("============================================");
				System.out.println("Partial: The partial path:");
				System.out.println(MRStates.CONCURRENCY().getPath(mrpn.getMRKey()));
				System.out.println("============================================");
				return;
				
			case TaskApplicationID.MR_FINAL_NOTIFICATION:
				System.out.println("MR_FINAL_NOTIFICATION received @" + Calendar.getInstance().getTime());
				
				// Get the final notification from the slave. 01/10/2020, Bing Li
				MRFinalNotification mrfn = (MRFinalNotification)notification;

				// Check whether the MR game of one round is done. 01/10/2020, Bing Li
				if (mrfn.isDone())
				{
					// If the MR game of this round is done, increment the final path received from the slave. 01/10/2020, Bing Li
					MRStates.CONCURRENCY().incrementPath(mrfn.getMRKey(), mrfn.getPath());
					System.out.println("============================================");
					System.out.println("Done: The final path:");
					System.out.println(MRStates.CONCURRENCY().getPath(mrfn.getMRKey()));
					System.out.println("============================================");
				}
				else
				{
					// If the MR game of this round is NOT done, increment the path received from the slave. 01/10/2020, Bing Li
					MRStates.CONCURRENCY().incrementPath(mrfn.getMRKey(), mrfn.getPath());
					// Increment the current CD. Until the current CD is NOT less than the maximum value of the current hop, the reduce procedure is DONE and a hop of this round can be started. 01/10/2020, Bing Li
					MRStates.CONCURRENCY().incrementCD(mrfn.getMRKey());
					// Check whether the CD of the current hop is fulfilled. 01/10/2020, Bing Li
					if (MRStates.CONCURRENCY().isCDFulfilled(mrfn.getMRKey(), mrfn.getCD()))
					{
						System.out.println("============================================");
						System.out.println("Interrupted: The current final path:");
						System.out.println(MRStates.CONCURRENCY().getPath(mrfn.getMRKey()));
						System.out.println("============================================");

						// Check whether the current hop reaches the maximum hop of this round. 01/10/2020, Bing Li
						if (mrfn.getCurrentHop() < mrfn.getMaxHop())
						{
							try
							{
//								Master.THREADING().continueMR(mrfn.getMRKey(), MRStates.CONCURRENCY().getPath(mrfn.getMRKey()), mrfn.getCurrentHop(), mrfn.getMaxHop());
								
								// If the current hop is less than the maximum hop of the MR game in the round, it indicates that the MR game of this round is NOT done. So, a new hop for the round is started. 01/10/2020, Bing Li
								Master.THREADING().continueMR(mrfn.getMRKey(), mrfn.getCurrentHop(), mrfn.getMaxHop());
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
