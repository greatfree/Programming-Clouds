package org.greatfree.concurrency.threading.message;

// Created: 09/10/2019, Bing Li
final public class ATMMessageType
{
	public final static int INSTRUCT_NOTIFICATION = 45;

	public final static int ATM_THREAD_REQUEST = 46;
	public final static int ATM_THEAD_RESPONSE = 47;

	public final static int TASK_STATE_NOTIFICATION = 48;
	
	// The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
//	public final static int TIMEOUT_NOTIFICATION = 49;
	// The method is not necessary since the thread is signaled when new messages are received. 09/18/2019, Bing Li
//	public final static int SIGNAL_NOTIFICATION = 50;
	public final static int EXECUTE_NOTIFICATION = 51;
	public final static int KILL_NOTIFICATION = 52;
	// The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
//	public final static int WAIT_NOTIFICATION = 53;
	
	public final static int IS_ALIVE_REQUEST = 54;
	public final static int IS_ALIVE_RESPONSE = 55;
	
	public final static int SHUTDOWN_SLAVE_NOTIFICATION = 56;
	
	public final static int TASK_NOTIFICATION = 57;
	public final static int KILL_ALL_NOTIFICATION = 58;
	public final static int IS_TASK_QUEUE_EMPTY = 59;
	
	public final static int SYNC_INSTRUCT_NOTIFICATION = 60;
	
	public final static int ALL_SLAVES_NOTIFICATION = 61;
	public final static int ALL_SLAVE_IPS_NOTIFICATION = 62;
	
	public final static int TASK_REQUEST = 63;
	public final static int TASK_RESPONSE = 64;

	public final static int TASK_INVOKE_NOTIFICATION = 65;
	
	public final static int TASK_INVOKE_REQUEST = 66;
	public final static int TASK_INVOKE_RESPONSE = 67;
	
	public final static int INTERACT_NOTIFICATION = 68;

	public final static int INTERACT_REQUEST = 69;
	public final static int INTERACT_RESPONSE = 70;
	
	public final static int MASTER_NOTIFICATION = 71;
}
