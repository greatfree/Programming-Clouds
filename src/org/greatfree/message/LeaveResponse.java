package org.greatfree.message;

/**
 * 
 * @author libing
 * 
 * 10/11/2022
 *
 */
public class LeaveResponse extends ServerMessage
{
	private static final long serialVersionUID = 5057615539192018920L;

	public LeaveResponse()
	{
		super(SystemMessageType.LEAVE_RESPONSE);
	}
}
