package org.greatfree.abandoned.cache.distributed.child;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.abandoned.cache.distributed.CacheNotificationThreadCreatable;
import org.greatfree.cache.KeyLoadable;
import org.greatfree.cache.PersistableMapFactorable;
import org.greatfree.cache.message.PutNotification;

// Created: 07/16/2017, Bing Li
public class PutUnicastNotificationThreadCreator<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>> implements CacheNotificationThreadCreatable<PutNotification<Value>, PutUnicastNotificationThread<Key, Value, Factory, DB>, Key, Value, Factory, DB>
{

	@Override
	public PutUnicastNotificationThread<Key, Value, Factory, DB> createNotificationThreadInstance(int taskSize, ChildMapRegistry<Key, Value, Factory, DB> registry)
	{
		return new PutUnicastNotificationThread<Key, Value, Factory, DB>(taskSize, registry);
	}

}
