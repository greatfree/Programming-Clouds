package edu.chainnet.sc.collaborator.child;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cluster.ChildTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.MulticastResponse;
import org.greatfree.message.multicast.container.InterChildrenNotification;
import org.greatfree.message.multicast.container.InterChildrenRequest;
import org.greatfree.message.multicast.container.IntercastNotification;
import org.greatfree.message.multicast.container.IntercastRequest;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

import edu.chainnet.sc.collaborator.child.db.CollaboratorDB;
import edu.chainnet.sc.collaborator.child.db.DBConfig;
import edu.chainnet.sc.message.BCNode;
import edu.chainnet.sc.message.BlockChainNodeRegistryRequest;
import edu.chainnet.sc.message.BlockChainNodeRegistryResponse;
import edu.chainnet.sc.message.DSNode;
import edu.chainnet.sc.message.DataLakeNodeRegistryRequest;
import edu.chainnet.sc.message.DataLakeNodeRegistryResponse;
import edu.chainnet.sc.message.OracleNodeRegistryRequest;
import edu.chainnet.sc.message.OracleNodeRegistryResponse;
import edu.chainnet.sc.message.RetrieveBCNodeUponIDRequest;
import edu.chainnet.sc.message.RetrieveBCNodeUponIDResponse;
import edu.chainnet.sc.message.RetrieveBCNodeUponNameRequest;
import edu.chainnet.sc.message.RetrieveBCNodeUponNameResponse;
import edu.chainnet.sc.message.RetrieveBCNodesUponDescriptionRequest;
import edu.chainnet.sc.message.RetrieveBCNodesUponDescriptionResponse;
import edu.chainnet.sc.message.RetrieveBCNodesUponServiceRequest;
import edu.chainnet.sc.message.RetrieveBCNodesUponServiceResponse;
import edu.chainnet.sc.message.RetrieveBCNodesUponVersionRequest;
import edu.chainnet.sc.message.RetrieveBCNodesUponVersionResponse;
import edu.chainnet.sc.message.RetrieveDLNodeUponIDRequest;
import edu.chainnet.sc.message.RetrieveDLNodeUponIDResponse;
import edu.chainnet.sc.message.RetrieveDLNodeUponNameRequest;
import edu.chainnet.sc.message.RetrieveDLNodeUponNameResponse;
import edu.chainnet.sc.message.RetrieveDLNodesUponDescriptionRequest;
import edu.chainnet.sc.message.RetrieveDLNodesUponDescriptionResponse;
import edu.chainnet.sc.message.RetrieveDLNodesUponServiceRequest;
import edu.chainnet.sc.message.RetrieveDLNodesUponServiceResponse;
import edu.chainnet.sc.message.RetrieveDSNodesUponVersionRequest;
import edu.chainnet.sc.message.RetrieveDSNodesUponVersionResponse;
import edu.chainnet.sc.message.RetrieveONNodeUponIDRequest;
import edu.chainnet.sc.message.RetrieveONNodeUponIDResponse;
import edu.chainnet.sc.message.RetrieveONNodeUponNameRequest;
import edu.chainnet.sc.message.RetrieveONNodeUponNameResponse;
import edu.chainnet.sc.message.RetrieveONNodesUponDescriptionRequest;
import edu.chainnet.sc.message.RetrieveONNodesUponDescriptionResponse;
import edu.chainnet.sc.message.RetrieveONNodesUponServiceRequest;
import edu.chainnet.sc.message.RetrieveONNodesUponServiceResponse;
import edu.chainnet.sc.message.RetrieveSCNodeUponIDRequest;
import edu.chainnet.sc.message.RetrieveSCNodeUponIDResponse;
import edu.chainnet.sc.message.RetrieveSCNodeUponNameRequest;
import edu.chainnet.sc.message.RetrieveSCNodeUponNameResponse;
import edu.chainnet.sc.message.RetrieveSCNodesUponDescriptionRequest;
import edu.chainnet.sc.message.RetrieveSCNodesUponDescriptionResponse;
import edu.chainnet.sc.message.RetrieveSCNodesUponServiceRequest;
import edu.chainnet.sc.message.RetrieveSCNodesUponServiceResponse;
import edu.chainnet.sc.message.SCAppID;
import edu.chainnet.sc.message.SmartContractNodeRegistryRequest;
import edu.chainnet.sc.message.SmartContractNodeRegistryResponse;
import edu.chainnet.sc.message.SynchronizeBCNodeRegistryNotification;
import edu.chainnet.sc.message.SynchronizeDLNodeRegistryNotification;
import edu.chainnet.sc.message.SynchronizeONNodeRegistryNotification;
import edu.chainnet.sc.message.SynchronizeSCNodeRegistryNotification;

/*
 * All of the tasks of the collaborator child are defined in the program. 10/17/2020, Bing Li
 */

// Created: 10/17/2020, Bing Li
class CollaboratorChildTask implements ChildTask
{
	private final static Logger log = Logger.getLogger("edu.chainnet.sc.collaborator.child");

	@Override
	public InterChildrenNotification prepareNotification(IntercastNotification notification)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InterChildrenRequest prepareRequest(IntercastRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processNotification(Notification notification)
	{
		BCNode currentBCNode;
		DSNode currentDSNode;
		switch (notification.getApplicationID())
		{
			case SCAppID.SYNCHRONIZE_BLOCK_CHAIN_NODE_REGISTRY_NOTIFICATION:
				log.info("SYNCHRONIZE_BLOCK_CHAIN_NODE_REGISTRY_NOTIFICATION received @" + Calendar.getInstance().getTime());
				SynchronizeBCNodeRegistryNotification sbcnrn = (SynchronizeBCNodeRegistryNotification)notification;
				currentBCNode = CollaboratorDB.BC().getBCNodeByID(sbcnrn.getKey());
				if (currentBCNode != null)
				{
					CollaboratorDB.BC().saveHistoryNode(currentBCNode);
				}
				else
				{
					CollaboratorDB.BC().saveHistoryNode(sbcnrn.getNode());
				}
				CollaboratorDB.BC().saveNode(sbcnrn.getNode());
				break;
				
			case SCAppID.SYNCHRONIZE_SMART_CONTRACT_NODE_REGISTRY_NOTIFICATION:
				log.info("SYNCHRONIZE_SMART_CONTRACT_NODE_REGISTRY_NOTIFICATION received @" + Calendar.getInstance().getTime());
				SynchronizeSCNodeRegistryNotification sccnrn = (SynchronizeSCNodeRegistryNotification)notification;
				currentDSNode = CollaboratorDB.BC().getDSNodeByID(sccnrn.getKey());
				if (currentDSNode != null)
				{
					CollaboratorDB.BC().saveHistoryNode(currentDSNode, DBConfig.SC_NODE);
				}
				else
				{
					CollaboratorDB.BC().saveHistoryNode(sccnrn.getNode(), DBConfig.SC_NODE);
				}
				CollaboratorDB.BC().saveNode(sccnrn.getNode(), DBConfig.SC_NODE);
				break;
				
			case SCAppID.SYNCHRONIZE_ORACLE_NODE_REGISTRY_NOTIFICATION:
				log.info("SYNCHRONIZE_ORACLE_NODE_REGISTRY_NOTIFICATION received @" + Calendar.getInstance().getTime());
				SynchronizeONNodeRegistryNotification sonnrn = (SynchronizeONNodeRegistryNotification)notification;
				currentDSNode = CollaboratorDB.BC().getDSNodeByID(sonnrn.getKey());
				if (currentDSNode != null)
				{
					CollaboratorDB.BC().saveHistoryNode(currentDSNode, DBConfig.ON_NODE);
				}
				else
				{
					CollaboratorDB.BC().saveHistoryNode(sonnrn.getNode(), DBConfig.ON_NODE);
				}
				CollaboratorDB.BC().saveNode(sonnrn.getNode(), DBConfig.ON_NODE);
				break;
				
			case SCAppID.SYNCHRONIZE_DATA_LAKE_NODE_REGISTRY_NOTIFICATION:
				log.info("SYNCHRONIZE_DATA_LAKE_NODE_REGISTRY_NOTIFICATION received @" + Calendar.getInstance().getTime());
				SynchronizeDLNodeRegistryNotification sdlnrn = (SynchronizeDLNodeRegistryNotification)notification;
				currentDSNode = CollaboratorDB.BC().getDSNodeByID(sdlnrn.getKey());
				if (currentDSNode != null)
				{
					CollaboratorDB.BC().saveHistoryNode(currentDSNode, DBConfig.DL_NODE);
				}
				else
				{
					CollaboratorDB.BC().saveHistoryNode(sdlnrn.getNode(), DBConfig.DL_NODE);
				}
				CollaboratorDB.BC().saveNode(sdlnrn.getNode(), DBConfig.ON_NODE);
				break;
				
			case SCAppID.STOP_COLLABORATOR_CLUSTER_NOTIFICATION:
				log.info("STOP_COLLABORATOR_CLUSTER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					CollaboratorChild.BC().stop(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
		
	}

	@Override
	public MulticastResponse processRequest(Request request)
	{
		switch (request.getApplicationID())
		{
			case SCAppID.BLOCK_CHAIN_NODE_REGISTRY_REQUEST:
				log.info("BLOCK_CHAIN_NODE_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				BlockChainNodeRegistryRequest bcnrr = (BlockChainNodeRegistryRequest)request;
				CollaboratorDB.BC().saveNode(bcnrr.getNode());
				return new BlockChainNodeRegistryResponse(true, bcnrr.getCollaboratorKey());
				
			case SCAppID.SMART_CONTRACT_NODE_REGISTRY_REQUEST:
				log.info("SMART_CONTRACT_NODE_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				SmartContractNodeRegistryRequest scnrr = (SmartContractNodeRegistryRequest)request;
				CollaboratorDB.BC().saveNode(scnrr.getNode(), DBConfig.SC_NODE);
				return new SmartContractNodeRegistryResponse(true, scnrr.getCollaboratorKey());
				
			case SCAppID.ORACLE_NODE_REGISTRY_REQUEST:
				log.info("ORACLE_NODE_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				OracleNodeRegistryRequest onrr = (OracleNodeRegistryRequest)request;
				CollaboratorDB.BC().saveNode(onrr.getNode(), DBConfig.ON_NODE);
				return new OracleNodeRegistryResponse(true, onrr.getCollaboratorKey());
				
			case SCAppID.DATA_LAKE_NODE_REGISTRY_REQUEST:
				log.info("DATA_LAKE_NODE_REGISTRY_REQUEST received @" + Calendar.getInstance().getTime());
				DataLakeNodeRegistryRequest dlnrr = (DataLakeNodeRegistryRequest)request;
				CollaboratorDB.BC().saveNode(dlnrr.getNode(), DBConfig.DL_NODE);
				return new DataLakeNodeRegistryResponse(true, dlnrr.getCollaboratorKey());

			case SCAppID.RETRIEVE_BC_NODE_UPON_ID_REQUEST:
				log.info("RETRIEVE_BC_NODE_UPON_ID_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveBCNodeUponIDRequest bnidr = (RetrieveBCNodeUponIDRequest)request;
				if (!bnidr.isHistorical())
				{
					return new RetrieveBCNodeUponIDResponse(CollaboratorDB.BC().getBCNodeByID(bnidr.getNodeID()), bnidr.getCollaboratorKey());
				}
				else
				{
					return new RetrieveBCNodeUponIDResponse(CollaboratorDB.BC().getHistoryBCNodesByID(bnidr.getNodeID()), bnidr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_BC_NODE_UPON_NAME_REQUEST:
				log.info("RETRIEVE_BC_NODE_UPON_NAME_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveBCNodeUponNameRequest bnnr = (RetrieveBCNodeUponNameRequest)request;
				if (!bnnr.isHistorical())
				{
					return new RetrieveBCNodeUponNameResponse(CollaboratorDB.BC().getBCNodeByName(bnnr.getName()), bnnr.getCollaboratorKey());
				}
				else
				{
					return new RetrieveBCNodeUponNameResponse(CollaboratorDB.BC().getHistoryBCNodesByName(bnnr.getName()), bnnr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_BC_NODES_UPON_SERVICE_REQUEST:
				log.info("RETRIEVE_BC_NODES_UPON_SERVICE_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveBCNodesUponServiceRequest bnsr = (RetrieveBCNodesUponServiceRequest)request;
				if (!bnsr.isHistorical())
				{
					return new RetrieveBCNodesUponServiceResponse(CollaboratorDB.BC().getBCNodesByService(bnsr.getServiceName()), bnsr.getCollaboratorKey());
				}
				else
				{
					return new RetrieveBCNodesUponServiceResponse(CollaboratorDB.BC().getHistoryBCNodesByService(bnsr.getServiceName()), bnsr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_BC_NODES_UPON_DESCRIPTION_REQUEST:
				log.info("RETRIEVE_BC_NODES_UPON_DESCRIPTION_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveBCNodesUponDescriptionRequest bndr = (RetrieveBCNodesUponDescriptionRequest)request;
				if (!bndr.isHistorical())
				{
					return new RetrieveBCNodesUponDescriptionResponse(CollaboratorDB.BC().getBCNodesByDescription(bndr.getDescription()), bndr.getCollaboratorKey());
				}
				else
				{
					return new RetrieveBCNodesUponDescriptionResponse(CollaboratorDB.BC().getHistoryBCNodesByDescription(bndr.getDescription()), bndr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_DL_NODE_UPON_ID_REQUEST:
				log.info("RETRIEVE_DL_NODE_UPON_ID_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveDLNodeUponIDRequest dnidr = (RetrieveDLNodeUponIDRequest)request;
				if (!dnidr.isHistorical())
				{
					return new RetrieveDLNodeUponIDResponse(CollaboratorDB.BC().getDSNodeByID(dnidr.getNodeID()), dnidr.getCollaboratorKey());
				}
				else
				{
//					return new RetrieveDLNodeUponIDResponse(CollaboratorDB.BC().getHistoryDSNodesByID(dnidr.getNodeID(), DBConfig.DL_NODE), dnidr.getCollaboratorKey());
					return new RetrieveDLNodeUponIDResponse(CollaboratorDB.BC().getHistoryDSNodesByID(dnidr.getNodeID()), dnidr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_DL_NODE_UPON_NAME_REQUEST:
				log.info("RETRIEVE_DL_NODE_UPON_NAME_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveDLNodeUponNameRequest dnnr = (RetrieveDLNodeUponNameRequest)request;
				if (!dnnr.isHistorical())
				{
					return new RetrieveDLNodeUponNameResponse(CollaboratorDB.BC().getDSNodeByName(dnnr.getName()), dnnr.getCollaboratorKey());
				}
				else
				{
//					return new RetrieveDLNodeUponNameResponse(CollaboratorDB.BC().getHistoryDSNodesByName(dnnr.getName(), DBConfig.DL_NODE), dnnr.getCollaboratorKey());
					return new RetrieveDLNodeUponNameResponse(CollaboratorDB.BC().getHistoryDSNodesByName(dnnr.getName()), dnnr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_DL_NODES_UPON_SERVICE_REQUEST:
				log.info("RETRIEVE_DL_NODES_UPON_SERVICE_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveDLNodesUponServiceRequest dnsr = (RetrieveDLNodesUponServiceRequest)request;
				if (!dnsr.isHistorical())
				{
					return new RetrieveDLNodesUponServiceResponse(CollaboratorDB.BC().getDSNodesByService(dnsr.getServiceName(), DBConfig.DL_NODE), dnsr.getCollaboratorKey());
				}
				else
				{
					return new RetrieveDLNodesUponServiceResponse(CollaboratorDB.BC().getHistoryDSNodesByService(dnsr.getServiceName(), DBConfig.DL_NODE), dnsr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_DL_NODES_UPON_DESCRIPTION_REQUEST:
				log.info("RETRIEVE_DL_NODES_UPON_DESCRIPTION_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveDLNodesUponDescriptionRequest dndr = (RetrieveDLNodesUponDescriptionRequest)request;
				if (!dndr.isHistorical())
				{
					return new RetrieveDLNodesUponDescriptionResponse(CollaboratorDB.BC().getDSNodesByDescription(dndr.getDescription()), dndr.getCollaboratorKey());
				}
				else
				{
					return new RetrieveDLNodesUponDescriptionResponse(CollaboratorDB.BC().getHistoryDSNodesByDescription(dndr.getDescription()), dndr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_ON_NODE_UPON_ID_REQUEST:
				log.info("RETRIEVE_ON_NODE_UPON_ID_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveONNodeUponIDRequest onidr = (RetrieveONNodeUponIDRequest)request;
				if (!onidr.isHistorical())
				{
					return new RetrieveONNodeUponIDResponse(CollaboratorDB.BC().getDSNodeByID(onidr.getNodeID()), onidr.getCollaboratorKey());
				}
				else
				{
//					return new RetrieveONNodeUponIDResponse(CollaboratorDB.BC().getHistoryDSNodesByID(onidr.getNodeID(), DBConfig.ON_NODE), onidr.getCollaboratorKey());
					return new RetrieveONNodeUponIDResponse(CollaboratorDB.BC().getHistoryDSNodesByID(onidr.getNodeID()), onidr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_ON_NODE_UPON_NAME_REQUEST:
				log.info("RETRIEVE_ON_NODE_UPON_NAME_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveONNodeUponNameRequest onnr = (RetrieveONNodeUponNameRequest)request;
				if (!onnr.isHistorical())
				{
					return new RetrieveONNodeUponNameResponse(CollaboratorDB.BC().getDSNodeByName(onnr.getName()), onnr.getCollaboratorKey());
				}
				else
				{
					return new RetrieveONNodeUponNameResponse(CollaboratorDB.BC().getHistoryDSNodesByName(onnr.getName()), onnr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_ON_NODES_UPON_SERVICE_REQUEST:
				log.info("RETRIEVE_ON_NODES_UPON_SERVICE_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveONNodesUponServiceRequest onsr = (RetrieveONNodesUponServiceRequest)request;
				if (!onsr.isHistorical())
				{
					return new RetrieveONNodesUponServiceResponse(CollaboratorDB.BC().getDSNodesByService(onsr.getServiceName(), DBConfig.ON_NODE), onsr.getCollaboratorKey());
				}
				else
				{
					return new RetrieveONNodesUponServiceResponse(CollaboratorDB.BC().getHistoryDSNodesByService(onsr.getServiceName(), DBConfig.ON_NODE), onsr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_ON_NODES_UPON_DESCRIPTION_REQUEST:
				log.info("RETRIEVE_ON_NODES_UPON_DESCRIPTION_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveONNodesUponDescriptionRequest ondr = (RetrieveONNodesUponDescriptionRequest)request;
				if (!ondr.isHistorical())
				{
					return new RetrieveONNodesUponDescriptionResponse(CollaboratorDB.BC().getDSNodesByDescription(ondr.getDescription()), ondr.getCollaboratorKey());
				}
				else
				{
					return new RetrieveONNodesUponDescriptionResponse(CollaboratorDB.BC().getHistoryDSNodesByDescription(ondr.getDescription()), ondr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_SC_NODE_UPON_ID_REQUEST:
				log.info("RETRIEVE_SC_NODE_UPON_ID_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveSCNodeUponIDRequest scidr = (RetrieveSCNodeUponIDRequest)request;
				if (!scidr.isHistorical())
				{
					return new RetrieveSCNodeUponIDResponse(CollaboratorDB.BC().getDSNodeByID(scidr.getNodeID()), scidr.getCollaboratorKey());
				}
				else
				{
					return new RetrieveSCNodeUponIDResponse(CollaboratorDB.BC().getHistoryDSNodesByID(scidr.getNodeID()), scidr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_SC_NODE_UPON_NAME_REQUEST:
				log.info("RETRIEVE_SC_NODE_UPON_NAME_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveSCNodeUponNameRequest scnr = (RetrieveSCNodeUponNameRequest)request;
				if (!scnr.isHistorical())
				{
					return new RetrieveSCNodeUponNameResponse(CollaboratorDB.BC().getDSNodeByName(scnr.getName()), scnr.getCollaboratorKey());
				}
				else
				{
					return new RetrieveSCNodeUponNameResponse(CollaboratorDB.BC().getHistoryDSNodesByName(scnr.getName()), scnr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_SC_NODES_UPON_SERVICE_REQUEST:
				log.info("RETRIEVE_SC_NODES_UPON_SERVICE_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveSCNodesUponServiceRequest scsr = (RetrieveSCNodesUponServiceRequest)request;
				if (!scsr.isHistorical())
				{
					return new RetrieveSCNodesUponServiceResponse(CollaboratorDB.BC().getDSNodesByService(scsr.getServiceName(), DBConfig.SC_NODE), scsr.getCollaboratorKey());
				}
				else
				{
					return new RetrieveSCNodesUponServiceResponse(CollaboratorDB.BC().getHistoryDSNodesByService(scsr.getServiceName(), DBConfig.SC_NODE), scsr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_SC_NODES_UPON_DESCRIPTION_REQUEST:
				log.info("RETRIEVE_SC_NODES_UPON_DESCRIPTION_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveSCNodesUponDescriptionRequest scdr = (RetrieveSCNodesUponDescriptionRequest)request;
				if (!scdr.isHistorical())
				{
					return new RetrieveSCNodesUponDescriptionResponse(CollaboratorDB.BC().getDSNodesByDescription(scdr.getDescription()), scdr.getCollaboratorKey());
				}
				else
				{
					return new RetrieveSCNodesUponDescriptionResponse(CollaboratorDB.BC().getHistoryDSNodesByDescription(scdr.getDescription()), scdr.getCollaboratorKey());
				}
				
			case SCAppID.RETRIEVE_BC_NODES_UPON_VERSION_REQUEST:
				log.info("RETRIEVE_BC_NODES_UPON_VERSION_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveBCNodesUponVersionRequest bnvr = (RetrieveBCNodesUponVersionRequest)request;
				return new RetrieveBCNodesUponVersionResponse(CollaboratorDB.BC().getHistoryBCNodesByVersion(bnvr.getVersion()), bnvr.getCollaboratorKey());
				
			case SCAppID.RETRIEVE_DS_NODES_UPON_VERSION_REQUEST:
				log.info("RETRIEVE_DS_NODES_UPON_VERSION_REQUEST received @" + Calendar.getInstance().getTime());
				RetrieveDSNodesUponVersionRequest dnvr = (RetrieveDSNodesUponVersionRequest)request;
				return new RetrieveDSNodesUponVersionResponse(CollaboratorDB.BC().getHistoryDSNodesByVersion(dnvr.getVersion(), dnvr.getNodeType()), dnvr.getCollaboratorKey());
		}
		
		return null;
	}

	@Override
	public MulticastResponse processRequest(InterChildrenRequest request)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processResponse(Response response)
	{
		// TODO Auto-generated method stub
		
	}

}
