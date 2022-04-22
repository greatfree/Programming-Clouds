package org.greatfree.server.nio;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.greatfree.concurrency.RunnerTask;
import org.greatfree.concurrency.Sync;
import org.greatfree.data.ServerConfig;
import org.greatfree.server.ByteList;

/**
 * 
 * @author libing
 * 
 * 01/28/2022, Bing Li
 *
 */
// class DataDeamon<Dispatcher extends ServerDispatcher<ServerMessage>> implements Runnable
// class DataDeamon<Dispatcher extends ServerDispatcher<ServerMessage>> extends RunnerTask
class Daemon extends RunnerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.server.nio");

//	private Queue<ServerEvent<Dispatcher>> queue;
	private Queue<ServerEvent> queue;
	private Sync sync;
	private Queue<Request> requests;
	private Map<SocketChannel, ByteList> buffers;
	private Selector selector;
	private AtomicBoolean isClosed;
	private AtomicBoolean isShutdownFinished;
	
	public Daemon(Queue<Request> requests, Map<SocketChannel, ByteList> buffers, Selector selector)
	{
//		this.queue = new ConcurrentLinkedQueue<ServerEvent<Dispatcher>>();
		this.queue = new ConcurrentLinkedQueue<ServerEvent>();
		this.sync = new Sync();
		this.requests = requests;
		this.buffers = buffers;
		this.selector = selector;
		this.isClosed = new AtomicBoolean(false);
		this.isShutdownFinished = new AtomicBoolean(false);
	}

	private void retainRequest(SocketChannel client, byte[] request)
	{
		this.requests.add(new Request(client, Request.CHANGEOPS, SelectionKey.OP_WRITE));
		ByteList bytes = this.buffers.get(client);
		if (bytes == null)
		{
			bytes = new ByteList();
			this.buffers.put(client, bytes);
		}
		this.buffers.get(client).add(request);
		this.selector.wakeup();
	}

//	public void processData(CSServer<Dispatcher> server, SocketChannel client, ByteBuffer buffer, int count)
	public void processData(SocketChannel client, ByteBuffer buffer, int count)
	{
//		log.info("Data is processed: count = " + count);
		if (count > 0)
		{
			byte[] receivedBytes = new byte[count];
			buffer.flip();
			buffer.get(receivedBytes);
			buffer.clear();
//			this.queue.add(new ServerEvent<Dispatcher>(server, client, receivedBytes));
//			this.queue.add(new ServerEvent<Dispatcher>(client, receivedBytes));
			this.queue.add(new ServerEvent(client, receivedBytes));
		}
		else
		{
//			log.info("Signaled! Data is processed: count = " + count);
			this.sync.signal();
		}
	}

	@Override
	public void run()
	{
//		ServerEvent<Dispatcher> event;
		ServerEvent event;
		while (!this.isClosed.get())
		{
			while (this.queue.isEmpty())
			{
//				log.info("1) Daemon thread being ended ...");
				this.sync.holdOn();
//				log.info("2) Daemon thread being ended ...");
				if (this.isClosed.get())
				{
					return;
				}
			}
			event = this.queue.poll();
//			log.info("3) Daemon thread being ended ...");
//			event.server.retainRequest(event.client, event.message);
//			log.info("ServerEvent is dequeued ...");
			this.retainRequest(event.client, event.message);
		}
//		log.info("Daemon thread ended ...");
		this.isShutdownFinished.set(true);
	}

	@Override
	public int getWorkload()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void dispose()
	{
//		log.info("Server daemon disposing ...");
		this.isClosed.set(true);
		while (!this.isShutdownFinished.get())
		{
			this.sync.shutdown();
			try
			{
				Thread.sleep(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
			}
			catch (InterruptedException e)
			{
				log.info("Server daemon interrupted ...");
			}
		}
//		log.info("Server daemon disposed ...");
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}
}
