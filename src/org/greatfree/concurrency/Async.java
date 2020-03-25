package org.greatfree.concurrency;

// Created: 09/10/2018, Bing Li
public abstract class Async<Message>
{
	public abstract void perform(Message message);
}
