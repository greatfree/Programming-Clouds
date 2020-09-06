package org.greatfree.cluster.root.container;

import java.io.IOException;

import org.greatfree.dsf.container.p2p.message.PeerAddressRequest;
import org.greatfree.dsf.multicast.message.PeerAddressResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

// Created; 01/13/2019, Bing Li
class Clustering
{
	public static void addChild(String childID) throws ClassNotFoundException, RemoteReadException, IOException
	{
//		System.out.println("Clustering-addChild(): childID = " + childID);
		PeerAddressResponse response = (PeerAddressResponse)ClusterRoot.CONTAINER().readRegistry(new PeerAddressRequest(childID));
		ClusterRoot.CONTAINER().addChild(childID, response.getPeerAddress().getIPKey(), response.getPeerAddress().getIP(), response.getPeerAddress().getPort());
	}
	
	public static void removeChild(String childID) throws ClassNotFoundException, RemoteReadException, IOException
	{
//		System.out.println("Clustering-removeChild(): childID = " + childID);
		ClusterRoot.CONTAINER().removeChild(childID);
		System.out.println("Clustering-removeChild(): children count = " + ClusterRoot.CONTAINER().getChildrenCount());
		if (ClusterRoot.CONTAINER().getChildrenCount() <= 0)
		{
			System.out.println("Clustering-removeChild(): notified!");
			TerminateSignal.SIGNAL().notifyTermination();
		}
		else
		{
			System.out.println("Clustering-removeChild(): NOT notified!");
		}
	}
}
