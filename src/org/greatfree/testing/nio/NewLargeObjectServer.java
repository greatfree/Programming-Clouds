package org.greatfree.testing.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Logger;

/**
 * 
 * @author Bing Li
 * 
 * 02/01/2022, Bing Li
 *
 */
class NewLargeObjectServer implements Runnable
{
	private final static Logger log = Logger.getLogger("edu.greatfree.networking.nio.rox.object.large.accepted");

	private InetAddress hostAddress;
	private int port;

	// The channel on which we'll accept connections
	private ServerSocketChannel serverChannel;

	// The selector we'll be monitoring
	private Selector selector;

	private ByteBuffer readBuffer = ByteBuffer.allocate(10);

	private NewLargeEchoWorker worker;

	private Queue<ChangeRequest> pendingChanges = new ConcurrentLinkedQueue<ChangeRequest>();
	private final Map<SocketChannel, ByteList> pendingData;

	public NewLargeObjectServer(InetAddress hostAddress, int port, NewLargeEchoWorker worker) throws IOException
	{
		this.hostAddress = hostAddress;
		this.port = port;
		this.selector = this.initSelector();
		this.worker = worker;
		this.pendingData = new ConcurrentHashMap<SocketChannel, ByteList>();
	}

	public void run()
	{
		while (true)
		{
			try
			{
				// Process any pending changes
				// Iterator<ChangeRequest> changes = this.pendingChanges.iterator();
				// while (changes.hasNext())
				for (ChangeRequest change : this.pendingChanges)
				{
					switch (change.type)
					{
						case ChangeRequest.CHANGEOPS:
							SelectionKey key = change.socket.keyFor(this.selector);
							key.interestOps(change.ops);
					}
				}
				this.pendingChanges.clear();

				// Wait for an event one of the registered channels
				this.selector.select();

				// Iterate over the set of keys for which events are available
				Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
				while (selectedKeys.hasNext())
				{
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();

					if (!key.isValid())
					{
						continue;
					}

					// Check what event is available and deal with it
					if (key.isAcceptable())
					{
						log.info("Server acceptable ...");
						this.accept(key);
					}
					else if (key.isReadable())
					{
						log.info("Server readable ...");
						this.read(key);
					}
					else if (key.isWritable())
					{
						log.info("Server writable ...");
						this.write(key);
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public void keepEchoData(SocketChannel client, byte[] data)
	{
//		log.info("Data in bytes to be sent ..." + data.length);
		this.pendingChanges.add(new ChangeRequest(client, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));

		ByteList bytes = this.pendingData.get(client);
		if (bytes == null)
		{
			bytes = new ByteList();
			this.pendingData.put(client, bytes);
		}
		this.pendingData.get(client).add(data);
		this.selector.wakeup();
	}

	private void accept(SelectionKey key) throws IOException
	{
		// For an accept to be pending the channel must be a server socket channel.
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

		// Accept the connection and make it non-blocking
		SocketChannel socketChannel = serverSocketChannel.accept();
		// Socket socket = socketChannel.socket();
		socketChannel.configureBlocking(false);

		// Register the new SocketChannel with our Selector, indicating
		// we'd like to be notified when there's data waiting to be read
		socketChannel.register(this.selector, SelectionKey.OP_READ);
	}

	private void read(SelectionKey key)
	{
		SocketChannel client = (SocketChannel) key.channel();
//		ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
		
		int numRead = 0;
		do
		{
			this.readBuffer.clear();
			try
			{
				numRead = client.read(this.readBuffer);
				log.info("1) Server read numRead = " + numRead);
			}
			catch (IOException e)
			{
				key.cancel();
				try
				{
					client.close();
				}
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
				return;
			}
			this.worker.processData(this, client, this.readBuffer, numRead);
		}
		while (numRead > 0);
		
		log.info("2) Server read numRead = " + numRead);
		if (numRead == -1)
		{
//			this.worker.processData(this, client, this.readBuffer, numRead);
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

	private void write(SelectionKey key) throws IOException
	{
		SocketChannel client = (SocketChannel) key.channel();
		ByteList bytes = this.pendingData.get(client);
		if (bytes != null)
		{
			ByteBuffer buffer = ByteBuffer.wrap(bytes.getAllBytes());
			log.info("Data size in buffer to be sent ..." + buffer.array().length);
			while (buffer.hasRemaining())
			{
				client.write(buffer);
			}
			bytes = null;
			this.pendingData.remove(client);
		}
		key.interestOps(SelectionKey.OP_READ);
	}

	private Selector initSelector() throws IOException
	{
		// Create a new selector
		Selector socketSelector = SelectorProvider.provider().openSelector();

		// Create a new non-blocking server socket channel
		this.serverChannel = ServerSocketChannel.open();
		this.serverChannel.configureBlocking(false);

		// Bind the server socket to the specified address and port
		InetSocketAddress isa = new InetSocketAddress(this.hostAddress, this.port);
		this.serverChannel.socket().bind(isa);

		// Register the server socket channel, indicating an interest in
		// accepting new connections
		this.serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

		return socketSelector;
	}

	public static void main(String[] args)
	{
		try
		{
			NewLargeEchoWorker worker = new NewLargeEchoWorker();
			new Thread(worker).start();
			new Thread(new NewLargeObjectServer(null, 9090, worker)).start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
