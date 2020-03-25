package org.greatfree.concurrency.reactive;

/*
 * In general, a pool needs to have the ability to create instances of managed resources. The Creatable interface is responsible for that. 11/21/2014, Bing Li
 * 
 * The interface defines the method to create instances of InteractiveThread, which is derived from InteractiveQueue. 11/21/2014, Bing Li
 * 
 */

// Created: 11/21/2014, Bing Li
public interface InteractiveThreadCreatable<Notification, Notifier extends Interactable<Notification>, InteractiveThread extends InteractiveQueue<Notification, Notifier>>
{
	public InteractiveThread createInteractiveThreadInstance(int taskSize, Notifier notifier);
}
