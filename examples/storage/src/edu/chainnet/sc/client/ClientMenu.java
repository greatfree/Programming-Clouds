package edu.chainnet.sc.client;

// Created: 10/19/2020, Bing Li
public class ClientMenu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	
	public final static String REGISTER = TAB + "1) Register";
	public final static String RETRIEVE = TAB + "2) Retrieve";
	public final static String UPDATE = TAB + "3) Synchronize";

	public final static String QUIT = ClientMenu.TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	public final static String WRONG_OPTION = "Wrong option!";
}
