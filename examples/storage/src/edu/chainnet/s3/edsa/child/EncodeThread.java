package edu.chainnet.s3.edsa.child;

import java.io.IOException;

import org.greatfree.concurrency.reactive.NotificationQueue;
import org.greatfree.exceptions.RemoteReadException;

import edu.chainnet.s3.edsa.EDSAConfig;
import edu.chainnet.s3.message.SliceUploadRequest;

/*
 * The thread encodes incoming slices and then sends the encoded slices to the storage server. 07/11/2020, Bing Li
 */

// Created: 07/11/2020, Bing Li
class EncodeThread extends NotificationQueue<SliceUploadRequest>
{

	public EncodeThread(int taskSize)
	{
		super(taskSize);
	}

	@Override
	public void run()
	{
		SliceUploadRequest notification;
		while (!super.isShutdown())
		{
			while (!super.isEmpty())
			{
				try
				{
					notification = super.getNotification();
					Coder.EDSA().encode(notification.getSessionKey(), notification.getFileName(), notification.getSliceKey(), notification.getPartitionIndex(), notification.getData(), notification.getEDID(), notification.getPosition(), notification.getDescription());
					super.disposeMessage(notification);
				}
				catch (InterruptedException | IOException | ClassNotFoundException | RemoteReadException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				super.holdOn(EDSAConfig.ENCODING_THREAD_TIMEOUT);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

}
