package org.greatfree.cluster.root;

import java.io.IOException;
import java.util.logging.Logger;

import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.PeerAddressRequest;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.util.TerminateSignal;

// Created: 10/01/2018, Bing Li
class Clustering
{
	private final static Logger log = Logger.getLogger("org.greatfree.cluster.root");

	public static void addChild(String childID) throws ClassNotFoundException, RemoteReadException, IOException, RemoteIPNotExistedException
	{
		log.info("Clustering-addChild(): childID = " + childID);
		PeerAddressResponse response = (PeerAddressResponse)ClusterRoot.CLUSTER().readRegistry(new PeerAddressRequest(childID));
		
//		System.out.println("Clustering-addChild(): key = " + response.getPeerAddress().getIPKey());
//		System.out.println("Clustering-addChild(): ip = " + response.getPeerAddress().getIP());
//		System.out.println("Clustering-addChild(): port = " + response.getPeerAddress().getPort());
//		System.out.println("Clustering-addChild(): ipKey = " + response.getPeerAddress().getIPKey());

		ClusterRoot.CLUSTER().addChild(childID, response.getPeerAddress().getIPKey(), response.getPeerAddress().getIP(), response.getPeerAddress().getPort());
	}
	
	public static void removeChild(String childID) throws ClassNotFoundException, RemoteReadException, IOException
	{
		log.info("Clustering-removeChild(): childID = " + childID);
//		PeerAddressResponse response = (PeerAddressResponse)ClusterRoot.ROOT().readRegistry(new PeerAddressRequest(childID));
		
//		System.out.println("Clustering-removeChild(): key = " + response.getPeerAddress().getIPKey());
//		System.out.println("Clustering-removeChild(): ip = " + response.getPeerAddress().getIP());
//		System.out.println("Clustering-removeChild(): port = " + response.getPeerAddress().getPort());
		
//		ClusterRoot.ROOT().removeChild(response.getPeerAddress().getIPKey());
		ClusterRoot.CLUSTER().removeChild(childID);
		log.info("Clustering-removeChild(): children count = " + ClusterRoot.CLUSTER().getChildrenCount());
		if (ClusterRoot.CLUSTER().getChildrenCount() <= 0)
		{
			log.info("Clustering-removeChild(): notified!");
			TerminateSignal.SIGNAL().notifyTermination();
		}
		else
		{
			log.info("Clustering-removeChild(): NOT notified!");
		}
	}
}
