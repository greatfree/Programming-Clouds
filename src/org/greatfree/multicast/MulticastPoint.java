package org.greatfree.multicast;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.greatfree.concurrency.Sync;
import org.greatfree.message.multicast.MulticastResponse;

// Created: 11/07/2018, Bing Li
public final class MulticastPoint
{
	private final String collaboratorKey;
	
	private Sync sync;
	private AtomicInteger receiverCount;
	private List<MulticastResponse> responses;
	
	public MulticastPoint(String collaboratorKey)
	{
		this.collaboratorKey = collaboratorKey;
		this.sync = new Sync(false);
		this.receiverCount = new AtomicInteger(0);
		this.responses = new CopyOnWriteArrayList<MulticastResponse>();
	}
	
	public String getCollabratorKey()
	{
		return this.collaboratorKey;
	}
	
	public boolean isFull()
	{
//		System.out.println("MulticastPoint-isFull(): collaboratorKey = " + this.collaboratorKey);
//		System.out.println("MulticastPoint-isFull(): receiver count = " + this.receiverCount.get());
//		System.out.println("MulticastPoint-isFull(): responses size = " + this.responses.size());
		return this.responses.size() >= this.receiverCount.get();
	}
	
	public boolean isAvailable()
	{
		return this.responses.size() > 0;
	}
	
	public void addResponse(MulticastResponse response)
	{
		this.responses.add(response);
	}
	
	public void addResponses(List<MulticastResponse> responses)
	{
		this.responses.addAll(responses);
	}

	public int getResponseCount()
	{
		return this.responses.size();
	}
	
	public void signal()
	{
		this.sync.signal();
	}
	
	public void signalAll()
	{
		this.sync.signalAll();
	}
	
	public void setReceiverSize(int count)
	{
		this.receiverCount.set(count);
	}

	public int getReceiverSize()
	{
		return this.receiverCount.get();
	}
	
	public void decrementReceiverSize()
	{
		this.receiverCount.decrementAndGet();
	}
	
	public void holdOn(long waitTime)
	{
		this.sync.holdOn(waitTime);
	}
	
	public List<MulticastResponse> getResponses()
	{
		return this.responses;
	}
}
