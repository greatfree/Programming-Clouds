package org.greatfree.dsf.streaming.publisher;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.dsf.streaming.StreamConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.FileManager;
import org.greatfree.util.Rand;
import org.greatfree.util.UtilConfig;

// Created: 03/20/2020, Bing Li
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
		String topic;
		String publishTimes;
		int times;
		do
		{
			System.out.println("What is your topic?");
			topic = in.nextLine();
			Publisher.CLIENT().addStream(topic);
			System.out.println("How many posts?");
			publishTimes = in.nextLine();
			times = Integer.valueOf(publishTimes);

			for (int i = 0; i < times; i++)
			{
				data = texts.get(Rand.getRandom(texts.size()));
				System.out.println("Published data: " + data);
				Publisher.CLIENT().publishStream(topic, data);
				Thread.sleep(StreamConfig.TIMEOUT);
			}
			System.out.println("===========================");
			Publisher.CLIENT().removeStream(topic);
			System.out.println("Post done! Continue? (y/q)");
			option = in.nextLine();
		}
		while (!option.equals("q"));

		Publisher.CLIENT().dispose();
		in.close();
	}
}
