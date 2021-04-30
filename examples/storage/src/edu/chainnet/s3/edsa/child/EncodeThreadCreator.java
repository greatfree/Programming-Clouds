package edu.chainnet.s3.edsa.child;

import org.greatfree.concurrency.reactive.NotificationThreadCreatable;

import edu.chainnet.s3.message.SliceUploadRequest;

// Created: 07/11/2020, Bing Li
class EncodeThreadCreator implements NotificationThreadCreatable<SliceUploadRequest, EncodeThread>
{

	@Override
	public EncodeThread createNotificationThreadInstance(int taskSize)
	{
		return new EncodeThread(taskSize);
	}

}
