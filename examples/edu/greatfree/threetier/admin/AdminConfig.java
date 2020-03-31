package edu.greatfree.threetier.admin;

// Created: 05/07/2019, Bing Li
public class AdminConfig
{
	public final static int STOP_COORDINATOR = 1;
	public final static int STOP_TERMINAL = 2;

	public final static int NO_OPTION = -1;
	public final static int END = 0;
	
	public final static String COORDINATOR_ADDRESS = "127.0.0.1";
	public final static int COORDINATOR_PORT = 8944;
	public final static int COORDINATOR_ADMIN_PORT = 8941;

	public final static String TERMINAL_ADDRESS = "127.0.0.1";
	public final static int TERMINAL_PORT = 8943;
	public final static int TERMINAL_ADMIN_PORT = 8942;
}
