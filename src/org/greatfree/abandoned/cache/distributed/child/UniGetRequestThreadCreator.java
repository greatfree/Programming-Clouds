package org.greatfree.abandoned.cache.distributed.child;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.abandoned.cache.distributed.CacheNotificationThreadCreatable;
import org.greatfree.cache.KeyLoadable;
import org.greatfree.cache.PersistableMapFactorable;
import org.greatfree.cache.message.UniGetRequest;

// Created: 07/23/2017, Bing Li
public class UniGetRequestThreadCreator<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>> implements CacheNotificationThreadCreatable<UniGetRequest<Key>, UniGetRequestThread<Key, Value, Factory, DB>, Key, Value, Factory, DB>
{

	@Override
	public UniGetRequestThread<Key, Value, Factory, DB> createNotificationThreadInstance(int taskSize, ChildMapRegistry<Key, Value, Factory, DB> registry)
	{
		return new UniGetRequestThread<Key, Value, Factory, DB>(taskSize, registry);
	}

}
