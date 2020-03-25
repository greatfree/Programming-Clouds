package org.greatfree.abandoned.cache.distributed;

// Created: 04/15/2017, Bing Li
public interface PrefetchNotificationCreatable<Notification>
{
//	public Notification createNotificationInstance(String cacheKey, String speakerKey, String receiverKey, int startIndex);
//	public Notification createNotificationInstance(String cacheKey, int startIndex);
	public Notification createNotificationInstance(String cacheKey, int startIndex, int endIndex);
}
