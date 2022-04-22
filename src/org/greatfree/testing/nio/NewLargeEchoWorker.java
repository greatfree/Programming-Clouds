package org.greatfree.testing.nio;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.greatfree.concurrency.Sync;

/**
 * 
 * @author Bing Li
 * 
 * 02/01/2022, Bing Li
 *
 */
class NewLargeEchoWorker implements Runnable
{
	private Queue<NewServerDataEvent> queue = new ConcurrentLinkedQueue<NewServerDataEvent>();
	private Sync sync;
	
	public NewLargeEchoWorker()
	{
		this.sync = new Sync();
	}

//	public void processData(LargeObjectServer server, SocketChannel socket, byte[] data, int count)
	public void processData(NewLargeObjectServer server, SocketChannel client, ByteBuffer buffer, int count)
	{
		/*
		 * The condition is incorrect. 01/19/2022, Bing Li
		 */
		if (count > 0)
		{
			byte[] receivedBytes = new byte[count];
			buffer.flip();
			buffer.get(receivedBytes);
			buffer.clear();
			queue.add(new NewServerDataEvent(server, client, receivedBytes));
		}
		else
		{
//			queue.notify();
			this.sync.signal();
		}
	}

	public void run()
	{
		NewServerDataEvent dataEvent;

		while (true)
		{
			// Wait for data to become available
			while (queue.isEmpty())
			{
//					queue.wait();
				this.sync.holdOn();
			}
			dataEvent = queue.poll();

			// Return to sender
			dataEvent.server.keepEchoData(dataEvent.socket, dataEvent.data);
		}
	}
}
