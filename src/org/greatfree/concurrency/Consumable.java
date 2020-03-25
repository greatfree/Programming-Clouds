package org.greatfree.concurrency;

/*
 * The interface defines the method for a consumer in the producer/consumer pattern. 11/30/2014, Bing Li
 */

// Created: 11/11/2014, Bing Li
public interface Consumable<Food>
{
	public void consume(Food food);
}
