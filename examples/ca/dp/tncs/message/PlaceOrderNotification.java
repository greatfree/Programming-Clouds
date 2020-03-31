package ca.dp.tncs.message;

import org.greatfree.message.ServerMessage;

// Created: 02/22/2020, Bing Li
public class PlaceOrderNotification extends ServerMessage
{
	private static final long serialVersionUID = 8948879506759210980L;
	
	private String clientName;
	private String bookName;
	private int bookCount;
	private float payment;

	public PlaceOrderNotification(String cn, String bn, int bc, float payment)
	{
		super(TNCSConfig.PLACE_ORDER_NOTIFICATION);
		this.clientName = cn;
		this.bookName = bn;
		this.bookCount = bc;
		this.payment = payment;
	}

	public String getClientName()
	{
		return this.clientName;
	}
	
	public String getBookName()
	{
		return this.bookName;
	}
	
	public int getBookCount()
	{
		return this.bookCount;
	}
	
	public float getPayment()
	{
		return this.payment;
	}
}
