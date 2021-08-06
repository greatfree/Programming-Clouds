package org.greatfree.message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.greatfree.concurrency.MessageBindable;
import org.greatfree.message.abandoned.OldMulticastMessage;

/*
 * This is an implementation of the MessageBindable interface. It is usually used by threads which share the same multicast messages. 11/26/2014, Bing Li
 */

// Created: 05/15/2017, Bing Li
public class MessageDisposer<Message extends OldMulticastMessage> extends MessageBindable<Message>
{
	// The collection keeps each message's state, i.e., the thread keys that share the message and whether the message has been already processed by a particular thread. 11/26/2014, Bing Li
	private Map<String, Map<String, Boolean>> messageStates;
	// All of the thread keys to be synchronized are saved in the list. 11/26/2014, Bing Li
	private CopyOnWriteArrayList<String> threadKeys;
	
	/*
	 * Initialize the message disposer. 11/26/2014, Bing Li
	 */
	public MessageDisposer()
	{
		this.messageStates = new ConcurrentHashMap<String, Map<String, Boolean>>();
		this.threadKeys = new CopyOnWriteArrayList<String>();
	}

	/*
	 * Add the thread key that shares the notification to the message disposer. 11/26/2014, Bing Li
	 */
	@Override
	public void addThread(String key)
	{
		this.threadKeys.add(key);
	}

	/*
	 * Put one message into the message disposer for synchronization and disposal. 11/26/2014, Bing Li
	 */
	@Override
	public void set(Message message)
	{
//		System.out.println("MessageDisposer-set(): message getKey() = " + message.getKey());
		// Check whether the message exists in the disposer. 11/26/2014, Bing Li
		if (!this.messageStates.containsKey(message.getKey()))
		{
			// If the message does not exist, save it. 11/26/2014, Bing Li
			this.messageStates.put(message.getKey(), new ConcurrentHashMap<String, Boolean>());
			// For the new message, it must associate it with all of threads. 11/26/2014, Bing Li
			for (String key : this.threadKeys)
			{
				// The flag, false, indicates that the thread has not finished processing the message. Therefore, the disposal cannot be performed. 11/26/2014, Bing Li
				this.messageStates.get(message.getKey()).put(key, false);
			}
		}
	}

	/*
	 * When a thread finishes processing a message, it must invoke the method, bind(), to notify the message disposer for synchronization. 11/26/2014, Bing Li
	 */
	@Override
	public void bind(String threadKey, Message message)
	{
//		System.out.println("MessageDisposer-bind(): message getKey() = " + message.getKey());
//		System.out.println("MessageDisposer-bind(): threadKeys size = " + this.threadKeys.size());
		// Update the flag to true. It indicates that the message has already been processed by a particular thread. 11/26/2014, Bing Li
		this.messageStates.get(message.getKey()).put(threadKey, true);
		// Initialize a count for synchronization management. 11/26/2014, Bing Li
		int count = 0;
		// Scan each thread that shares the message for the issue of whether they have already processed the message or not. 11/26/2014, Bing Li
		for (String key : this.threadKeys)
		{
			// Check if one message is finished processing by a thread. 11/26/2014, Bing Li 
			if (this.messageStates.get(message.getKey()).get(key))
			{
				// Increment the count if one thread has finished processing the message. 11/26/2014, Bing Li
				count++;
			}
		}
		// Check whether the count exceeds the count of all of the threads. 11/26/2014, Bing Li
		if (count >= this.threadKeys.size())
		{
			// If the above condition is fulfilled, it denotes that all of the threads have already consumed the message. 11/26/2014, Bing Li
			this.messageStates.remove(message.getKey());
			// Then, further processing on the message can be performed here. In this case, the message is disposed. 11/26/2014, Bing Li
			message = null;
		}
	}

	/*
	 * Dispose the message disposer. 11/26/2014, Bing Li
	 */
	@Override
	public void dispose()
	{
		this.messageStates.clear();
		this.threadKeys.clear();
		this.messageStates = null;
		this.threadKeys = null;
	}

}
