package org.greatfree.server.nio;

import java.nio.channels.SocketChannel;

import org.greatfree.message.ServerMessage;

/**
 * 
 * @author Bing Li
 * 
 * 02/02/2022, Bing Li
 * 
 * The class is designed to incorporate SocketChannel to send messages to remote nodes. It replaces the old one, MessageStream, which employs the ObjectOutputStream. 02/02/2022, Bing Li
 *
 */
public class MessageStream<Message extends ServerMessage>
{
	private SocketChannel channel;
	private Message message;
	
	public MessageStream(SocketChannel channel, Message message)
	{
		this.channel = channel;
		this.message = message;
	}

	public SocketChannel getChannel()
	{
		return this.channel;
	}
	
	public Message getMessage()
	{
		return this.message;
	}
	
	public void disposeMessage()
	{
		this.message = null;
	}
}
