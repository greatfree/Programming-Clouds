package edu.chainnet.center.message;

import java.util.List;

import org.greatfree.message.multicast.MulticastResponse;

import edu.chainnet.center.PageIndex;

// Created: 04/29/2021, Bing Li
public class SearchResponse extends MulticastResponse
{
	private static final long serialVersionUID = 8678131153801329511L;
	
	private List<PageIndex> pages;

	public SearchResponse(List<PageIndex> pages, String collaboratorKey)
	{
		super(CenterApplicationID.SEARCH_RESPONSE, collaboratorKey);
		this.pages = pages;
	}

	public List<PageIndex> getPages()
	{
		return this.pages;
	}
}
