package org.greatfree.cache;

// Created: 06/07/2017, Bing Li
public interface CompoundKeyCreatable<Key>
{
	public String createCompoundKey(String prefix, Key key);
//	public String createPrefixKey(String prefix);
//	public String createPrefixKey(String prefix, boolean isHash);
}
