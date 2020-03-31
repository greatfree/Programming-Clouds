package org.greatfree.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.greatfree.exceptions.FutureExceptionHandler;

// Created: 03/29/2020, Bing Li
public class AfterExecuteThreadExecutor extends ThreadPoolExecutor
{
	private FutureExceptionHandler handler;

	public AfterExecuteThreadExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		this.handler = null;
	}

	public AfterExecuteThreadExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, FutureExceptionHandler handler)
	{
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		this.handler = handler;
	}
	
	public void setFutureExceptionHandler(FutureExceptionHandler handler)
	{
		this.handler = handler;
	}
	
	@Override
	protected void afterExecute(Runnable r, Throwable t)
	{
		super.afterExecute(r, t);

		/*
		System.out.println("AfterExecuteThreadExecutor: afterExecute is EXECUTED ...");
		if (t != null)
		{
			System.out.println("AfterExecuteThreadExecutor: afterExecute: Throwable is NOT null");
		}
		else
		{
			System.out.println("AfterExecuteThreadExecutor: afterExecute: Throwable is null");
		}
		
		if (this.handler != null)
		{
			System.out.println("AfterExecuteThreadExecutor: afterExecute: handler is NOT null");
		}
		else
		{
			System.out.println("AfterExecuteThreadExecutor: afterExecute: handler is null");
		}
		*/
		
		if (t != null && this.handler != null)
		{
			this.handler.handleException(t);
		}
	}
}
