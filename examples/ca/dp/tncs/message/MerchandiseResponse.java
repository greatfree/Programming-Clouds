package ca.dp.tncs.message;

import org.greatfree.message.ServerMessage;

// Created: 02/22/2020, Bing Li
public class MerchandiseResponse extends ServerMessage
{
	private static final long serialVersionUID = -2128194938993306351L;
	
	private Book book;

	public MerchandiseResponse(Book book)
	{
		super(TNCSConfig.MERCHANDISE_RESPONSE);
		this.book = book;
	}

	public Book getBook()
	{
		return this.book;
	}
}
