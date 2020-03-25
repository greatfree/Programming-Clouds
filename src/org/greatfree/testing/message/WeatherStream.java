package org.greatfree.testing.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 02/15/2016, Bing Li
public class WeatherStream extends OutMessageStream<WeatherRequest>
{
	// Initialize the instance of the request stream. 02/15/2016, Bing Li
	public WeatherStream(ObjectOutputStream out, Lock lock, WeatherRequest message)
	{
		super(out, lock, message);
	}
}
