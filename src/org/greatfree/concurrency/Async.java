package org.greatfree.concurrency;

// Created: 09/10/2018, Bing Li
public abstract class Async<Message>
{
	public abstract void perform(Message message);
	
	/*
	 * It is convenient to update the affected code. So the line is removed. 04/10/2022, Bing Li
	 * 
	 * The method is used for the asynchronous operations with the cryptography algorithm. 04/09/2022, Bing Li
	 */
//	public abstract void perform(Message message, int cryptoOption);
}
