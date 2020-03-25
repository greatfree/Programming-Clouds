package org.greatfree.multicast;

// Created: 11/04/2018, Bing Li
public class MulticastConfig
{
	// When a multicast request is performed and responses are received, it is possible that the RP has not got ready to wait before the signal is executed. So it is necessary to wait for a while to signal again. The case does not happen frequently, especially on a networking environment. When testing that on a standalone computer, I notice the issue. The current solution to wait and signal again does not affect the quality of the system. 11/04/2018, Bing Li
//	public final static long RP_SIGNAL_PERIOD = 1000;
//	public final static long RP_SIGNAL_PERIOD = 10000;
//	public final static long RP_SIGNAL_PERIOD = 10;
	public final static long RP_SIGNAL_TIMEOUT = 1;

}
