package org.greatfree.app.container.cs.multinode.library.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Created: 12/19/2018, Bing Li
public class LibraryDB
{
	private Map<String, String> books;
	
	private LibraryDB()
	{
		this.books = new ConcurrentHashMap<String, String>();
	}
	
	private static LibraryDB instance = new LibraryDB();
	
	public static LibraryDB CS()
	{
		if (instance == null)
		{
			instance = new LibraryDB();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void save(String bookTitle, String author)
	{
		this.books.put(bookTitle, author);
	}
	
	public String get(String bookTitle)
	{
		return this.books.get(bookTitle);
	}

}
