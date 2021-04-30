package edu.chainnet.s3.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

// Created: 07/09/2020, Bing Li
public class WriteResponse extends ServerMessage
{
	private static final long serialVersionUID = 5195733517628756236L;
	
	private boolean isSucceeded;
	private IPAddress edsaRootIP;
	private IPAddress ssRootIP;
	private int n;

	public WriteResponse(IPAddress edsaIP, IPAddress ssRootIP, int n)
	{
		super(S3AppID.WRITE_RESPONSE);
		this.edsaRootIP = edsaIP;
		this.ssRootIP = ssRootIP;
		this.n = n;
		this.isSucceeded = true;
	}

	public WriteResponse()
	{
		super(S3AppID.WRITE_RESPONSE);
		this.edsaRootIP = UtilConfig.NO_IP_ADDRESS;
		this.ssRootIP = UtilConfig.NO_IP_ADDRESS;
		this.isSucceeded = false;
	}
	
	public IPAddress getEDSAIP()
	{
		return this.edsaRootIP;
	}
	
	public IPAddress getSSRootIP()
	{
		return this.ssRootIP;
	}
	
	public int getN()
	{
		return this.n;
	}

	public boolean isSucceeded()
	{
		return this.isSucceeded;
	}
}
