package org.greatfree.abandoned.cache.distributed;

import java.io.Serializable;

/*
 * The interface is used to be implemented by distributed maps. When data is retrieved from the cluster, the method is called back such that it wakes up the get method which is waiting for the response. 07/04/2017, Bing Li
 */

// Created: 07/04/2017, Bing Li
public interface MapValueReceivable<Key extends Serializable, Value extends CacheKey<Key>>
{
	public void valueReceived(Key key, Value value);
}
