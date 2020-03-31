package ca.dp.tncs.client;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.exceptions.RemoteReadException;

import ca.dp.tncs.message.Book;

// Created: 02/22/2020, Bing Li
class StartClient
{

	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		BusinessReader.init();
		BusinessEventer.RE().init();
		
		Book b = BusinessReader.getBook("Java");
		System.out.println(b.getAuthor() + ", " + b.getTitle() + ", " + b.getPrice());
		BusinessEventer.RE().notify("Smith", "Java", 2, 5.00f);
		System.out.println("Order is placed");
		
		Scanner in = new Scanner(System.in);
		System.out.println("Press enter to exit ...");
		in.nextLine();

		BusinessReader.shutdown();
		BusinessEventer.RE().dispose(1000);
		in.close();
	}

}
