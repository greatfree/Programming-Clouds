package org.greatfree.exceptions;

/**
 * 
 * @author libing
 * 
 * 02/12/2023
 *
 */
public class PeerNameIsNullException extends Exception
{
	private static final long serialVersionUID = 8019751788198701944L;
	
	private String ip;
	private int port;

	public PeerNameIsNullException(String ip, int port)
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
		return this.ip + ":" + this.port + " is named as null!";
	}
}
