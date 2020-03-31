package ca.dp.tncs.message;

import java.io.Serializable;

// Created: 02/22/2020, Bing Li
public class Book implements Serializable
{
	private static final long serialVersionUID = 2546630133107172036L;
	
	private String title;
	private String author;
	private float price;
	
	public Book(String title, String author, float price)
	{
		this.title = title;
		this.author = author;
		this.price = price;
	}

	public String getTitle()
	{
		return this.title;
	}
	
	public String getAuthor()
	{
		return this.author;
	}
	
	public float getPrice()
	{
		return this.price;
	}
	
	public String toString()
	{
		return this.title + ", " + this.author + ", " + this.price;
	}
}
