package org.greatfree.client;

import java.io.IOException;
import java.util.TimerTask;

/**
 * 
 * @author libing
 * 
 * 10/09/2022
 *
 */
final class ClientManagerIdleChecker extends TimerTask
{
	private ClientManager pool;
	
	public ClientManagerIdleChecker(ClientManager pool)
	{
		this.pool = pool;
	}

	@Override
	public void run()
	{
		try
		{
			this.pool.checkIdle();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
