package org.greatfree.message.abandoned;

import java.util.Map;

import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

// Created: 12/15/2018, Bing Li
public class OldMulticastRequest extends OldMulticastMessage
{
	private static final long serialVersionUID = -3531345094683159962L;

	private String collaboratorKey;

	public OldMulticastRequest(int type)
	{
		super(type);
		this.collaboratorKey = Tools.generateUniqueKey();
	}

	public OldMulticastRequest(int dataType, Map<String, IPAddress> childrenServerMap)
	{
//		super(dataType, key, childrenServerMap);
		super(dataType, childrenServerMap);
		this.collaboratorKey = Tools.generateUniqueKey();
//		this.rootAddress = rootAddress;
	}

	public String getCollaboratorKey()
	{
		return this.collaboratorKey;
	}
}
