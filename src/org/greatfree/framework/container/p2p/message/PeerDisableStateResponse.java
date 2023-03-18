package org.greatfree.framework.container.p2p.message;

import org.greatfree.message.ServerMessage;
import org.greatfree.message.SystemMessageType;

/**
 * 
 * @author libing
 * 
 * 03/02/2023
 *
 */
public class PeerDisableStateResponse extends ServerMessage
{
	private static final long serialVersionUID = 8240949263907552791L;
	
	private boolean isServerDisabled;
//	private boolean isClientDisabled;

//	public PeerDisableStateResponse(boolean isSD, boolean isCD)
	public PeerDisableStateResponse(boolean isSD)
	{
		super(SystemMessageType.PEER_DISABLE_STATE_RESPONSE);
		this.isServerDisabled = isSD;
//		this.isClientDisabled = isCD;
	}

	public boolean isServerDisabled()
	{
		return this.isServerDisabled;
	}

	/*
	public boolean isClientDisabled()
	{
		return this.isClientDisabled;
	}
	*/
}
