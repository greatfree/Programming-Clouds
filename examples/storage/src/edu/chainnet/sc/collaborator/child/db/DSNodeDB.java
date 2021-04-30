package edu.chainnet.sc.collaborator.child.db;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.greatfree.cache.db.DBEnv;
import org.greatfree.util.FileManager;
import org.greatfree.util.IPAddress;

import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.EntityJoin;
import com.sleepycat.persist.ForwardCursor;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.SecondaryIndex;

import edu.chainnet.sc.message.DSNode;

/*
 * DSNode is saved in the Berkeley Object-Oriented DB for Java. 10/18/2020, Bing Li 
 */

// Created: 10/18/2020, Bing Li
class DSNodeDB
{
	private File envPath;
	private DBEnv env;
	private DSNodeAccessor accessor;
	
	public DSNodeDB(String path)
	{
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
		}
		this.envPath = new File(path);
		this.env = new DBEnv(this.envPath, false, DBConfig.DB_CACHE_SIZE, DBConfig.LOCK_TIME_OUT, DBConfig.DSNODE_STORE);
		this.accessor = new DSNodeAccessor(this.env.getEntityStore());
	}

	public void dispose()
	{
		this.accessor.dispose();
		this.env.close();
	}

	public void saveNode(DSNode node, int nodeType)
	{
		this.accessor.getPrimaryKey().put(new DSNodeEntity(node.getKey(), nodeType, node.getName(), node.getIP().getIP(), node.getIP().getPort(), node.getServiceName(), node.getDescription()));
	}

	public DSNode getNodeByID(String nodeID)
	{
		DSNodeEntity entity = this.accessor.getPrimaryKey().get(nodeID);
		if (entity != null)
		{
			return new DSNode(entity.getName(), new IPAddress(entity.getIP(), entity.getPort()), entity.getServiceName(), entity.getDescription());
		}
		return null;
	}
	
	public DSNode getNodeByName(String name)
	{
		DSNodeEntity entity = this.accessor.getPrimaryKey().get(DSNode.getKey(name));
		if (entity != null)
		{
			return new DSNode(entity.getName(), new IPAddress(entity.getIP(), entity.getPort()), entity.getServiceName(), entity.getDescription());
		}
		return null;
	}
	
	public List<DSNode> getNodesByService(String serviceName, int nodeType)
	{
		PrimaryIndex<String, DSNodeEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, DSNodeEntity.class);
		SecondaryIndex<String, String, DSNodeEntity> sIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, String.class, DBConfig.SERVICE_NAME);
		SecondaryIndex<Integer, String, DSNodeEntity> ntIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, Integer.class, DBConfig.NODE_TYPE);
		EntityJoin<String, DSNodeEntity> join = new EntityJoin<String, DSNodeEntity>(primaryIndex);
		join.addCondition(sIndex, serviceName);
		join.addCondition(ntIndex, nodeType);
		ForwardCursor<DSNodeEntity> results = join.entities();
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (DSNodeEntity entry : results)
		{
			nodes.add(new DSNode(entry.getName(), new IPAddress(entry.getIP(), entry.getPort()), entry.getServiceName(), entry.getDescription()));
		}
		results.close();
		return nodes;
	}
	
	public List<DSNode> getNodesByDescription(String description, int nodeType)
	{
		PrimaryIndex<String, DSNodeEntity> primaryIndex = this.env.getEntityStore().getPrimaryIndex(String.class, DSNodeEntity.class);
		SecondaryIndex<Integer, String, DSNodeEntity> ntIndex = this.env.getEntityStore().getSecondaryIndex(primaryIndex, Integer.class, DBConfig.NODE_TYPE);
		EntityJoin<String, DSNodeEntity> join = new EntityJoin<String, DSNodeEntity>(primaryIndex);
		join.addCondition(ntIndex, nodeType);
		ForwardCursor<DSNodeEntity> results = join.entities();
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (DSNodeEntity entry : results)
		{
			if (entry.getDescription().indexOf(description) >= 0)
			{
				nodes.add(new DSNode(entry.getName(), new IPAddress(entry.getIP(), entry.getPort()), entry.getServiceName(), entry.getDescription()));
			}
		}
		results.close();
		return nodes;
	}

	public List<DSNode> getNodesByDescription(String description)
	{
		EntityCursor<DSNodeEntity> results = this.env.getEntityStore().getPrimaryIndex(String.class, DSNodeEntity.class).entities();
		List<DSNode> nodes = new ArrayList<DSNode>();
		for (DSNodeEntity entry : results)
		{
			if (entry.getDescription().indexOf(description) >= 0)
			{
				nodes.add(new DSNode(entry.getName(), new IPAddress(entry.getIP(), entry.getPort()), entry.getServiceName(), entry.getDescription()));
			}
		}
		results.close();
		return nodes;
	}
}
