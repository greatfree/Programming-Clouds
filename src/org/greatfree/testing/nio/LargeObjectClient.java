package org.greatfree.testing.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

import org.greatfree.util.Tools;

/**
 * 
 * @author Bing Li
 * 
 * 02/01/2022, Bing Li
 *
 */
class LargeObjectClient implements Runnable
{
	private final static Logger log = Logger.getLogger("edu.greatfree.networking.nio.rox.object.large");

	private InetAddress hostAddress;
	private int port;

	// The selector we'll be monitoring
	private Selector selector;

//	private static final int BUFFER_SIZE = 10;

	// The buffer into which we'll read data when it's available
	private ByteBuffer readBuffer = ByteBuffer.allocate(10);

	// A list of PendingChange instances
	private Queue<ChangeRequest> pendingChanges = new ConcurrentLinkedQueue<ChangeRequest>();
	private final Map<SocketChannel, ByteList> pendingData;
	private Map<SocketChannel, RspHandler> rspHandlers = new ConcurrentHashMap<SocketChannel, RspHandler>();

	public LargeObjectClient(InetAddress hostAddress, int port) throws IOException
	{
		this.hostAddress = hostAddress;
		this.port = port;
		this.selector = this.initSelector();
		this.pendingData = new ConcurrentHashMap<SocketChannel, ByteList>();
	}
	
	@Override
	public void run()
	{
		while (true)
		{
			for (ChangeRequest change : this.pendingChanges)
			{
				switch (change.type)
				{
					case ChangeRequest.CHANGEOPS:
						SelectionKey key = change.socket.keyFor(this.selector);
						key.interestOps(change.ops);
						break;
					case ChangeRequest.REGISTER:
						try
						{
							change.socket.register(this.selector, change.ops);
						}
						catch (ClosedChannelException e)
						{
							e.printStackTrace();
						}
						break;
				}
			}
			this.pendingChanges.clear();

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

					// Check what event is available and deal with it
					if (key.isConnectable())
					{
						this.finishConnection(key);
					}
					else if (key.isReadable())
					{
						this.read(key);
					}
					else if (key.isWritable())
					{
						this.write(key);
					}
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void send(byte[] data, RspHandler handler) throws IOException
	{
		SocketChannel server = this.initiateConnection();
		this.rspHandlers.put(server, handler);
		ByteList bytes = this.pendingData.get(server);
		if (bytes == null)
		{
			bytes = new ByteList();
			this.pendingData.put(server, bytes);
		}
		this.pendingData.get(server).add(data);
		this.selector.wakeup();
	}
	
	private void read(SelectionKey key)
	{
		SocketChannel server = (SocketChannel) key.channel();
//		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

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
	
	private void handleResponse(SocketChannel server, ByteBuffer buffer, int numRead) throws IOException
	{
		RspHandler handler = this.rspHandlers.get(server);
		/*
		if (handler.processData(this, server, buffer, numRead))
		{
			server.close();
			server.keyFor(this.selector).cancel();
		}
		*/
		handler.processData(this, server, buffer, numRead);
	}
	
	public void keepData(SocketChannel server, byte[] data)
	{
		log.info("Data kept = " + data.length);
		ByteList bytes = this.pendingData.get(server);
		if (bytes == null)
		{
			bytes = new ByteList();
			this.pendingData.put(server, bytes);
		}
		this.pendingData.get(server).add(data);
	}
	
	public byte[] obtainFinalData(SocketChannel server)
	{
		ByteList bytes = this.pendingData.get(server);
		if (bytes != null)
		{
			return bytes.getAllBytes();
		}
		return null;
	}
	
//	public void write(SocketChannel server, byte[] data) throws IOException
	private void write(SelectionKey key) throws IOException
	{
		SocketChannel server = (SocketChannel) key.channel();

		ByteList bytes = this.pendingData.get(server);
		if (bytes != null)
		{
			log.info("bytes to be sent: = " + bytes.getAllBytes().length);
			ByteBuffer buffer = ByteBuffer.wrap(bytes.getAllBytes());
			while (buffer.hasRemaining())
			{
				server.write(buffer);
			}
			bytes = null;
			this.pendingData.remove(server);
		}
		key.interestOps(SelectionKey.OP_READ);
	}

	private Selector initSelector() throws IOException
	{
		// Create a new selector
		return SelectorProvider.provider().openSelector();
	}

	private SocketChannel initiateConnection() throws IOException
	{
		// Create a non-blocking socket channel
		SocketChannel server = SocketChannel.open();
		server.configureBlocking(false);

		// Kick off connection establishment
		server.connect(new InetSocketAddress(this.hostAddress, this.port));

		// Queue a channel registration since the caller is not the
		// selecting thread. As part of the registration we'll register
		// an interest in connection events. These are raised when a channel
		// is ready to complete connection establishment.
		this.pendingChanges.add(new ChangeRequest(server, ChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
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

	public static void main(String[] args)
	{
		Student s = new Student("Li", 13);
		try
		{
//			LargeObjectClient client = new LargeObjectClient(InetAddress.getByName("192.168.1.18"), 9090);
			LargeObjectClient client = new LargeObjectClient(InetAddress.getByName("192.168.3.8"), 9090);
			Thread t = new Thread(client);
			t.setDaemon(true);
			t.start();
			RspHandler handler = new RspHandler();
			client.send(Tools.serialize(s), handler);
			byte[] allBytes = handler.waitForResponse();
			if (allBytes != null)
			{
				Student so = (Student)Tools.deserialize(allBytes);
				System.out.println(so);
			}
			else
			{
				System.out.println("Null bytes are received!");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
