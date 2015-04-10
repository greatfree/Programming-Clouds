package com.greatfree.testing.db;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;

/*
 * This is an example to create an object that can be persisted in the object-oriented database, the Berkeley DB. 09/26/2014, Bing Li
 */

// Created: 09/26/2014, Bing Li
@Entity
public class NodeEntity
{
	// The unique key that distinguishes from others. For an object to be persisted, it is required to design such a key. 09/26/2014, Bing Li
	@PrimaryKey
	private String key;

	// A field, userName, to be saved. 09/26/2014, Bing Li
	private String username;
	// A field, password, to be saved. 09/26/2014, Bing Li
	private String password;

	/*
	 * The constructor is required by the database, the Berkeley DB. 09/26/2014, Bing Li
	 */
	public NodeEntity()
	{
	}

	/*
	 * An constructor initializes the class to set values for all of the properties. 10/03/2014, Bing Li
	 */
	public NodeEntity(String key, String username, String password)
	{
		this.key = key;
		this.username = username;
		this.password = password;
	}

	/*
	 * The setter for the attribute of key. It is required by the Berkeley DB. 10/03/2014, Bing Li
	 */
	public void setKey(String key)
	{
		this.key = key;
	}
	
	/*
	 * The getter for the attribute of key. It is required by the Berkeley DB. 10/03/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}
	
	/*
	 * The setter for the attribute of userName. It is required by the Berkeley DB. 10/03/2014, Bing Li
	 */
	public void setUserName(String username)
	{
		this.username = username;
	}
	
	/*
	 * The getter for the attribute of userName. It is required by the Berkeley DB. 10/03/2014, Bing Li
	 */
	public String getUserName()
	{
		return this.username;
	}
	
	/*
	 * The setter for the attribute of password. It is required by the Berkeley DB. 10/03/2014, Bing Li
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	/*
	 * The getter for the attribute of password. It is required by the Berkeley DB. 10/03/2014, Bing Li
	 */
	public String getPassword()
	{
		return this.password;
	}
}
