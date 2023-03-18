package org.greatfree.exceptions;

/**
 * 
 * @author libing
 * 
 * 01/11/2023
 *
 */
public class RemoteIPNotExistedException extends Exception
{
	private static final long serialVersionUID = -6112924488025519157L;
	
	private String ip;
	private int port;
	
	public RemoteIPNotExistedException(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
	}

	public String getIP()
	{
		return this.ip;
	}
	
	public int getPort()
	{
		return this.port;
	}
	
	public String toString()
	{
		return this.ip + ":" + this.port + " does not exist!";
	}
}
