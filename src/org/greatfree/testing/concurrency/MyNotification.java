package org.greatfree.testing.concurrency;

// Created: 09/10/2018, Bing Li
class MyNotification
{
	private String firstName;
	private String lastName;
	
	public MyNotification(String firstName, String lastName)
	{
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public String getFirstName()
	{
		return this.firstName;
	}
	
	public String getLastName()
	{
		return this.lastName;
	}

}
