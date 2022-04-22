package org.greatfree.server;

import java.nio.channels.SocketChannel;

/**
 * 
 * @author libing
 * 
 * 01/28/2022, Bing Li
 *
 */
class ChangeRequest
{
	public static final int REGISTER = 1;
	public static final int CHANGEOPS = 2;

	public SocketChannel client;
	public int type;
	public int ops;

	public ChangeRequest(SocketChannel client, int type, int ops)
	{
		this.client = client;
		this.type = type;
		this.ops = ops;
	}
}
