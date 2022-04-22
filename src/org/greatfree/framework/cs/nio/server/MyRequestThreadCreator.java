package org.greatfree.framework.cs.nio.server;

import org.greatfree.concurrency.reactive.nio.RequestQueueCreator;
import org.greatfree.framework.cs.nio.message.MyRequest;
import org.greatfree.framework.cs.nio.message.MyResponse;
import org.greatfree.framework.cs.nio.message.MyStream;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 *
 */
class MyRequestThreadCreator implements RequestQueueCreator<MyRequest, MyStream, MyResponse, MyRequestThread>
{

	@Override
	public MyRequestThread createInstance(int taskSize)
	{
		return new MyRequestThread(taskSize);
	}

}
