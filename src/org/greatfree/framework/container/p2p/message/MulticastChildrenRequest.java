package org.greatfree.framework.container.p2p.message;

import org.greatfree.message.SystemMessageType;
import org.greatfree.message.container.Request;

/**
 * 
 * I am writing the book. To replace the primitive registry with the container-based one, the message is necessary to support multicasting in the book. Initially, the multicasting of the book interacts with primitive registry only. 10/14/2022, Bing Li
 * 
 * @author libing
 * 
 * 10/14/2022
 *
 */
public class MulticastChildrenRequest extends Request
{
	private static final long serialVersionUID = -6726525869912529822L;

//	private String rootKey;

//	public MulticastChildrenRequest(String rootKey)
	public MulticastChildrenRequest()
	{
		super(SystemMessageType.MULTICAST_CHILDREN_REQUEST);
//		this.rootKey = rootKey;
	}

	/*
	public String getRootKey()
	{
		return this.rootKey;
	}
	*/
}
