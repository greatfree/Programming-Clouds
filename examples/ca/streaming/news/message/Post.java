package ca.streaming.news.message;

import java.io.Serializable;

import org.greatfree.util.UtilConfig;

// Created: 03/31/2020, Bing Li
public class Post implements Serializable
{
	private static final long serialVersionUID = -9087709365816979079L;
	
	private String publisher;
	private String category;
	private String message;
	
	public Post(String publisher, String category, String message)
	{
		this.publisher = publisher;
		this.category = category;
		this.message = message;
	}

	public String getPublisher()
	{
		return this.publisher;
	}
	
	public String getCategory()
	{
		return this.category;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public String toString()
	{
		return this.publisher + UtilConfig.COMMA + this.category + UtilConfig.COMMA + this.message;
	}
}
