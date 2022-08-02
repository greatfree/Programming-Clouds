package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.Request;

/**
 * 
 * @author libing
 * 
 * 08/01/2022
 *
 */
class Query extends Request
{
	private String query;
	
	public Query()
	{
	}
	
	public Query(String query)
	{
		this.query = query;
	}

	public String getQuery()
	{
		return this.query;
	}
	
	public void setQuery(String query)
	{
		this.query = query;
	}
}
