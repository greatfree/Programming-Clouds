package edu.greatfree.notification.message;

import org.greatfree.message.ServerMessage;

// Created: 04/01/2019, Bing Li
public class MyNotification extends ServerMessage
{
	private static final long serialVersionUID = -2008156377623500674L;
	
	private Book book;
	
	private String message;

	public MyNotification(Book book, String message)
	{
		super(MessageType.MY_NOTIFICATION);
		this.book = book;
		this.message = message;
	}
	
	public Book getBook()
	{
		return this.book;
	}
	
	public String getMessage()
	{
		return this.message;
	}

}
