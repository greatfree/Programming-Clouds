package com.greatfree.testing.db;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.greatfree.testing.server.Node;
import com.greatfree.util.FileManager;
import com.greatfree.util.FreeObject;
import com.greatfree.util.Tools;
import com.sleepycat.persist.EntityCursor;

/*
 * The class implements the manipulations on the object, NodeEntity, to save and retrieve in the way supported by the Berkeley DB. It derives from FreeObject such that its instance can be managed by a QueuedPool. 11/03/2014, Bing Li
 */

// Created: 10/08/2014, Bing Li
public class NodeDB extends FreeObject
{
	// Declare the instance of File since operations on a file system is required. 11/03/2014, Bing Li
	private File envPath;
	// Declare the instance of DBEnv to set up the required environment. 11/03/2014, Bing Li
	private DBEnv env;
	// Declare the instance of NodeAccessor to manipulate objects. 11/03/2014, Bing Li
	private NodeAccessor accessor;

	/*
	 * Initialize. 11/03/2014, Bing Li
	 */
	public NodeDB(String path)
	{
		super(Tools.getHash(path));
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.NODE_STORE);
		this.accessor = new NodeAccessor(this.env.getEntityStore());
	}

	/*
	 * Dispose the database. 11/03/2014, Bing Li
	 */
	public void dispose()
	{
		this.accessor.dispose();
		this.env.close();
	}

	/*
	 * Load all of the persisted nodes from the database. 11/03/2014, Bing Li
	 */
	public Map<String, Node> loadAllNodes()
	{
		EntityCursor<NodeEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, NodeEntity.class).entities();
		Map<String, Node> nodes = new HashMap<String, Node>();
		Node nodeValue;
		for (NodeEntity node : results)
		{
			nodeValue = new Node(node.getKey(), node.getUserName(), node.getPassword());
			nodes.put(nodeValue.getKey(), nodeValue);
		}
		results.close();
		return nodes;
	}

	/*
	 * Persist a collection of nodes. 11/03/2014, Bing Li
	 */
	public void saveNodes(Map<String, Node> nodes)
	{
		for (Node node : nodes.values())
		{
			this.saveNode(node);
		}
	}

	/*
	 * Persist a single node. 11/03/2014, Bing Li
	 */
	public void saveNode(Node node)
	{
		this.accessor.getPrimaryIndex().put(new NodeEntity(node.getKey(), node.getUsername(), node.getPassword()));
	}
}
