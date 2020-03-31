package org.greatfree.testing.server;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;
import org.greatfree.testing.message.TestRequest;
import org.greatfree.testing.message.TestResponse;
import org.greatfree.testing.message.TestStream;

// Created: 12/10/2016, Bing Li
class TestRequestThreadCreator implements RequestThreadCreatable<TestRequest, TestStream, TestResponse, TestRequestThread>
{

	@Override
	public TestRequestThread createRequestThreadInstance(int taskSize)
	{
		return new TestRequestThread(taskSize);
	}

}
