package ca.streaming.news.message;

import org.greatfree.message.multicast.MulticastMessage;

// Created: 03/31/2020, Bing Li
public class VideoPieceNotification extends MulticastMessage
{
	private static final long serialVersionUID = 5762268114325649698L;
	
	private Post post;
	private byte[] videoPiece;

	public VideoPieceNotification(Post post, byte[] videoPiece)
	{
		super(NewsDataType.VIDEO_PIECE_NOTIFICATION);
		this.post = post;
		this.videoPiece = videoPiece;
	}

	public Post getPost()
	{
		return this.post;
	}
	
	public byte[] getVideoPiece()
	{
		return this.videoPiece;
	}
}
