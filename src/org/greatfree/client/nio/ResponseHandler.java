package org.greatfree.client.nio;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.greatfree.concurrency.Sync;
import org.greatfree.server.ByteList;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 *
 */
class ResponseHandler
{
//	private Queue<ClientEvent<ServerMessage>> queue;
	private Queue<ClientEvent> queue;
	private Sync sync;
	private Map<SocketChannel, ByteList> buffers;

	public ResponseHandler(Map<SocketChannel, ByteList> buffers)
	{
//		this.queue = new ConcurrentLinkedQueue<ClientEvent<ServerMessage>>();
		this.queue = new ConcurrentLinkedQueue<ClientEvent>();
		this.sync = new Sync();
		this.buffers = buffers;
	}

//	public void processData(SyncRemoteEventer<Notification> client, SocketChannel server, ByteBuffer buffer, int count)
	public void processData(SocketChannel server, ByteBuffer buffer, int count)
	{
		if (count > 0)
		{
			byte[] receivedBytes = new byte[count];
			buffer.flip();
			buffer.get(receivedBytes);
			buffer.clear();
//			this.queue.add(new ClientEvent<Notification>(client, server, receivedBytes));
//			this.queue.add(new ClientEvent<ServerMessage>(server, receivedBytes));
			this.queue.add(new ClientEvent(server, receivedBytes));
		}
		else
		{
			this.sync.signal();
		}
	}

	private void retainData(SocketChannel server, byte[] data)
	{
		ByteList bytes = this.buffers.get(server);
		if (bytes == null)
		{
			bytes = new ByteList();
			this.buffers.put(server, bytes);
		}
		this.buffers.get(server).add(data);
	}

	private byte[] obtainFinalData(SocketChannel server)
	{
		ByteList bytes = this.buffers.get(server);
		if (bytes != null)
		{
			return bytes.getAllBytes();
		}
		return null;
	}

	public synchronized byte[] waitForResponse()
	{
		ClientEvent event = null;
		while (this.queue.isEmpty())
		{
			this.sync.holdOn();
		}
		while (!this.queue.isEmpty())
		{
			event = this.queue.poll();
//			event.client.retainData(event.server, event.data);
			this.retainData(event.server, event.data);
		}
		if (event != null)
		{
//			return event.client.obtainFinalData(event.server);
			return this.obtainFinalData(event.server);
		}
		return null;
	}
}
