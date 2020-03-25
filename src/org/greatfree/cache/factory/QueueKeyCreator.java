package org.greatfree.cache.factory;

import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

// Created: 06/07/2017, Bing Li
public class QueueKeyCreator implements CompoundKeyCreatable<Integer>
{

	@Override
	public String createCompoundKey(String prefix, Integer key)
	{
		return Tools.getHash(prefix + UtilConfig.A + key);
	}

	/*
	@Override
	public String createPrefixKey(String prefix)
	{
		return Tools.getAHash(prefix);
	}
	*/
}
