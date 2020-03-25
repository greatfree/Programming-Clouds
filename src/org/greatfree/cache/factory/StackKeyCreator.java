package org.greatfree.cache.factory;

import org.greatfree.cache.CompoundKeyCreatable;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

// Created: 06/21/2017, Bing Li
public class StackKeyCreator implements CompoundKeyCreatable<Integer>
{

	@Override
	public String createCompoundKey(String prefix, Integer key)
	{
		// The A is put in the middle of the input is to avoid the integer value results in the messing up of the generated key. 08/12/2018, Bing Li
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
