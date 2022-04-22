package org.greatfree.server.nio;

import java.nio.channels.SocketChannel;

/**
 * 
 * @author imac
 * 
 * 02/01/2022, Bing Li
 *
 */
class Request
{
	public static final int REGISTER = 1;
	public static final int CHANGEOPS = 2;

	public SocketChannel client;
	public int type;
	public int ops;

	public Request(SocketChannel client, int type, int ops)
	{
		this.client = client;
		this.type = type;
		this.ops = ops;
	}
}
