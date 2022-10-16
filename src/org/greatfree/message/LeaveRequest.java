package org.greatfree.message;

/**
 * 
 * The message is sent from a client to a server when the client intends to shutdown. 10/11/2022, Bing Li
 * 
 * The message is designed to keep leaving delaying for some time and avoid possible data loss when disconnecting the connection between a client and a server. 10/11/2022, Bing Li
 * 
 * @author libing
 * 
 * 10/11/2022
 *
 */
public class LeaveRequest extends ServerMessage
{
	private static final long serialVersionUID = -5353332275024619378L;

	public LeaveRequest()
	{
		super(SystemMessageType.LEAVE_REQUEST);
	}
}
