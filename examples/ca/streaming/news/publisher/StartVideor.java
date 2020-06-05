package ca.streaming.news.publisher;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.dip.streaming.StreamConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.FileManager;

import ca.streaming.news.message.Post;
import ca.streaming.news.message.VideoPieceNotification;

// Created: 04/02/2020, Bing Li
class StartVideor
{
	public static void main(String[] args) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		Scanner in = new Scanner(System.in);
		System.out.println("What is your name as a publisher?");
		String publisherName = in.nextLine();
		Publisher.CLIENT().init(publisherName);
//		String category = PublisherOptions.VIDEOR;
		System.out.println("What is your category?");
		String category = in.nextLine();
		Publisher.CLIENT().addStream(category);
		
		long size = FileManager.getFileSize(NewsConfig.FILE_PATH);
		int startIndex = 0;
		int endIndex = startIndex + NewsConfig.PIECE_SIZE - 1;
		byte[] bytes;
		int index = 0;
		while (endIndex < size)
		{
			bytes = FileManager.loadFile(NewsConfig.FILE_PATH, startIndex, endIndex);
			Publisher.CLIENT().publishStream(new VideoPieceNotification(new Post(publisherName, category, NewsConfig.PIECE_HEAD + index), bytes));
			System.out.println(index++ + ") " + bytes.length + " bytes are transmitted!");
			startIndex = endIndex + 1;
			endIndex = startIndex + NewsConfig.PIECE_SIZE - 1;
			Thread.sleep(StreamConfig.TIMEOUT);
		}
		
		bytes = FileManager.loadFile(NewsConfig.FILE_PATH, startIndex, (int)(size - 1));
		Publisher.CLIENT().publishStream(new VideoPieceNotification(new Post(publisherName, category, NewsConfig.PIECE_HEAD + index), bytes));
		System.out.println("Video publishing is done! Press enter to quit!");
		Publisher.CLIENT().removeStream(category);
		in.nextLine();
		
		Publisher.CLIENT().dispose();
		in.close();
	}

}
