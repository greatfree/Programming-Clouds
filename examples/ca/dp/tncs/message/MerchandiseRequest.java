package ca.dp.tncs.message;

import org.greatfree.message.ServerMessage;

// Created: 02/22/2020, Bing Li
public class MerchandiseRequest extends ServerMessage
{
	private static final long serialVersionUID = -884237149535296574L;
	
	private String book;

	public MerchandiseRequest(String book)
	{
		super(TNCSConfig.MERCHANDISE_REQUEST);
		this.book = book;
	}

	public String getBook()
	{
		return this.book;
	}
}
