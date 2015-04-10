package com.greatfree.concurrency;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;

/*
 * This is a producer/consumer pattern class to input received messages into a concurrency mechanism, the server dispatcher, smoothly. 07/30/2014, Bing Li
 */

// Created: 07/30/2014, Bing Li
public class MessageProducer<Consumer extends ServerMessageDispatcher<ServerMessage>> extends Thread
{
	// A dispatcher that processes messages concurrently. 08/04/2014, Bing Li
	private Consumer consumer;
	// A queue to schedule messages in the way of first-in-first-out. 08/04/2014, Bing Li
	private Queue<OutMessageStream<ServerMessage>> queue;
	// The collaborator supports the concurrency control. 08/04/2014, Bing Li
	private Collaborator collaborator;

	// Initializing ... 08/04/2014, Bing Li
	public MessageProducer(Consumer consumer)
	{
		// The consumer is defined and initialized outside the message producer. 08/22/2014, Bing Li
		this.consumer = consumer;
		// Initialize a concurrency controlled queue to keep the messages in a thread-safe way. 08/22/2014, Bing Li
		this.queue = new LinkedBlockingQueue<OutMessageStream<ServerMessage>>();
		// Initialize a collaborator for the notify-wait mechanism. 08/22/2014, Bing Li
		this.collaborator = new Collaborator();
	}

	/*
	 * Disposing ... 08/04/2014, Bing Li
	 */
	public void dispose()
	{
		// Set the shutdown flag to true. 08/22/2014, Bing Li
		this.collaborator.setShutdown();
		// Notify all the threads that hold the lock to check the shutdown flag. 08/22/2014, Bing Li
		this.collaborator.signalAll();
		// Since the message producer is shutdown, the queue can be cleared. 08/22/2014, Bing Li
		this.queue.clear();
		// Shutdown the consumer. 08/22/2014, Bing Li
		this.consumer.shutdown();
	}

	/*
	 * Push new messages into the queue and notify the associated thread to process. 08/04/2014, Bing Li
	 */
	public synchronized void produce(OutMessageStream<ServerMessage> message)
	{
		// Push the messages into the queue. 08/22/2014, Bing Li
		this.queue.add(message);
		// Notify the running thread to process the message. 08/22/2014, Bing Li
		this.collaborator.signal();
	}

	/*
	 * 	Wait for the available messages to process. 08/04/2014, Bing Li
	 */
	public void run()
	{
		try
		{
			OutMessageStream<ServerMessage> message;
			// An always running loop to keep the thread alive all the time until it is disposed explicitly. 08/22/2014, Bing Li
			while (!this.collaborator.isShutdown())
			{
				// Check whether the queue is empty or not. If messages exist in the queue, all of them must be processed until the queue is empty. 08/22/2014, Bing Li
				while (!this.queue.isEmpty())
				{
					// Dequeue a message in the queue. 08/22/2014, Bing Li
					message = this.queue.poll();
					// Process the message by the consumer, which is defined outside the class. 08/22/2014, Bing Li
					this.consumer.consume(message);
				}
				// If the message queue is empty, the thread needs to wait until messages are received. 08/22/2014, Bing Li
				this.collaborator.holdOn();
			}
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
