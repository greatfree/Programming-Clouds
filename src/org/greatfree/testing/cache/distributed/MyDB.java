package org.greatfree.testing.cache.distributed;

import java.util.concurrent.ConcurrentSkipListSet;

import org.greatfree.cache.KeyLoadable;

// Created: 07/04/2017, Bing Li
public class MyDB implements KeyLoadable<String>
{

	@Override
	public ConcurrentSkipListSet<String> loadKeys()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveKey(String key)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveKeys(ConcurrentSkipListSet<String> keys)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(String key)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(ConcurrentSkipListSet<String> keys)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAll()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub
		
	}

}
