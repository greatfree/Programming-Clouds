package com.greatfree.concurrency;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/*
 * This is an implementation of the pattern of producer/consumer. 11/11/2014, Bing Li
 */

// Created: 11/11/2014, Bing Li
public class ConsumerThread<Food, Consumer extends Consumable<Food>> extends Thread
{
	// Declare a queue to take the task. 11/11/2014, Bing Li
	private Queue<Food> queue;
	// Declare an instance of Collaborator that is used to coordinate threads to produce and consume products. 11/11/2014, Bing Li
	private Collaborator collaborator;
	// The consumer that consumes products. It defined how to process the injected food/products. 11/11/2014, Bing Li
	private Consumable<Food> consumer;
	// This is a flag to indicate the all of the food (products) is put into the queue. 11/11/2014, Bing Li
	private boolean isAllFoodQueued;
	// When the queue is empty but some food (products) is still not put into the queue, it is required to wait for some time. The argument defines the length of waiting time. 11/11/2014, Bing Li
	private long waitTime;

	/*
	 * Initialize the consumer. 11/20/2014, Bing Li
	 */
	public ConsumerThread(Consumable<Food> consumer, long waitTime)
	{
		this.queue = new LinkedBlockingQueue<Food>();
		this.collaborator = new Collaborator();
		this.consumer = consumer;
		this.isAllFoodQueued = false;
		this.waitTime = waitTime;
	}

	/*
	 * Dispose the consumer thread. 11/20/2014, Bing Li
	 */
	public void dispose()
	{
		// Set the flag of shutdown to terminate the loop in the consumer. 11/20/2014, Bing Li
		this.collaborator.setShutdown();
		// Signal all of the potentially waiting threads that the consumer is dead. They will not keep waiting for the signal. 11/20/2014, Bing Li
		this.collaborator.signalAll();
		// Clear the queue. 11/20/2014, Bing Li
		this.queue.clear();
		try
		{
			// Waiting for the consumer thread to be dead. 11/20/2014, Bing Li
			this.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Return the length of products/food to be consumed. 11/20/2014, Bing Li
	 */
	public int queueLength()
	{
		return this.queue.size();
	}

	/*
	 * Produce food such that the consumer can keep eating. 11/20/2014, Bing Li
	 */
	public void produce(Food food)
	{
		// Enqueue the food. 11/20/2014, Bing Li
		this.queue.add(food);
		// Notify the waiting thread that is hungry. 11/20/2014, Bing Li
		this.signal();
	}

	/*
	 * When no food is available, set the relevant flag. Thus, the consumer can be terminated its running. 11/20/2014, Bing Li
	 */
	public synchronized void setIsFoodQueued(boolean isAllFoodQueued)
	{
		// Update the current state of whether food is still available. 11/20/2014, Bing Li
		this.isAllFoodQueued = isAllFoodQueued;
		// Check whether all of the food is enqueued. 11/20/2014, Bing Li
		if (this.isAllFoodQueued)
		{
			// Notify the waiting consumer that it is time to be dismissed. 11/20/2014, Bing Li
			this.signal();
		}
	}

	/*
	 * Signal the consumer thread. 11/20/2014, Bing Li
	 */
	private void signal()
	{
		this.collaborator.signal();
	}

	/*
	 * Consume food/products concurrently. 11/20/2014, Bing Li
	 */
	public void run()
	{
		// Declare an instance of Food, i.e., a product. 11/20/2014, Bing Li
		Food food;
		// Check whether the consumer is already shutdown. 11/20/2014, Bing Li
		while (!this.collaborator.isShutdown())
		{
			// Check whether the queue is empty. 11/20/2014, Bing Li
			while (!this.queue.isEmpty())
			{
				// Dequeue the food. 11/2014, Bing Li
				food = this.queue.poll();
				// Consume the food. 11/20/2014, Bing Li
				this.consumer.consume(food);
			}
			// Check whether all of the food is already enqueued. 11/20/2014, Bing Li
			if (!this.isAllFoodQueued)
			{
				// If not all of the food is enqueued, it needs to wait for future newly enqueued food. 11/20/2014, Bing Li
				try
				{
					this.collaborator.holdOn(this.waitTime);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				// If all of the food is consumed, it is time to terminate the consumer thread. 11/20/2014, Bing Li
				return;
			}
		}
	}
}
