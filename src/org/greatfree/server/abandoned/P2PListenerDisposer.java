package org.greatfree.server.abandoned;

import org.greatfree.message.ServerMessage;
import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.server.P2PListener;
import org.greatfree.server.ServerDispatcher;

/*
 * The class is abandoned. 05/19/2018, Bing Li
 */

/*
 * The class is responsible for disposing the instance of dispatching server istener by invoking its method of shutdown(). 09/20/2014, Bing Li
 */

// Created: 04/19/2017, Bing Li
public class P2PListenerDisposer<Dispatcher extends ServerDispatcher<ServerMessage>> implements RunnerDisposable<P2PListener<Dispatcher>>
{

	@Override
	public void dispose(P2PListener<Dispatcher> r) throws InterruptedException
	{
		r.dispose();
	}

	@Override
	public void dispose(P2PListener<Dispatcher> r, long time) throws InterruptedException
	{
		r.dispose();
	}

}
