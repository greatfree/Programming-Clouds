package edu.chainnet.sc.client;

import org.greatfree.chat.ClientMenu;

// Created: 10/20/2020, Bing Li
public class RegisterMenu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";

	public final static String REGISTER_BLOCK_CHAIN_NODE = TAB + "1) Register the block-chain node";
	public final static String REGISTER_SMART_CONTRACT_NODE = TAB + "2) Register the smart contract node";
	public final static String REGISTER_ORACLE_NODE = TAB + "3) Register the oracle node";
	public final static String REGISTER_DATA_LAKE_NODE = TAB + "4) Register the data lake node";
	
	public final static String QUIT = ClientMenu.TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	
	public final static String WRONG_OPTION = "Wrong option!";
}
