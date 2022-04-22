package org.greatfree.testing.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import org.greatfree.concurrency.Sync;

/**
 * 
 * @author Bing Li
 * 
 * 02/01/2022, Bing Li
 *
 */
class RspHandler
{
	private final static Logger log = Logger.getLogger("edu.greatfree.networking.nio.rox.object");
	
	/*
	 * When multiple clients are available, the queue cannot handle? 01/18/2022, Bing Li
	 */
	private Queue<ClientDataEvent> queue = new ConcurrentLinkedQueue<ClientDataEvent>();
	private Sync sync;
	
	public RspHandler()
	{
		this.sync = new Sync();
	}
	
//	public boolean processData(LargeObjectClient client, SocketChannel server, ByteBuffer buffer, int count)
	public void processData(LargeObjectClient client, SocketChannel server, ByteBuffer buffer, int count)
	{
		log.info("Data Received: " + count);
		if (count > 0)
		{
			byte[] receivedBytes = new byte[count];
			buffer.flip();
			buffer.get(receivedBytes);
			buffer.clear();
			queue.add(new ClientDataEvent(client, server, receivedBytes));
//			return false;
		}
		else
		{
			log.info("Data Received: signaled!");
//			queue.notify();
			this.sync.signal();
//			return true;
		}
	}

	public synchronized byte[] waitForResponse() throws IOException
	{
		ClientDataEvent dataEvent = null;
		while (queue.isEmpty())
		{
//				queue.wait();
			this.sync.holdOn();
		}
		while (!queue.isEmpty())
		{
			dataEvent = queue.poll();
			dataEvent.client.keepData(dataEvent.server, dataEvent.data);
		}
		if (dataEvent != null)
		{
			log.info("Data Received: signal received!");
			return dataEvent.client.obtainFinalData(dataEvent.server);
		}
		return null;
	}

}
