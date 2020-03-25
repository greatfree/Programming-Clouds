package org.greatfree.cache;

/*
 * The class is abandoned. 06/28/2017, Bing Li
 */

/*
 * Since the keys are added by me, when the cache is overflowed, the keys must be inconsistent with the size of the cache. The task defined here is executed periodically to check which keys should be removed. 06/25/2017, Bing Li
 */

// Created: 06/25/2017, Bing Li
public class KeysRemover<Key, Cache extends KeyRemovable<Key>> implements Runnable
{
	private Cache c;
	
	public KeysRemover(Cache c)
	{
		this.c = c;
	}

	@Override
	public void run()
	{
		// The class is abandoned. So the parameter being null does not matter anything. 06/28/2017, Bing Li
		this.c.removeDBKey(null);
	}
}
