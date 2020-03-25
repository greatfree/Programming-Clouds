package org.greatfree.testing.server;

import java.io.IOException;

import org.greatfree.cache.db.DBConfig;
import org.greatfree.concurrency.reactive.RequestQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.testing.db.NodeDB;
import org.greatfree.testing.db.NodeDBPool;
import org.greatfree.testing.message.SignUpRequest;
import org.greatfree.testing.message.SignUpResponse;
import org.greatfree.testing.message.SignUpStream;
import org.greatfree.util.Tools;

/*
 * This is an example to use RequestQueue, which receives users' requests and responds concurrently. 11/04/2014, Bing Li
 */

// Created: 09/22/2014, Bing Li
public class SignUpThread extends RequestQueue<SignUpRequest, SignUpStream, SignUpResponse>
{
	/*
	 * Initialize the thread. The value of maxTaskSize is the length of the queue to take the count of requests. 11/04/2014, Bing Li
	 */
	public SignUpThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	/*
	 * Respond users' requests concurrently. 11/04/2014, Bing Li
	 */
	public void run()
	{
		// Declare the request stream. 11/04/2014, Bing Li
		SignUpStream request;
		// Declare the response. 11/04/2014, Bing Li
		SignUpResponse response;
		// Get an instance of NodeDB to save new nodes. 11/04/2014, Bing Li
		NodeDB db = NodeDBPool.PERSISTENT().getDB(DBConfig.NODE_DB_PATH);
		// The thread is shutdown when it is idle long enough. Before that, the thread keeps alive. It is necessary to detect whether it is time to end the task. 11/04/2014, Bing Li
		while (!this.isShutdown())
		{
			// The loop detects whether the queue is empty or not. 11/04/2014, Bing Li
			while (!this.isEmpty())
			{
				// Dequeue a request. 11/04/2014, Bing Li
				request = this.getRequest();
				// Persist the node locally. 11/04/2014, Bing Li
				db.saveNode(new Node(Tools.getHash(request.getMessage().getUserName()), request.getMessage().getUserName(), request.getMessage().getPassword()));
				// Initialize a new response. 11/04/2014, Bing Li
				response = new SignUpResponse(true);
				try
				{
					// Respond to the client. 11/04/2014, Bing Li
					this.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				// Dispose the request and the response. 11/04/2014, Bing Li
				this.disposeMessage(request, response);
			}
			try
			{
				// Wait for some time when the queue is empty. During the period and before the thread is killed, some new requests might be received. If so, the thread can keep working. 11/04/2014, Bing Li
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		// Collect the instance of the database after using it. 11/04/2014, Bing Li
		NodeDBPool.PERSISTENT().collectDB(db);
	}
}
