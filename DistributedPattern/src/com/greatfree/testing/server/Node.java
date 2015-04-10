package com.greatfree.testing.server;

/*
 * The class keeps the information to define a sign-up or sign-in client. It is usually different from NoteEntity, which consists of data for a node only without relevant manipulations. For the testing case, they are almost the same since the scenarios are simple. In practice, it is recommended to differentiate them like the samples. 11/03/2014, Bing Li
 */

// Created: 11/03/2014, Bing Li
public class Node
{
	private String key;
	private String username;
	private String password;

	/*
	 * Initialize. 11/03/2014, Bing Li
	 */
	public Node(String key, String username, String password)
	{
		this.key = key;
		this.username = username;
		this.password = password;
	}

	/*
	 * Expose the key. 11/03/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}

	/*
	 * Expose the username. 11/03/2014, Bing Li
	 */
	public String getUsername()
	{
		return this.username;
	}

	/*
	 * Expose the password. 11/03/2014, Bing Li
	 */
	public String getPassword()
	{
		return this.password;
	}
}
