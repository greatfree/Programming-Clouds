package org.greatfree.server.abandoned;

import org.greatfree.message.ServerMessage;
import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.server.CSListener;
import org.greatfree.server.ServerDispatcher;

/*
 * The class is abandoned. 05/19/2018, Bing Li
 */

// Created: 04/20/2017, Bing Li
public class CSListenerDisposer<Dispatcher extends ServerDispatcher<ServerMessage>> implements RunnerDisposable<CSListener<Dispatcher>>
{

	@Override
	public void dispose(CSListener<Dispatcher> r) throws InterruptedException
	{
		r.dispose();
	}

	@Override
	public void dispose(CSListener<Dispatcher> r, long time) throws InterruptedException
	{
		r.dispose();
	}

}
