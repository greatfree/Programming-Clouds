package com.greatfree.testing.coordinator.memorizing;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.greatfree.remote.IPPort;
import com.greatfree.testing.message.NodeKeyNotification;

/*
 * The class is derived from Thread. It is responsible for connecting the remote memory server such that it is feasible for the server to notify the client even without the memory server's request. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class ConnectMemServerThread extends Thread
{
	// The queue keeps the clients' IPs which need to be connected to. 11/28/2014, Bing Li
	private Queue<IPPort> ipQueue;
	
	/*
	 * Initialize. 11/28/2014, Bing Li
	 */
	public ConnectMemServerThread()
	{
		this.ipQueue = new LinkedBlockingQueue<IPPort>();
	}
	
	/*
	 * Dispose the resource of the class. 11/28/2014, Bing Li
	 */
	public synchronized void dispose()
	{
		if (this.ipQueue != null)
		{
			this.ipQueue.clear();
			this.ipQueue = null;
		}
	}
	
	/*
	 * Input the IP address and the port number into the queue. They are connected in the order of first-in-first-out. The CrawlServerClientPool is responsible for the connections. 11/28/2014, Bing Li
	 */
	public void enqueue(IPPort ipPort)
	{
		this.ipQueue.add(ipPort);
	}
	
	/*
	 * Connect the remote IP addresses and the associated port numbers concurrently. 11/28/2014, Bing Li
	 */
	@Override
	public void run()
	{
		IPPort ipPort;
		// The thread keeps working until all of the IP addresses are connected. 11/28/2014, Bing Li
		while (this.ipQueue.size() > 0)
		{
			// Dequeue the IP addresses. 11/28/2014, Bing Li
			ipPort = this.ipQueue.poll();
			try
			{
				// Notify the remote node its unique ID. This is usually used to set up a multicasting cluster which contains a bunch of nodes. Each of them is identified by the ID. 11/28/2014, Bing Li
				MemoryServerClientPool.COORDINATE().getPool().send(ipPort, new NodeKeyNotification(ipPort.getObjectKey()));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
