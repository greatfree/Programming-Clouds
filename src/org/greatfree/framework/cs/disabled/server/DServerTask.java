package org.greatfree.framework.cs.disabled.server;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cs.disabled.message.DSNotification;
import org.greatfree.framework.cs.disabled.message.DSRequest;
import org.greatfree.framework.cs.disabled.message.DSResponse;
import org.greatfree.framework.cs.disabled.message.DisabledAppID;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.server.container.ServerTask;
import org.greatfree.server.container.UnaryDisabledPeer;

/**
 * 
 * @author libing
 * 
 * 03/07/2023
 *
 */
final class DServerTask implements ServerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.cs.disabled.server");

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case DisabledAppID.DS_NOTIFICATION:
				log.info("DS_NOTIFICATION received @" + Calendar.getInstance().getTime());
				DSNotification dsn = (DSNotification)notification;
				log.info(dsn.getMessage());
				break;
				
			case DisabledAppID.DS_SHUTDOWN_NOTIFICATION:
				log.info("DS_SHUTDOWN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					UnaryDisabledPeer.CS().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | RemoteReadException | RemoteIPNotExistedException
						| IOException | InterruptedException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public ServerMessage processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case DisabledAppID.DS_REQUEST:
				log.info("DS_REQUEST received @" + Calendar.getInstance().getTime());
				DSRequest dsr = (DSRequest)request;
				log.info(dsr.getMessage());
				return new DSResponse("Got it");
		}
		return null;
	}
}
