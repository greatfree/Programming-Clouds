package org.greatfree.concurrency;

/*
 * Some behaviors, such as dispose(), on the messages must be synchronized among threads. For example, if no synchronization, it is possible that a message is disposed while it is consumed in another one. 11/26/2014, Bing Li
 * 
 * The methods for those behaviors and the ones to synchronize them are defined in the interface. 11/26/2014, Bing Li
 */

// Created: 11/26/2014, Bing Li
//public interface MessageBindable<ServerMessage>
public abstract class MessageBindable<ServerMessage>
{
	// Add the thread key. 11/26/2014, Bing Li
	public abstract void addThread(String key);
	// Set the message that the synchronize needs to take care. 11/26/2014, Bing Li
	public abstract void set(ServerMessage message);
	// Bind the message with the thread that needs to be synchronized. 11/26/2014, Bing Li
	public abstract void bind(String threadKey, ServerMessage message);
	// Dispose the resources of the binder. 11/26/2014, Bing Li
	public abstract void dispose();
}
