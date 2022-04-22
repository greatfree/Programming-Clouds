package org.greatfree.framework.cs.nio.message;

import java.nio.channels.SocketChannel;

import org.greatfree.server.nio.MessageStream;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 *
 */
public class MyStream extends MessageStream<MyRequest>
{

	public MyStream(SocketChannel channel, MyRequest message)
	{
		super(channel, message);
	}

}
