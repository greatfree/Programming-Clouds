package org.greatfree.framework.cluster.multicast.child;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.PeerNameIsNullException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.cluster.ClusterConfig;
import org.greatfree.util.TerminateSignal;

/**
 * 
 * @author libing
 * 
 * 03/10/2023
 *
 */
final class StartChild
{

	public static void main(String[] args) throws InvalidKeyException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, IOException, RemoteReadException, InterruptedException, DistributedNodeFailedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException, PeerNameIsNullException
	{
		System.out.println("Cluster child starting up ...");
		ClusterChild.CRY().start(ClusterConfig.REGISTRY_IP, ClusterConfig.REGISTRY_PORT, new ClusterChildTask(), ClusterConfig.ROOT_KEY);
		System.out.println("Cluster child started ...");
		TerminateSignal.SIGNAL().waitTermination();
	}

}
