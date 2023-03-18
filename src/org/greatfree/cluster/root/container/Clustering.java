package org.greatfree.cluster.root.container;

import java.io.IOException;
import java.util.logging.Logger;

import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.util.TerminateSignal;

// Created; 01/13/2019, Bing Li
final class Clustering
{
	private final static Logger log = Logger.getLogger("org.greatfree.cluster.root.container");

	public static void addChild(String childID) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
	{
//		System.out.println("Clustering-addChild(): childID = " + childID);
		PeerAddressResponse response = (PeerAddressResponse)ClusterRoot.CONTAINER().readRegistry(new PeerAddressRequest(childID));
		ClusterRoot.CONTAINER().addChild(childID, response.getPeerAddress().getIPKey(), response.getPeerAddress().getIP(), response.getPeerAddress().getPort());
	}
	
	public static void removeChild(String childID) throws ClassNotFoundException, RemoteReadException, IOException
	{
//		System.out.println("Clustering-removeChild(): childID = " + childID);
		ClusterRoot.CONTAINER().removeChild(childID);
		log.info("Clustering-removeChild(): children count = " + ClusterRoot.CONTAINER().getChildrenCount());
		if (ClusterRoot.CONTAINER().getChildrenCount() <= 0)
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
