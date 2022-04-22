package org.greatfree.concurrency.reactive.nio;

import org.greatfree.message.ServerMessage;
import org.greatfree.server.nio.MessageStream;

/**
 * 
 * @author Bing Li
 * 
 * 02/02/2022, Bing Li
 * 
 * The class is used to replace org.greatfree.concurrency.reactive.RequestQueueCreator with the Java NIO. 02/02/2022, Bing Li
 *
 */
public interface RequestQueueCreator<Request extends ServerMessage, Stream extends MessageStream<Request>, Response extends ServerMessage, RequestThread extends RequestQueue<Request, Stream, Response>>
{
	public RequestThread createInstance(int taskSize);
}
