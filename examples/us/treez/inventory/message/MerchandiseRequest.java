package us.treez.inventory.message;

import org.greatfree.message.container.Request;

// Created: 02/05/2020, Bing Li
public class MerchandiseRequest extends Request
{
	private static final long serialVersionUID = -6683819852830745537L;
	
	private String merchandiseKey;

	public MerchandiseRequest(String merchandiseKey)
	{
		super(BusinessAppID.MERCHANDISE_REQUEST);
		this.merchandiseKey = merchandiseKey;
	}

	public String getMerchandiseKey()
	{
		return this.merchandiseKey;
	}
}
