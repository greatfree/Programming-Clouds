package edu.greatfree.notification.message;

import java.io.Serializable;

// Created: 04/08/2019, Bing Li
public class Book implements Serializable
{
	private static final long serialVersionUID = 1614991368174399523L;
	
	private String title;
	private String author;
	
	public Book(String title, String author)
	{
		this.title = title;
		this.author = author;
	}
	
	public String getTitle()
	{
		return this.title;
	}
	
	public String getAuthor()
	{
		return this.author;
	}
}
