package org.greatfree.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import org.greatfree.server.MessageStream;

/**
 * 
 * @author libing
 * 
 * 10/11/2022
 *
 */
public class LeaveStream extends MessageStream<LeaveRequest>
{
	public LeaveStream(ObjectOutputStream out, Lock lock, LeaveRequest message)
	{
		super(out, lock, message);
	}
}
