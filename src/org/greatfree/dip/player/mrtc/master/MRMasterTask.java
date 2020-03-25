package org.greatfree.dip.player.mrtc.master;

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

// Created: 09/30/2019, Bing Li
class MRMasterTask extends PlayerTask
{

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case ThreadingMessageType.TASK_STATE_NOTIFICATION:
				System.out.println("TASK_STATE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				return;

			case TaskApplicationID.MR_PARTIAL_NOTIFICATION:
				System.out.println("MR_PARTICAL_NOTIFICATION received @" + Calendar.getInstance().getTime());
				MRPartialNotification mrpn = (MRPartialNotification)notification;
				MRStates.CONCURRENCY().incrementPath(mrpn.getMRKey(), mrpn.getPath());
				System.out.println("============================================");
				System.out.println("Partial: The partial path:");
				System.out.println(MRStates.CONCURRENCY().getPath(mrpn.getMRKey()));
				System.out.println("============================================");
				return;
				
			case TaskApplicationID.MR_FINAL_NOTIFICATION:
				System.out.println("MR_FINAL_NOTIFICATION received @" + Calendar.getInstance().getTime());
				MRFinalNotification mrfn = (MRFinalNotification)notification;
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
					MRStates.CONCURRENCY().incrementPath(mrfn.getMRKey(), mrfn.getPath());
					MRStates.CONCURRENCY().incrementCD(mrfn.getMRKey());
					if (MRStates.CONCURRENCY().isCDFulfilled(mrfn.getMRKey(), mrfn.getCD()))
					{
						System.out.println("============================================");
						System.out.println("Interrupted: The current final path:");
						System.out.println(MRStates.CONCURRENCY().getPath(mrfn.getMRKey()));
						System.out.println("============================================");
						if (mrfn.getCurrentHop() < mrfn.getMaxHop())
						{
							try
							{
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
