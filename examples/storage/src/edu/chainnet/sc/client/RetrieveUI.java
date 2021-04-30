package edu.chainnet.sc.client;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.exceptions.RemoteReadException;

import edu.chainnet.sc.collaborator.child.db.DBConfig;
import edu.chainnet.sc.message.BCNode;
import edu.chainnet.sc.message.DSNode;

// Created: 10/20/2020, Bing Li
class RetrieveUI
{
	private Scanner in = new Scanner(System.in);

	private RetrieveUI()
	{
	}

	private static RetrieveUI instance = new RetrieveUI();
	
	public static RetrieveUI BC()
	{
		if (instance == null)
		{
			instance = new RetrieveUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.in.close();
	}

	public void printMenu()
	{
		System.out.println(RetrieveMenu.MENU_HEAD);
		
		System.out.println(RetrieveMenu.RETRIEVE_BC_NODE_UPON_ID);
		System.out.println(RetrieveMenu.RETRIEVE_BC_NODES_UPON_SERVICE);
		System.out.println(RetrieveMenu.RETRIEVE_BC_NODES_UPON_DESCRIPTION);
		
		System.out.println(RetrieveMenu.RETRIEVE_DL_NODE_UPON_ID);
		System.out.println(RetrieveMenu.RETRIEVE_DL_NODES_UPON_SERVICE);
		System.out.println(RetrieveMenu.RETRIEVE_DL_NODES_UPON_DESCRIPTION);
		
		System.out.println(RetrieveMenu.RETRIEVE_ON_NODE_UPON_ID);
		System.out.println(RetrieveMenu.RETRIEVE_ON_NODES_UPON_SERVICE);
		System.out.println(RetrieveMenu.RETRIEVE_ON_NODES_UPON_DESCRIPTION);
		
		System.out.println(RetrieveMenu.RETRIEVE_SC_NODE_UPON_ID);
		System.out.println(RetrieveMenu.RETRIEVE_SC_NODES_UPON_SERVICE);
		System.out.println(RetrieveMenu.RETRIEVE_SC_NODES_UPON_DESCRIPTION);
		
		System.out.println(RetrieveMenu.RETRIEVE_BC_NODES_UPON_VERSION);
		System.out.println(RetrieveMenu.RETRIEVE_DL_NODES_UPON_VERSION);
		System.out.println(RetrieveMenu.RETRIEVE_ON_NODES_UPON_VERSION);
		System.out.println(RetrieveMenu.RETRIEVE_SC_NODES_UPON_VERSION);
		
		System.out.println(RetrieveMenu.QUIT);
		System.out.println(RetrieveMenu.MENU_TAIL);
		System.out.println(RetrieveMenu.INPUT_PROMPT);
	}
	
	public void send(int option) throws ClassNotFoundException, RemoteReadException, IOException
	{
		BCNode bcNode;
		DSNode dsNode;
		List<BCNode> bcNodes;
		List<DSNode> dsNodes;
		switch (option)
		{
			case RetrieveOptions.RETRIEVE_BC_NODE_UPON_ID:
				bcNode = BCClient.BC().getBCNodeUponID();
				if (bcNode != null)
				{
					System.out.println("Retrieved BC node: " + bcNode);
				}
				else
				{
					System.out.println("No BC node retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_BC_NODES_UPON_SERVICE:
				bcNodes = BCClient.BC().getBCNodesUponService();
				if (bcNodes.size() > 0)
				{
					System.out.println("Retrieved BC nodes: ");
					for (BCNode entry : bcNodes)
					{
						System.out.println(entry);
					}
				}
				else
				{
					System.out.println("No BC nodes retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_BC_NODES_UPON_DESCRIPTION:
				bcNodes = BCClient.BC().getBCNodesUponDescription();
				if (bcNodes.size() > 0)
				{
					System.out.println("Retrieved BC nodes: ");
					for (BCNode entry : bcNodes)
					{
						System.out.println(entry);
					}
				}
				else
				{
					System.out.println("No BC nodes retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_DL_NODE_UPON_ID:
				dsNode = BCClient.BC().getDLNodeUponID();
				if (dsNode != null)
				{
					System.out.println("Retrieved DL node: " + dsNode);
				}
				else
				{
					System.out.println("No DL node retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_DL_NODES_UPON_SERVICE:
				dsNodes = BCClient.BC().getDLNodesUponService();
				if (dsNodes.size() > 0)
				{
					System.out.println("Retrieved DL nodes: ");
					for (DSNode entry : dsNodes)
					{
						System.out.println(entry);
					}
				}
				else
				{
					System.out.println("No DL nodes retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_DL_NODES_UPON_DESCRIPTION:
				dsNodes = BCClient.BC().getDLNodesUponDescription();
				if (dsNodes.size() > 0)
				{
					System.out.println("Retrieved DL nodes: ");
					for (DSNode entry : dsNodes)
					{
						System.out.println(entry);
					}
				}
				else
				{
					System.out.println("No DL nodes retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_ON_NODE_UPON_ID:
				dsNode = BCClient.BC().getONNodeUponID();
				if (dsNode != null)
				{
					System.out.println("Retrieved ON node: " + dsNode);
				}
				else
				{
					System.out.println("No ON node retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_ON_NODES_UPON_SERVICE:
				dsNodes = BCClient.BC().getONNodesUponService();
				if (dsNodes.size() > 0)
				{
					System.out.println("Retrieved ON nodes: ");
					for (DSNode entry : dsNodes)
					{
						System.out.println(entry);
					}
				}
				else
				{
					System.out.println("No ON nodes retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_ON_NODES_UPON_DESCRIPTION:
				dsNodes = BCClient.BC().getONNodesUponDescription();
				if (dsNodes.size() > 0)
				{
					System.out.println("Retrieved ON nodes: ");
					for (DSNode entry : dsNodes)
					{
						System.out.println(entry);
					}
				}
				else
				{
					System.out.println("No ON nodes retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_SC_NODE_UPON_ID:
				dsNode = BCClient.BC().getSCNodeUponID();
				if (dsNode != null)
				{
					System.out.println("Retrieved SC node: " + dsNode);
				}
				else
				{
					System.out.println("No SC node retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_SC_NODES_UPON_SERVICE:
				dsNodes = BCClient.BC().getSCNodesUponService();
				if (dsNodes.size() > 0)
				{
					System.out.println("Retrieved SC nodes: ");
					for (DSNode entry : dsNodes)
					{
						System.out.println(entry);
					}
				}
				else
				{
					System.out.println("No SC nodes retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_SC_NODES_UPON_DESCRIPTION:
				dsNodes = BCClient.BC().getSCNodesUponDescription();
				if (dsNodes.size() > 0)
				{
					System.out.println("Retrieved SC nodes: ");
					for (DSNode entry : dsNodes)
					{
						System.out.println(entry);
					}
				}
				else
				{
					System.out.println("No SC nodes retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_BC_NODES_UPON_VERSION:
				bcNodes = BCClient.BC().getBCNodesUponVersion();
				if (bcNodes.size() > 0)
				{
					System.out.println("Retrieved BC nodes: ");
					for (BCNode entry : bcNodes)
					{
						System.out.println(entry);
					}
				}
				else
				{
					System.out.println("No BC nodes retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_DL_NODES_UPON_VERSION:
				dsNodes = BCClient.BC().getDSNodesUponVersion(DBConfig.DL_NODE);
				if (dsNodes.size() > 0)
				{
					System.out.println("Retrieved DL nodes: ");
					for (DSNode entry : dsNodes)
					{
						System.out.println(entry);
					}
				}
				else
				{
					System.out.println("No DL nodes retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_ON_NODES_UPON_VERSION:
				dsNodes = BCClient.BC().getDSNodesUponVersion(DBConfig.ON_NODE);
				if (dsNodes.size() > 0)
				{
					System.out.println("Retrieved ON nodes: ");
					for (DSNode entry : dsNodes)
					{
						System.out.println(entry);
					}
				}
				else
				{
					System.out.println("No ON nodes retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_SC_NODES_UPON_VERSION:
				dsNodes = BCClient.BC().getDSNodesUponVersion(DBConfig.SC_NODE);
				if (dsNodes.size() > 0)
				{
					System.out.println("Retrieved SC nodes: ");
					for (DSNode entry : dsNodes)
					{
						System.out.println(entry);
					}
				}
				else
				{
					System.out.println("No SC nodes retrieved!");
				}
				break;
		}
	}
}
