package org.greatfree.client.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.RunnerTask;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ByteList;
import org.greatfree.util.Tools;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 *
 */
// public class SyncRemoteEventer<Notification extends ServerMessage> extends RunnerTask
public class CSClient extends RunnerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.client.nio");

//	private InetAddress hostAddress;
//	private int port;
	private Selector selector;
	private ByteBuffer readBuffer;
	private Queue<Request> requests;
	private Map<SocketChannel, ByteList> buffers;
	private Map<SocketChannel, ResponseHandler> responseHandlers;
	
//	private Runner<ClientTask<Notification>> runner;
	private Runner<CSClient> client;
	private AtomicBoolean isClosed;
	
//	public SyncRemoteEventer(String serverIP, int serverPort) throws IOException
	public CSClient(int bufferSize) throws IOException
	{
//		this.hostAddress = InetAddress.getByName(serverIP);
//		this.port = serverPort;
		this.selector = SelectorProvider.provider().openSelector();
		this.readBuffer = ByteBuffer.allocate(bufferSize);
		this.requests = new ConcurrentLinkedQueue<Request>();
		this.buffers = new ConcurrentHashMap<SocketChannel, ByteList>();
		this.responseHandlers = new ConcurrentHashMap<SocketChannel, ResponseHandler>();
		this.isClosed = new AtomicBoolean(false);

		this.client = new Runner<CSClient>(this, false);
		this.client.start();
	}

	/*
	public void start()
	{
//		this.runner = new Runner<ClientTask<Notification>>(new ClientTask<Notification>(this), false);
		this.runner = new Runner<CSClient>(this, false);
		this.runner.start();
	}
	*/
	
	public void close(long timeout) throws InterruptedException, IOException
	{
		this.readBuffer.clear();
		this.readBuffer = null;
		this.buffers.clear();
		this.buffers = null;
		this.responseHandlers.clear();
		this.responseHandlers = null;
		this.isClosed.set(true);
		this.selector.close();
		this.client.stop(timeout);
	}

	@Override
	public void run()
	{
		while (!this.isClosed.get())
		{
			for (Request entry : this.requests)
			{
				switch (entry.type)
				{
					case Request.CHANGEOPS:
						SelectionKey key = entry.server.keyFor(this.selector);
						key.interestOps(entry.ops);
						break;
						
					case Request.REGISTER:
						try
						{
							entry.server.register(this.selector, entry.ops);
						}
						catch (ClosedChannelException e)
						{
							e.printStackTrace();
						}
						break;
				}
			}
			this.requests.clear();
			
			try
			{
				this.selector.select();
				Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext())
				{
					SelectionKey key = selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid())
					{
						continue;
					}

					if (key.isConnectable())
					{
						try
						{
							this.finishConnection(key);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
					else if (key.isReadable())
					{
						this.read(key);
					}
					else if (key.isWritable())
					{
						try
						{
							this.write(key);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			catch (ClosedSelectorException | IOException e)
			{
				log.info("Client selector is closed!");
			}
		}
	}
	
	public void notify(String ip, int port, ServerMessage notification) throws IOException
	{
//		ResponseHandler handler = new ResponseHandler(this.buffers);
		this.send(ip, port, Tools.serialize(notification), new ResponseHandler(this.buffers));
	}
	
	public ServerMessage read(String ip, int port, ServerMessage request) throws IOException, ClassNotFoundException
	{
		ResponseHandler handler = new ResponseHandler(this.buffers);
		this.send(ip, port, Tools.serialize(request), handler);
		byte[] response = handler.waitForResponse();
		if (response != null)
		{
			return (ServerMessage)Tools.deserialize(response);
		}
		return null;
	}

	private void send(String ip, int port, byte[] data, ResponseHandler handler) throws IOException
	{
		SocketChannel server = this.initiateConnection(ip, port);
		this.responseHandlers.put(server, handler);
		ByteList bytes = this.buffers.get(server);
		if (bytes == null)
		{
			bytes = new ByteList();
			this.buffers.put(server, bytes);
		}
		this.buffers.get(server).add(data);
		this.selector.wakeup();
	}

	/*
	public void retainData(SocketChannel server, byte[] data)
	{
		log.info("Data kept = " + data.length);
		ByteList bytes = this.buffers.get(server);
		if (bytes == null)
		{
			bytes = new ByteList();
			this.buffers.put(server, bytes);
		}
		this.buffers.get(server).add(data);
	}

	public byte[] obtainFinalData(SocketChannel server)
	{
		ByteList bytes = this.buffers.get(server);
		if (bytes != null)
		{
			return bytes.getAllBytes();
		}
		return null;
	}
	
	private Selector initSelector() throws IOException
	{
		return SelectorProvider.provider().openSelector();
	}
	*/

	private void read(SelectionKey key)
	{
		SocketChannel server = (SocketChannel) key.channel();
		int numRead = 0;
		do
		{
			this.readBuffer.clear();
			log.info("Received data = " + numRead);
			try
			{
				numRead = server.read(this.readBuffer);
				this.handleResponse(server, this.readBuffer, numRead);
			}
			catch (IOException e)
			{
				key.cancel();
				try
				{
					server.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
				return;
			}
		}
		while (numRead > 0);
		
		if (numRead == -1)
		{
			// Remote entity shut the socket down cleanly. Do the
			// same from our end and cancel the channel.
			try
			{
				key.channel().close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			key.cancel();
			return;
		}
	}

	private SocketChannel initiateConnection(String ip, int port) throws IOException
	{
		InetAddress serverAddress = InetAddress.getByName(ip);
		// Create a non-blocking socket channel
		SocketChannel server = SocketChannel.open();
		server.configureBlocking(false);

		// Kick off connection establishment
//		server.connect(new InetSocketAddress(this.hostAddress, this.port));
		server.connect(new InetSocketAddress(serverAddress, port));

		// Queue a channel registration since the caller is not the
		// selecting thread. As part of the registration we'll register
		// an interest in connection events. These are raised when a channel
		// is ready to complete connection establishment.
		this.requests.add(new Request(server, Request.REGISTER, SelectionKey.OP_CONNECT));
		return server;
	}

	private void finishConnection(SelectionKey key) throws IOException
	{
		SocketChannel server = (SocketChannel) key.channel();

		// Finish the connection. If the connection operation failed
		// this will raise an IOException.
		try
		{
			server.finishConnect();
		}
		catch (IOException e)
		{
			// Cancel the channel's registration with our selector
			System.out.println(e);
			key.cancel();
			return;
		}

		// Register an interest in writing on this channel
		key.interestOps(SelectionKey.OP_WRITE);
	}

	private void handleResponse(SocketChannel server, ByteBuffer buffer, int numRead) throws IOException
	{
		ResponseHandler handler = this.responseHandlers.get(server);
//		handler.processData(this, server, buffer, numRead);
		handler.processData(server, buffer, numRead);
	}

	private void write(SelectionKey key) throws IOException
	{
		SocketChannel server = (SocketChannel) key.channel();
		ByteList bytes = this.buffers.get(server);
		if (bytes != null)
		{
			log.info("bytes to be sent: = " + bytes.getAllBytes().length);
			ByteBuffer buffer = ByteBuffer.wrap(bytes.getAllBytes());
			while (buffer.hasRemaining())
			{
				server.write(buffer);
			}
			bytes = null;
			this.buffers.remove(server);
		}
		key.interestOps(SelectionKey.OP_READ);
	}

	@Override
	public int getWorkload()
	{
		return 0;
	}

	@Override
	public void dispose() throws InterruptedException
	{
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
	}
}
