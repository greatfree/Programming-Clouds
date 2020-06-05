package ca.streaming.news.message;

import org.greatfree.message.ServerMessage;

// Created: 04/03/2020, Bing Li
public class VideoSearchResponse extends ServerMessage
{
	private static final long serialVersionUID = 8760506956627220173L;
	
	private byte[] videoPiece;

	public VideoSearchResponse(byte[] vp)
	{
		super(NewsDataType.VIDEO_SEARCH_RESPONSE);
		this.videoPiece = vp;
	}

	public byte[] getVideoPiece()
	{
		return this.videoPiece;
	}
}
