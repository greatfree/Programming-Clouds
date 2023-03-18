package org.greatfree.framework.cs.disabled.broker.child;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.framework.cs.disabled.broker.message.BrokerNotification;

/**
 * 
 * @author libing
 * 
 * 03/08/2023
 *
 */
final class MessageRepository
{
	private Map<String, List<BrokerNotification>> messages;
	
	private MessageRepository()
	{
		this.messages = new ConcurrentHashMap<String, List<BrokerNotification>>();
	}
	
	private static MessageRepository instance = new MessageRepository();
	
	public static MessageRepository DB()
	{
		if (instance == null)
		{
			instance = new MessageRepository();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void addNotification(BrokerNotification notification)
	{
		if (!this.messages.containsKey(notification.getDestinationName()))
		{
			this.messages.put(notification.getDestinationName(), new ArrayList<BrokerNotification>());
		}
		this.messages.get(notification.getDestinationName()).add(notification);
	}
	
	public List<BrokerNotification> getNotifications(String destinationName)
	{
		if (this.messages.containsKey(destinationName))
		{
			List<BrokerNotification> notifications = this.messages.get(destinationName);
			this.messages.remove(destinationName);
			return notifications;
		}
		return null;
	}
}
