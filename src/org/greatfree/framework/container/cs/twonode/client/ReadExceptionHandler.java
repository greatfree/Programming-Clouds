package org.greatfree.framework.container.cs.twonode.client;

import org.greatfree.exceptions.FutureExceptionHandler;

// Created: 03/29/2020, Bing Li
class ReadExceptionHandler implements FutureExceptionHandler
{

	@Override
	public void handleException(Throwable e)
	{
//		IOException ex = (IOException)e;
//		ex.printStackTrace();
		System.out.println("===========================");
//		System.out.println(ex);
		System.out.println("Exception occurs ...");
		System.out.println("===========================");
	}

}
