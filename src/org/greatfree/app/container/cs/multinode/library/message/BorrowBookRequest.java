package org.greatfree.app.container.cs.multinode.library.message;

import org.greatfree.message.container.Request;

// Created: 12/19/2018, Bing Li
public class BorrowBookRequest extends Request
{
	private static final long serialVersionUID = 6862072113206987103L;
	
	private String bookTitle;

	public BorrowBookRequest(String bookTitle)
	{
//		super(CSMessageType.REQUEST, LibraryID.BORROW_BOOK_REQUEST);
		super(LibraryID.BORROW_BOOK_REQUEST);
		this.bookTitle = bookTitle;
	}

	public String getBookTitle()
	{
		return this.bookTitle;
	}
}
