package ca.streaming.news.message;

import org.greatfree.message.ServerMessage;

// Created: 04/03/2020, Bing Li
public class VideoSearchRequest extends ServerMessage
{
	private static final long serialVersionUID = 6852221999866119912L;
	
	private String query;
	private int index;

	public VideoSearchRequest(String query, int index)
	{
		super(NewsDataType.VIDEO_SEARCH_REQUEST);
		this.query = query;
		this.index = index;
	}

	public String getQuery()
	{
		return this.query;
	}
	
	public int getIndex()
	{
		return this.index;
	}
}
