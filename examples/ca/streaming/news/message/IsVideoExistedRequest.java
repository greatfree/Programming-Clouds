package ca.streaming.news.message;

import org.greatfree.message.ServerMessage;

// Created: 04/05/2020, Bing Li
public class IsVideoExistedRequest extends ServerMessage
{
	private static final long serialVersionUID = -5617081997468404286L;
	
	private String keyword;

	public IsVideoExistedRequest(String keyword)
	{
		super(NewsDataType.IS_VIDEO_EXISTED_REQUEST);
		this.keyword = keyword;
	}
	
	public String getKeyword()
	{
		return this.keyword;
	}

}
