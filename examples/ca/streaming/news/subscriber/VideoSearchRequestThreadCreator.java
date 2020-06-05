package ca.streaming.news.subscriber;

import org.greatfree.concurrency.reactive.RequestThreadCreatable;

import ca.streaming.news.message.VideoSearchRequest;
import ca.streaming.news.message.VideoSearchResponse;
import ca.streaming.news.message.VideoSearchStream;

// Created: 04/05/2020, Bing Li
class VideoSearchRequestThreadCreator implements RequestThreadCreatable<VideoSearchRequest, VideoSearchStream, VideoSearchResponse, VideoSearchRequestThread>
{

	@Override
	public VideoSearchRequestThread createRequestThreadInstance(int taskSize)
	{
		return new VideoSearchRequestThread(taskSize);
	}

}
