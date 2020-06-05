package ca.streaming.news.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.client.OutMessageStream;

// Created: 04/03/2020, Bing Li
public class VideoSearchStream extends OutMessageStream<VideoSearchRequest>
{

	public VideoSearchStream(ObjectOutputStream out, Lock lock, VideoSearchRequest message)
	{
		super(out, lock, message);
	}

}
