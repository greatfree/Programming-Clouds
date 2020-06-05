package ca.streaming.news.message;

import org.greatfree.message.ServerMessage;

// Created: 04/05/2020, Bing Li
public class IsVideoExistedResponse extends ServerMessage
{
	private static final long serialVersionUID = -997579375075025994L;
	
	private boolean isExisted;

	public IsVideoExistedResponse(boolean isExisted)
	{
		super(NewsDataType.IS_VIDEO_EXISTED_RESPONSE);
		this.isExisted = isExisted;
	}

	public boolean isExisted()
	{
		return this.isExisted;
	}
}
