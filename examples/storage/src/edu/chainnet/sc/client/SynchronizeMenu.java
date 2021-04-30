package edu.chainnet.sc.client;

import org.greatfree.chat.ClientMenu;

// Created: 10/20/2020, Bing Li
public class SynchronizeMenu
{
	public final static String TAB = "	";
	public final static String MENU_HEAD = "\n========== Menu Head ===========";
	
	public final static String SYNCHRONIZE_BLOCK_CHAIN_NODE_REGISTRY = "1) Synchronize the block-chain node registry";
	public final static String SYNCHRONIZE_DATA_LAKE_NODE_REGISTRY = "2) Synchronize the data lake node registry";
	public final static String SYNCHRONIZE_SMART_CONTRACT_NODE_REGISTRY = "3) Synchronize the smart contract node registry";
	public final static String SYNCHRONIZE_ORACLE_NODE_REGISTRY = "4) Synchronize the oracle node registry";
	
	public final static String QUIT = ClientMenu.TAB + "0) Quit";
	public final static String MENU_TAIL = "========== Menu Tail ===========\n";
	public final static String INPUT_PROMPT = "Input an option:";
	
	public final static String WRONG_OPTION = "Wrong option!";
}
