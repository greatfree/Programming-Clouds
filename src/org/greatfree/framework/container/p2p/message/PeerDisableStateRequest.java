package org.greatfree.framework.container.p2p.message;

import org.greatfree.message.SystemMessageType;
import org.greatfree.message.container.Request;

/**
 * 
 * @author libing
 * 
 * 03/02/2023
 *
 */
public class PeerDisableStateRequest extends Request
{
	private static final long serialVersionUID = -681412815032305859L;

	private String peerID;

	public PeerDisableStateRequest(String peerID)
	{
		super(SystemMessageType.PEER_DISABLE_STATE_REQUEST);
		this.peerID = peerID;
	}

	public String getPeerID()
	{
		return this.peerID;
	}
}
