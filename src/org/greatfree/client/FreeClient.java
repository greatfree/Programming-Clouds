package org.greatfree.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.message.InitReadNotification;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.FreeObject;
import org.greatfree.util.Tools;

/*
 * This is a TCP client that encloses some details of TCP APIs such that it is convenient for developers to interact with remote servers. Moreover, the client is upgraded to fit the caching management. 08/10/2014, Bing Li
 */

// Created: 08/10/2014, Bing Li
public class FreeClient extends FreeObject
{
	// A client socket that connects to the remote server socket at a remote end. In the system, the remote end is a node in a distributed cluster. 08/24/2014, Bing Li
	private Socket socket;
	// The IP address of the remote end. 08/24/2014, Bing Li
	private String serverAddress;
	// The port number of the remote end. 08/24/2014, Bing Li
	private int serverPort;
	// The output stream that sends data to the remote end. 08/24/2014, Bing Li
	private ObjectOutputStream out;
	// The input stream that receives data from the remote end. 08/24/2014, Bing Li
	private ObjectInputStream in;
	// The lock that keeps sending and receiving operations through the client atomic. The lock is used by RemoteReader. 08/24/2014, Bing Li
	private ReentrantLock lock;

	/*
	 * Initialize the client. 08/24/2014, Bing Li
	 */
//	public FreeClient(String serverAddress, int serverPort) throws IOException
	public FreeClient(String serverAddress, int serverPort, int timeout) throws IOException
	{
		// The key of the FreeClient is created upon the IP and the port of the remote end. 08/24/2014, Bing Li
		super(Tools.getKeyOfFreeClient(serverAddress, serverPort));
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.socket = new Socket(this.serverAddress, this.serverPort);
		/*
		 * I want to test whether the timeout works or not. 03/25/2020, Bing Lis
		 */
		this.socket.setSoTimeout(timeout);
		this.lock = new ReentrantLock();

		this.out = new ObjectOutputStream(this.socket.getOutputStream());
	}

	public FreeClient(String serverAddress, int serverPort) throws IOException
	{
		// The key of the FreeClient is created upon the IP and the port of the remote end. 08/24/2014, Bing Li
		super(Tools.getKeyOfFreeClient(serverAddress, serverPort));
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.socket = new Socket(this.serverAddress, this.serverPort);
		this.lock = new ReentrantLock();

		this.out = new ObjectOutputStream(this.socket.getOutputStream());
	}

	/*
	 * Dispose the associated resources. 08/24/2014, Bing Li
	 */
	public void dispose() throws IOException
	{
		if (this.out != null)
		{
			this.out.close();
		}
		if (this.in != null)
		{
			this.in.close();
		}
		if (this.socket != null)
		{
			this.socket.close();
		}
	}

	/*
	 * Expose the lock for outside invocation to keep the sending and receiving operations atomic. The lock is used by RemoteReader. 08/24/2014, Bing Li
	 */
	public ReentrantLock getLock()
	{
		return this.lock;
	}

	/*
	 * Expose the connected server IP address. 11/04/2014, Bing Li
	 */
	public String getServerAddress()
	{
		return this.serverAddress;
	}

	/*
	 * Expose the connected server port number. 11/04/2014, Bing Li
	 */
	public int getServerPort()
	{
		return this.serverPort;
	}

	/*
	 * Initialize the ObjectInputStream by sending a notification to the remote server. 11/04/2014, Bing Li
	 */
	public void initRead(String nodeKey) throws IOException
	{
		this.send(new InitReadNotification(nodeKey));
	}

	/*
	 * Initialize the ObjectInputStream after getting a feedback from the server. That means the server has already initialized the corresponding ObjectOutputStream. 11/04/2014, Bing Li
	 */
	public void setInputStream() throws IOException
	{
		this.in = new ObjectInputStream(this.socket.getInputStream());
	}

	/*
	 * Send a message to the remote end. The method is required to be synchronized to avoid potential memory leak in OutputObjectStream. 08/24/2014, Bing Li
	 */
	public synchronized void send(ServerMessage event) throws IOException
	{
		// Send the message to the remote end. 09/17/2014, Bing Li
		this.out.writeObject(event);
		// It is required to invoke the below methods to avoid the memory leak. 09/17/2014, Bing Li
		this.out.flush();
		this.out.reset();
	}

	/*
	 * Send a request to the remote end and wait until a response is received. The same as the method of Send(), the synchronized descriptor intends to avoid potential memory leak in OutputObjectStream. 08/24/2014, Bing Li
	 */
	public synchronized ServerMessage sendWithResponse(ServerMessage request) throws IOException, ClassNotFoundException, SocketTimeoutException
	{
		// Send the message to the remote end. 09/17/2014, Bing Li
		this.out.writeObject(request);
		// It is required to invoke the below methods to avoid the memory leak. 09/17/2014, Bing Li
		this.out.flush();
		this.out.reset();
		// Wait for the response from the remote end. 09/17/2014, Bing Li
		return (ServerMessage)this.in.readObject();
	}
}
