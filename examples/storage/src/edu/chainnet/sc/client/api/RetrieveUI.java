package edu.chainnet.sc.client.api;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.greatfree.exceptions.RemoteReadException;

import edu.chainnet.sc.client.NodeInfo;
import edu.chainnet.sc.collaborator.child.db.DBConfig;
import edu.chainnet.sc.message.BCNode;
import edu.chainnet.sc.message.DSNode;

// Created: 10/25/2020, Bing Li
class RetrieveUI
{
	private Scanner in = new Scanner(System.in);

	private RetrieveUI()
	{
	}

	private static RetrieveUI instance = new RetrieveUI();
	
	public static RetrieveUI API()
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
		System.out.println(RetrieveMenu.RETRIEVE_BC_NODE_UPON_ID_IN_HISTORY);

		System.out.println(RetrieveMenu.RETRIEVE_BC_NODE_UPON_NAME);
		System.out.println(RetrieveMenu.RETRIEVE_BC_NODE_UPON_NAME_IN_HISTORY);
		
		System.out.println(RetrieveMenu.RETRIEVE_BC_NODES_UPON_SERVICE);
		System.out.println(RetrieveMenu.RETRIEVE_BC_NODES_UPON_SERVICE_IN_HISTORY);

		System.out.println(RetrieveMenu.RETRIEVE_BC_NODES_UPON_DESCRIPTION);
		System.out.println(RetrieveMenu.RETRIEVE_BC_NODES_UPON_DESCRIPTION_IN_HISTORY);
		
		System.out.println(RetrieveMenu.RETRIEVE_DL_NODE_UPON_ID);
		System.out.println(RetrieveMenu.RETRIEVE_DL_NODE_UPON_ID_IN_HISTORY);
		
		System.out.println(RetrieveMenu.RETRIEVE_DL_NODE_UPON_NAME);
		System.out.println(RetrieveMenu.RETRIEVE_DL_NODE_UPON_NAME_IN_HISTORY);
		
		System.out.println(RetrieveMenu.RETRIEVE_DL_NODES_UPON_SERVICE);
		System.out.println(RetrieveMenu.RETRIEVE_DL_NODES_UPON_SERVICE_IN_HISTORY);

		System.out.println(RetrieveMenu.RETRIEVE_DL_NODES_UPON_DESCRIPTION);
		System.out.println(RetrieveMenu.RETRIEVE_DL_NODES_UPON_DESCRIPTION_IN_HISTORY);
		
		System.out.println(RetrieveMenu.RETRIEVE_ON_NODE_UPON_ID);
		System.out.println(RetrieveMenu.RETRIEVE_ON_NODE_UPON_ID_IN_HISTORY);
		
		System.out.println(RetrieveMenu.RETRIEVE_ON_NODE_UPON_NAME);
		System.out.println(RetrieveMenu.RETRIEVE_ON_NODE_UPON_NAME_IN_HISTORY);
		
		System.out.println(RetrieveMenu.RETRIEVE_ON_NODES_UPON_SERVICE);
		System.out.println(RetrieveMenu.RETRIEVE_ON_NODES_UPON_SERVICE_IN_HISTORY);

		System.out.println(RetrieveMenu.RETRIEVE_ON_NODES_UPON_DESCRIPTION);
		System.out.println(RetrieveMenu.RETRIEVE_ON_NODES_UPON_DESCRIPTION_IN_HISTORY);
		
		System.out.println(RetrieveMenu.RETRIEVE_SC_NODE_UPON_ID);
		System.out.println(RetrieveMenu.RETRIEVE_SC_NODE_UPON_ID_IN_HISTORY);

		System.out.println(RetrieveMenu.RETRIEVE_SC_NODE_UPON_NAME);
		System.out.println(RetrieveMenu.RETRIEVE_SC_NODE_UPON_NAME_IN_HISTORY);

		System.out.println(RetrieveMenu.RETRIEVE_SC_NODES_UPON_SERVICE);
		System.out.println(RetrieveMenu.RETRIEVE_SC_NODES_UPON_SERVICE_IN_HISTORY);

		System.out.println(RetrieveMenu.RETRIEVE_SC_NODES_UPON_DESCRIPTION);
		System.out.println(RetrieveMenu.RETRIEVE_SC_NODES_UPON_DESCRIPTION_IN_HISTORY);
		
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
				bcNode = CollaboratorClient.CHAIN().retrieveBlockChainNodeByID(NodeInfo.BC().getBCID());
				if (bcNode != null)
				{
					System.out.println("Retrieved BC node: " + bcNode);
				}
				else
				{
					System.out.println("No BC node retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_BC_NODE_UPON_ID_IN_HISTORY:
				bcNodes = CollaboratorClient.CHAIN().retrieveBlockChainNodeByIDInHistory(NodeInfo.BC().getBCID());
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

			case RetrieveOptions.RETRIEVE_BC_NODE_UPON_NAME:
				bcNode = CollaboratorClient.CHAIN().retrieveBlockChainNodeByName(NodeInfo.BC().getBCName());
				if (bcNode != null)
				{
					System.out.println("Retrieved BC node: " + bcNode);
				}
				else
				{
					System.out.println("No BC node retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_BC_NODE_UPON_NAME_IN_HISTORY:
				bcNodes = CollaboratorClient.CHAIN().retrieveBlockChainNodeByIDInHistory(NodeInfo.BC().getBCName());
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

			case RetrieveOptions.RETRIEVE_BC_NODES_UPON_SERVICE:
				bcNodes = CollaboratorClient.CHAIN().retrieveBlockChainNodesByService(NodeInfo.BC().getService());
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

			case RetrieveOptions.RETRIEVE_BC_NODES_UPON_SERVICE_IN_HISTORY:
				bcNodes = CollaboratorClient.CHAIN().retrieveBlockChainNodesByServiceInHistory(NodeInfo.BC().getService());
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
				bcNodes = CollaboratorClient.CHAIN().retrieveBlockChainNodesByDescription(NodeInfo.BC().getBCDescription());
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

			case RetrieveOptions.RETRIEVE_BC_NODES_UPON_DESCRIPTION_IN_HISTORY:
				bcNodes = CollaboratorClient.CHAIN().retrieveBlockChainNodesByDescriptionInHistory(NodeInfo.BC().getBCDescription());
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
				dsNode = CollaboratorClient.CHAIN().retrieveDataLakeNodeByID(NodeInfo.BC().getDLID());
				if (dsNode != null)
				{
					System.out.println("Retrieved DL node: " + dsNode);
				}
				else
				{
					System.out.println("No DL node retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_DL_NODE_UPON_ID_IN_HISTORY:
				dsNodes = CollaboratorClient.CHAIN().retrieveDataLakeNodeByIDInHistory(NodeInfo.BC().getDLID());
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
				
			case RetrieveOptions.RETRIEVE_DL_NODE_UPON_NAME:
				dsNode = CollaboratorClient.CHAIN().retrieveDataLakeNodeByName(NodeInfo.BC().getDLName());
				if (dsNode != null)
				{
					System.out.println("Retrieved DL node: " + dsNode);
				}
				else
				{
					System.out.println("No DL node retrieved!");
				}
				break;
				
			case RetrieveOptions.RETRIEVE_DL_NODE_UPON_NAME_IN_HISTORY:
				dsNodes = CollaboratorClient.CHAIN().retrieveDataLakeNodeByNameInHistory(NodeInfo.BC().getDLName());
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

			case RetrieveOptions.RETRIEVE_DL_NODES_UPON_SERVICE:
				dsNodes = CollaboratorClient.CHAIN().retrieveDataLakeNodesByService(NodeInfo.BC().getService());
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

			case RetrieveOptions.RETRIEVE_DL_NODES_UPON_SERVICE_IN_HISTORY:
				dsNodes = CollaboratorClient.CHAIN().retrieveDataLakeNodesByServiceInHistory(NodeInfo.BC().getService());
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
				dsNodes = CollaboratorClient.CHAIN().retrieveDataLakeNodesByDescription(NodeInfo.BC().getDLDescription());
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

			case RetrieveOptions.RETRIEVE_DL_NODES_UPON_DESCRIPTION_IN_HISTORY:
				dsNodes = CollaboratorClient.CHAIN().retrieveDataLakeNodesByDescriptionInHistory(NodeInfo.BC().getDLDescription());
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
				dsNode = CollaboratorClient.CHAIN().retrieveOracleNodeByID(NodeInfo.BC().getONID());
				if (dsNode != null)
				{
					System.out.println("Retrieved ON node: " + dsNode);
				}
				else
				{
					System.out.println("No ON node retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_ON_NODE_UPON_ID_IN_HISTORY:
				dsNodes = CollaboratorClient.CHAIN().retrieveOracleNodeByIDInHistory(NodeInfo.BC().getONID());
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

			case RetrieveOptions.RETRIEVE_ON_NODE_UPON_NAME:
				dsNode = CollaboratorClient.CHAIN().retrieveOracleNodeByName(NodeInfo.BC().getONName());
				if (dsNode != null)
				{
					System.out.println("Retrieved ON node: " + dsNode);
				}
				else
				{
					System.out.println("No ON node retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_ON_NODE_UPON_NAME_IN_HISTORY:
				dsNodes = CollaboratorClient.CHAIN().retrieveOracleNodeByNameInHistory(NodeInfo.BC().getONName());
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

			case RetrieveOptions.RETRIEVE_ON_NODES_UPON_SERVICE:
				dsNodes = CollaboratorClient.CHAIN().retrieveDataLakeNodesByService(NodeInfo.BC().getService());
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

			case RetrieveOptions.RETRIEVE_ON_NODES_UPON_SERVICE_IN_HISTORY:
				dsNodes = CollaboratorClient.CHAIN().retrieveDataLakeNodesByServiceInHistory(NodeInfo.BC().getService());
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
				dsNodes = CollaboratorClient.CHAIN().retrieveOracleNodesByDescription(NodeInfo.BC().getONDescription());
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

			case RetrieveOptions.RETRIEVE_ON_NODES_UPON_DESCRIPTION_IN_HISTORY:
				dsNodes = CollaboratorClient.CHAIN().retrieveOracleNodesByDescriptionInHistory(NodeInfo.BC().getONDescription());
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
				dsNode = CollaboratorClient.CHAIN().retrieveSmartContractNodeByID(NodeInfo.BC().getSCID());
				if (dsNode != null)
				{
					System.out.println("Retrieved SC node: " + dsNode);
				}
				else
				{
					System.out.println("No SC node retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_SC_NODE_UPON_ID_IN_HISTORY:
				dsNodes = CollaboratorClient.CHAIN().retrieveSmartContractNodeByIDInHistory(NodeInfo.BC().getSCID());
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

			case RetrieveOptions.RETRIEVE_SC_NODE_UPON_NAME:
				dsNode = CollaboratorClient.CHAIN().retrieveSmartContractNodeByName(NodeInfo.BC().getSCName());
				if (dsNode != null)
				{
					System.out.println("Retrieved SC node: " + dsNode);
				}
				else
				{
					System.out.println("No SC node retrieved!");
				}
				break;

			case RetrieveOptions.RETRIEVE_SC_NODE_UPON_NAME_IN_HISTORY:
				dsNodes = CollaboratorClient.CHAIN().retrieveSmartContractNodeByNameInHistory(NodeInfo.BC().getSCName());
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

			case RetrieveOptions.RETRIEVE_SC_NODES_UPON_SERVICE:
				dsNodes = CollaboratorClient.CHAIN().retrieveSmartContractNodesByService(NodeInfo.BC().getService());
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

			case RetrieveOptions.RETRIEVE_SC_NODES_UPON_SERVICE_IN_HISTORY:
				dsNodes = CollaboratorClient.CHAIN().retrieveSmartContractNodesByServiceInHistory(NodeInfo.BC().getService());
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
				dsNodes = CollaboratorClient.CHAIN().retrieveSmartContractNodesByDescription(NodeInfo.BC().getSCDescription());
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

			case RetrieveOptions.RETRIEVE_SC_NODES_UPON_DESCRIPTION_IN_HISTORY:
				dsNodes = CollaboratorClient.CHAIN().retrieveSmartContractNodesByDescriptionInHistory(NodeInfo.BC().getSCDescription());
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
				bcNodes = CollaboratorClient.CHAIN().retrieveBlockChainNodesByVersion(NodeInfo.BC().getVersion(DBConfig.BC_NODE));
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
				dsNodes = CollaboratorClient.CHAIN().retrieveDataLakeNodesByVersion(NodeInfo.BC().getVersion(DBConfig.DL_NODE));
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
				dsNodes = CollaboratorClient.CHAIN().retrieveOracleNodesByVersion(NodeInfo.BC().getVersion(DBConfig.ON_NODE));
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
				dsNodes = CollaboratorClient.CHAIN().retrieveSmartContractNodesByVersion(NodeInfo.BC().getVersion(DBConfig.SC_NODE));
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
