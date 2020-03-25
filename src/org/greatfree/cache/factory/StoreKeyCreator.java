package org.greatfree.cache.factory;

import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.util.Tools;

// Created: 06/07/2017, Bing Li
public class StoreKeyCreator implements CompoundKeyCreatable<String>
{

	@Override
	public String createCompoundKey(String prefix, String key)
	{
		return Tools.getHash(prefix + key);
	}

	/*
	@Override
	public String createPrefixKey(String prefix)
	{
		return Tools.getAHash(prefix);
	}
	*/
}
