package ca.streaming.news.publisher;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.dip.streaming.StreamConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.FileManager;
import org.greatfree.util.Rand;
import org.greatfree.util.UtilConfig;

import ca.streaming.news.message.CommentNotification;
import ca.streaming.news.message.JournalistPostNotification;
import ca.streaming.news.message.MicroblogNotification;
import ca.streaming.news.message.NewsFeedNotification;
import ca.streaming.news.message.Post;

// Created: 04/02/2020, Bing Li
class StartPublisher
{
	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		Scanner in = new Scanner(System.in);
		System.out.println("What is your name as a publisher?");
		String publisherName = in.nextLine();
		Publisher.CLIENT().init(publisherName);

		List<String> texts = FileManager.readText(StreamConfig.TEST_DATA);

		String data;
		String option = UtilConfig.EMPTY_STRING;
		String category;
		String publishTimes;
		int times;
		do
		{
			System.out.println("What is your category?");
			category = in.nextLine();
			Publisher.CLIENT().addStream(category);
			System.out.println("How many posts?");
			publishTimes = in.nextLine();
			times = Integer.valueOf(publishTimes);

			for (int i = 0; i < times; i++)
			{
				data = texts.get(Rand.getRandom(texts.size()));
				System.out.println("Published data: " + data);
				switch (publisherName)
				{
					case PublisherOptions.JOURNALIST:
						Publisher.CLIENT().publishStream(new JournalistPostNotification(new Post(publisherName, category, data)));
						break;
						
					case PublisherOptions.COMMENTER:
						Publisher.CLIENT().publishStream(new CommentNotification(new Post(publisherName, category, data)));
						break;
						
					case PublisherOptions.MICROBLOGGER:
						Publisher.CLIENT().publishStream(new MicroblogNotification(new Post(publisherName, category, data)));
						break;
						
					case PublisherOptions.NEWS_FEEDER:
						Publisher.CLIENT().publishStream(new NewsFeedNotification(new Post(publisherName, category, data)));
						break;
				}
				Thread.sleep(StreamConfig.TIMEOUT);
			}
			System.out.println("===========================");
			Publisher.CLIENT().removeStream(category);
			System.out.println("Post done! Continue? (y/q)");
			option = in.nextLine();
		}
		while (!option.equals("q"));

		Publisher.CLIENT().dispose();
		in.close();
	}
}
