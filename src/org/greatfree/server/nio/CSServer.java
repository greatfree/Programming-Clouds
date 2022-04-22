package org.greatfree.server.nio;

import java.io.EOFException;
import java.io.IOException;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedSelectorException;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.RunnerTask;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ByteList;
import org.greatfree.util.Builder;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

/**
 * 
 * @author libing
 * 
 * 01/28/2022, Bing Li
 *
 */
public class CSServer<Dispatcher extends ServerDispatcher<ServerMessage>> extends RunnerTask
{
	private final static Logger log = Logger.getLogger("org.greatfree.server.nio");

	private final String id;
//	private InetAddress hostAddress;
	private final int port;
	private ServerSocketChannel serverChannel;
	private Selector selector;
	private ByteBuffer readBuffer;
//	private DataDeamon<Dispatcher> worker;
	private Queue<Request> requests;
	private final Map<SocketChannel, ByteList> buffers;
	
	private ServerMessageProducer<Dispatcher> producer;
	private Dispatcher dispatcher;
	private Runner<CSServer<Dispatcher>> server;
	private Runner<Daemon> daemon;
	private AtomicBoolean isClosed;
	
	public CSServer(CSServerBuilder<Dispatcher> builder) throws IOException
	{
		this.id = Tools.generateUniqueKey();
		this.port = builder.getPort();
		this.selector = this.initSelector();
		this.readBuffer = ByteBuffer.allocate(builder.getBufferSize());
		this.buffers = new ConcurrentHashMap<SocketChannel, ByteList>();
		this.requests = new ConcurrentLinkedQueue<Request>();

//		this.worker = new DataDeamon<Dispatcher>(this.requests, this.buffers, this.selector);
		this.producer = new ServerMessageProducer<Dispatcher>();
		this.dispatcher = builder.getDispatcher();
		this.dispatcher.setServerKey(this.id);
		this.isClosed = new AtomicBoolean(false);
	}

	public static class CSServerBuilder<Dispatcher extends ServerDispatcher<ServerMessage>> implements Builder<CSServer<Dispatcher>>
	{
		private int port;
		private int bufferSize;
		private Dispatcher dispatcher;

		public CSServerBuilder()
		{
		}

		public CSServerBuilder<Dispatcher> port(int port)
		{
			this.port = port;
			return this;
		}

		public CSServerBuilder<Dispatcher> bufferSize(int bufferSize)
		{
			this.bufferSize = bufferSize;
			return this;
		}

		public CSServerBuilder<Dispatcher> dispatcher(Dispatcher dispatcher)
		{
			this.dispatcher = dispatcher;
			return this;
		}

		@Override
		public CSServer<Dispatcher> build() throws IOException
		{
			return new CSServer<Dispatcher>(this);
		}

		public int getPort()
		{
			return this.port;
		}
		
		public int getBufferSize()
		{
			return this.bufferSize;
		}
		
		public Dispatcher getDispatcher()
		{
			return this.dispatcher;
		}
	}

	public void start()
	{
		this.producer.init(this.dispatcher);
		this.daemon = new Runner<Daemon>(new Daemon(this.requests, this.buffers, this.selector), false);
		this.daemon.start();
		this.server = new Runner<CSServer<Dispatcher>>(this, false);
		this.server.start();
	}
	
	public void stop(long timeout) throws InterruptedException, IOException
	{
		this.producer.dispose(timeout);
		this.isClosed.set(true);
		this.selector.close();
//		log.info("Stopping the Daemon ...");
		this.daemon.stop();
//		log.info("Stopped the Daemon ...");
		this.server.stop(timeout);
	}
	
	public String getID()
	{
		return this.id;
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
						SelectionKey key = entry.client.keyFor(this.selector);
						key.interestOps(entry.ops);
				}
			}
			this.requests.clear();
			
			try
			{
				this.selector.select();
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
//						log.info("Server acceptable ...");
						try
						{
							this.accept(key);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
					else if (key.isReadable())
					{
//						log.info("Server readable ...");
						this.read(key);
					}
					else if (key.isWritable())
					{
//						log.info("Server writable ...");
						try
						{
							this.write(key);
						}
						catch (ClassNotFoundException | IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			catch (ClosedSelectorException | IOException e)
			{
				log.info("Server selector is closed!");
			}
		}
	}

	/*
	public void retainRequest(SocketChannel client, byte[] request)
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
	*/
	
	/*
	public void removeClient(SocketChannel client)
	{
		this.buffers.remove(client);
	}
	*/

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
//			this.worker.processData(this, client, this.readBuffer, numRead);
//			this.worker.processData(client, this.readBuffer, numRead);
			this.daemon.getFunction().processData(client, this.readBuffer, numRead);
		}
		while (numRead > 0);
		
//		log.info("2) Server read numRead = " + numRead);
		if (numRead == -1)
		{
			this.buffers.remove(client);
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

	private void write(SelectionKey key) throws IOException, ClassNotFoundException
	{
		SocketChannel client = (SocketChannel) key.channel();
		ByteList bytes = this.buffers.get(client);
//		log.info("1) Message received ...");
		if (bytes != null)
		{
//			log.info("2) Message received ...");
			try
			{
				ServerMessage message = (ServerMessage)Tools.deserialize(bytes.getAllBytes());
//				log.info("Message deserialized ...");
				this.producer.produceMessage(new MessageStream<ServerMessage>(client, message));

				/*
				 * The buffer cannot be removed here. Otherwise, data cannot be retrieved in time by the Daemon. 02/09/2022, Bing Li
				 */
//				this.buffers.remove(client);
			}
			catch (EOFException | StreamCorruptedException e)
			{
//				this.buffers.remove(client);
//				e.printStackTrace();
				log.info("Data processsed ...");
			}
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
		InetSocketAddress isa = new InetSocketAddress(UtilConfig.NO_ADDRESS, this.port);
		this.serverChannel.socket().bind(isa);

		// Register the server socket channel, indicating an interest in
		// accepting new connections
		this.serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);
		return socketSelector;
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
