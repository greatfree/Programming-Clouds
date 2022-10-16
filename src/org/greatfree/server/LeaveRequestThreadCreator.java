package org.greatfree.server;

import org.greatfree.concurrency.reactive.RequestQueueCreator;
import org.greatfree.message.LeaveRequest;
import org.greatfree.message.LeaveResponse;
import org.greatfree.message.LeaveStream;

/**
 * 
 * @author libing
 * 
 * 10/11/2022
 *
 */
final class LeaveRequestThreadCreator implements RequestQueueCreator<LeaveRequest, LeaveStream, LeaveResponse, LeaveRequestThread>
{
	@Override
	public LeaveRequestThread createInstance(int taskSize)
	{
		return new LeaveRequestThread(taskSize);
	}
}
