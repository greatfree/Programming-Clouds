package org.greatfree.concurrency.batch;

// Created: 04/23/2018, Bing Li
public interface BalancedThreadCreatable<Notification, Notifier extends ThreadNotifiable<Notification>, CallbackThread extends BalancedQueue<Notification, Notifier>>
{
	public CallbackThread createBalanceThreadInstance(int taskSize, Notifier notifier);
}
