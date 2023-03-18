package org.greatfree.exceptions;

/**
 * 
 * @author libing
 * 
 * 01/01/2023
 *
 */
public class DuplicatePeerNameException extends Exception
{
	private static final long serialVersionUID = -5674501448476308419L;
	
	private String peerName;
	
	public DuplicatePeerNameException(String peerName)
	{
		this.peerName = peerName;
	}

	public String getPeerName()
	{
		return this.peerName;
	}
	
	public String toString()
	{
		return this.peerName + " is registered duplicately!";
	}
}
