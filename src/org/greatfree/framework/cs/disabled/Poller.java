package org.greatfree.framework.cs.disabled;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.greatfree.exceptions.BrokerClusterNotAvailableException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.cs.disabled.broker.message.BrokerNotification;
import org.greatfree.message.container.Response;
import org.greatfree.server.container.DisabledPeer;

/**
 * 
 * @author libing
 * 
 * 03/17/2023
 *
 */
public class Poller implements Runnable
{
	private final static Logger log = Logger.getLogger("org.greatfree.framework.cs.disabled");

	private DisabledPeer peer;
	
	public Poller(DisabledPeer peer)
	{
		this.peer = peer;
	}

	@Override
	public void run()
	{
		try
		{
			List<BrokerNotification> notifications = this.peer.checkMessages();
//			log.info("notifications' size = " + notifications.size());
			for (BrokerNotification entry : notifications)
			{
				if (entry.getNotification() != null)
				{
					this.peer.processNotifiation(entry.getNotification());
				}
				else if (entry.getRequest() != null)
				{
//					this.peer.notifyBroker(new Response(this.peer.processRequest(entry.getRequest()), entry.getRequest().getKey()));
					this.peer.notifyBroker(entry.getSourceName(), new Response(this.peer.processRequest(entry.getRequest()), entry.getRequest().getKey()));
				}
				else if (entry.getResponse() != null)
				{
					log.info("Response, " + entry.getResponse().getKey() + ", is saved locally!");
					this.peer.saveResponse(entry.getResponse());
				}
			}
		}
		catch (ClassNotFoundException | RemoteReadException | RemoteIPNotExistedException | IOException | InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (BrokerClusterNotAvailableException e)
		{
			log.info(e.toString());
		}
	}
}
