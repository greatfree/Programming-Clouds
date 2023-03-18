package org.greatfree.exceptions;

/**
 * 
 * @author libing
 * 
 * 01/11/2023
 *
 */
public class ServerPortConflictedException extends Exception
{
	private static final long serialVersionUID = -352235427320551002L;
	
	private int port;
	
	public ServerPortConflictedException(int port)
	{
		this.port = port;
	}
	public int getPort()
	{
		return this.port;
	}
	
	public String toString()
	{
		return this.port + " conflicts with others!";
	}
}
