package org.greatfree.client.nio;

import java.nio.channels.SocketChannel;

/**
 * 
 * @author Bing Li
 * 
 * 02/05/2022
 *
 */
class Request
{
	public static final int REGISTER = 1;
	public static final int CHANGEOPS = 2;

	public SocketChannel server;
	public int type;
	public int ops;

	public Request(SocketChannel server, int type, int ops)
	{
		this.server = server;
		this.type = type;
		this.ops = ops;
	}
}
