package org.greatfree.testing.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.testing.message.WeatherRequest;
import org.greatfree.testing.message.WeatherResponse;
import org.greatfree.testing.message.WeatherStream;

/*
 * This is a class that creates the instance of WeatherThread. It is used by the RequestDispatcher to create the instances in a high-performance and low-cost manner. 02/15/2016, Bing Li
 */

// Created: 02/15/2016, Bing Li
class WeatherThreadCreator implements RequestQueueCreator<WeatherRequest, WeatherStream, WeatherResponse, WeatherThread>
{
	@Override
	public WeatherThread createInstance(int taskSize)
	{
		return new WeatherThread(taskSize);
	}
}
