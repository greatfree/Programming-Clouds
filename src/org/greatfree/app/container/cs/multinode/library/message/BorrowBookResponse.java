package org.greatfree.app.container.cs.multinode.library.message;

import org.greatfree.message.ServerMessage;

// Created: 12/19/2018, Bing Li
public class BorrowBookResponse extends ServerMessage
{
	private static final long serialVersionUID = 6832743057667951080L;
	
	private String bookTitle;
	private String author;

	public BorrowBookResponse(String bookTitle, String author)
	{
		super(LibraryID.BORROW_BOOK_RESPONSE);
		this.bookTitle = bookTitle;
		this.author = author;
	}

	public String getBookTitle()
	{
		return this.bookTitle;
	}
	
	public String getAuthor()
	{
		return this.author;
	}
}
