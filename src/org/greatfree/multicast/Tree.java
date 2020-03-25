package org.greatfree.multicast;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.IPAddress;
import org.greatfree.util.UtilConfig;

/*
 * It aims to construct a tree to raise the quality of multicasting. The tree in the case is simple, such as each node having an equal number of children. The tree is acceptable when all of the nodes have a closed computing capacity and most of them run within a stable computing environment. For a heterogeneous environment, a more complicated tree or other topologies must be applied. 11/10/2014, Bing Li
 */

// Created: 11/10/2014, Bing Li
public class Tree
{
	/*
	 * Construct a tree. 11/10/2014, Bing Li
	 * 
	 * Parameters:
	 * 
	 * 		String: rootKey, the root of the tree;
	 * 
	 *  	int: rootBranchCount, the count of the root;
	 *  
	 *  	Map<String, Integer>: nodes, all of nodes, excluding the root, in the tree. In addition, besides all of the keys of the nodes, the capacity of each node is also kept in the collection. The value is equal to the number of children the node can support when transmitting data.
	 */
	public static Map<String, List<String>> constructTree(String rootKey, int rootBranchCount, Map<String, Integer> nodes)
	{
		// Define a collection to take the tree. 11/10/2014, Bing Li
		Map<String, List<String>> tree = new HashMap<String, List<String>>();
		// Besides the root, the tree consists of has a number of children. All of them are numbered from the largest to the smallest according to their capacities. Moreover, for the root, it must has the count, rootBranchCount, of children. They are also numbered from the left to the right. The integer, firstChildIndex, represents the index of the leftmost one and the one, endChildIndex represents the rightmost one. 11/10/2014, Bing Li
		int firstChildIndex;
		int endChildIndex;
		// When constructing the tree, it is necessary to select the parent node. The parentNodeKey keeps the parent node temporarily. 11/10/2014, Bing Li
		String parentNodeKey;
		// The childNodeKey keeps the child node temporarily. 11/10/2014, Bing Li
		String childNodeKey;
		// Sort the nodes according to their capacities in the order from the larger to the smaller. 11/10/2014, Bing Li
		Map<String, Integer> nodeCapacityMap = CollectionSorter.sortDescendantByValue(nodes);
		// Assign the sorted nodes into a list, which is convenient to be accessed by the integer index. The smaller index represents a larger capacity, and vice versa. In general, the node which has a higher capacity must be placed in the higher level within the tree. The sorting supports the following algorithm reaches the goal. 11/10/2014, Bing Li
		List<String> nodeKeys = new LinkedList<String>(nodeCapacityMap.keySet());
		// The rootKey should also be counted. So plus one here. 11/10/2014, Bing Li
		int nodeSize = nodeKeys.size() + 1;
		// For each node, a certain number of children are assigned. Therefore, the tree is constructed. 11/10/2014, Bing Li
		for (int i = 0; i < nodeSize; i ++)
		{
			// To get the leftmost child for the node i, it is required to input the root children count and all of the nodes' capacities. 11/10/2014, Bing Li
			firstChildIndex = getFirstChildIndex(i, rootBranchCount, nodeCapacityMap);
			// Check whether the first child index is less than the total node count to ensure it is valid. Otherwise, it means all of the nodes have got their children such that the procedure can be terminated. 11/10/2014, Bing Li
			if (firstChildIndex < nodeSize)
			{
				// If i is equal to 0, it denotes that the current parent node key is the root key. 11/10/2014, Bing Li
				if (i == 0)
				{
					// Assign the root key to the parent node key. 11/10/2014, Bing Li
					parentNodeKey = rootKey;
				}
				else
				{
					// If i is not equal to 0, it is convenient to get the key by its index. 11/10/2014, Bing Li
					parentNodeKey = nodeKeys.get(i - 1);
				}
				
				// Check if i is equal to 0. 11/10/2014, Bing Li 
				if (i == 0)
				{
					// Since i is equal to 0, it denotes that it is the first child index of the root key is found. According to the capacity of the root, it is convenient to get the rightmost child of the root. 11/10/2014, Bing Li
					endChildIndex = firstChildIndex + rootBranchCount - 1;
				}
				else
				{
					// Since i is not equal to 0, it denotes that it is the first child index of another node is found. According to the capacity of the node, it is convenient to get its rightmost child. 11/10/2014, Bing Li
					endChildIndex = firstChildIndex + nodeCapacityMap.get(parentNodeKey) - 1;
				}
				
				// After the leftmost, firstChildIndex, and the rightmost, endChildIndex, are found, it is easy to get all of the other node keys between them. 11/10/2014, Bing Li
				for (int j = firstChildIndex; j <= endChildIndex; j++)
				{
					// Check whether j is larger than the node size. If it is, it denotes that all of the nodes have got their children. Then, the algorithm is terminated. 11/10/2014, Bing Li
					if (j < nodeSize)
					{
						// If j is less than the node size, get the key according to the index, j. 11/10/2014, Bing Li
						childNodeKey = nodeKeys.get(j - 1);
						// Check if the current parent key is put into the collection, tree, to be returned. 11/10/2014, Bing Li
						if (!tree.containsKey(parentNodeKey))
						{
							// If the collection, tree, does not include the parent key, add it and its child key. 11/10/2014, Bing Li
							tree.put(parentNodeKey, new LinkedList<String>());
							tree.get(parentNodeKey).add(childNodeKey);
						}
						else
						{
							// If the collection, tree, includes the parent key, add its child key. 11/10/2014, Bing Li
							tree.get(parentNodeKey).add(childNodeKey);
						}
					}
					else
					{
						// Return the tree. 11/10/2014, Bing Li
						return tree;
					}
				}
			}
			else
			{
				// Return the tree. 11/10/2014, Bing Li
				return tree;
			}
		}
		// Return the tree. 11/10/2014, Bing Li
		return tree;
	}

	/*
	 * The method constructs a tree in which each node, except the root node, has the identical capacity. 11/10/2014, Bing Li
	 * 
	 * Parameters:
	 * 
	 *		String: rootKey, the root of the tree;
	 *
	 *		List<String>: nodeKeys, all of the node keys to be added into the tree;
	 *
	 *		int: rootBranchCount, the root capacity or the branch count;
	 *
	 *		int: branchCount, the capacity or the branch count of each node, except the root. 
	 * 
	 */
	public static Map<String, List<String>> constructTree(String rootKey, List<String> nodeKeys, int rootBranchCount, int branchCount)
	{
		// Define a collection to take the tree. 11/10/2014, Bing Li
		Map<String, List<String>> tree = new HashMap<String, List<String>>();
		// Besides the root, the tree consists of has a number of children. All of them are numbered from the largest to the smallest according to their capacities. Moreover, for the root, it must has the count, rootBranchCount, of children. They are also numbered from the left to the right. The integer, firstChildIndex, represents the index of the leftmost one and the one, endChildIndex represents the rightmost one. 11/10/2014, Bing Li
		int firstChildIndex;
		int endChildIndex;
		// When constructing the tree, it is necessary to select the parent node. The parentNodeKey keeps the parent node temporarily. 11/10/2014, Bing Li
		String parentNodeKey;
		// The childNodeKey keeps the child node temporarily. 11/10/2014, Bing Li
		String childNodeKey;
		// The rootKey should also be counted. So plus one here. 11/10/2014, Bing Li
		int nodeSize = nodeKeys.size() + 1;
		// For each node, a certain number of children are assigned. Therefore, the tree is constructed. 11/10/2014, Bing Li
		for (int i = 0; i < nodeSize; i++)
		{
			// To get the leftmost child for the node i, it is required to input the root children count and all of the nodes' capacities, which are the same in this case. 11/10/2014, Bing Li
			firstChildIndex = getFirstChildIndex(i, rootBranchCount, branchCount);
			// Check whether the first child index is less than the total node count to ensure it is valid. Otherwise, it means all of the nodes have got their children such that the procedure can be terminated. 11/10/2014, Bing Li
			if (firstChildIndex < nodeSize)
			{
				// If i is equal to 0, it denotes that the current parent node key is the root key. 11/10/2014, Bing Li
				if (i == 0)
				{
					// Assign the root key to the parent node key. 11/10/2014, Bing Li
					parentNodeKey = rootKey;
				}
				else
				{
					// If i is not equal to 0, it is convenient to get the key by its index. 11/10/2014, Bing Li
					parentNodeKey = nodeKeys.get(i - 1);
				}

				// Check if i is equal to 0. 11/10/2014, Bing Li 
				if (i == 0)
				{
					// Since i is equal to 0, it denotes that it is the first child index of the root key is found. According to the capacity of the root, it is convenient to get the rightmost child of the root. 11/10/2014, Bing Li
					endChildIndex = firstChildIndex + rootBranchCount - 1;
				}
				else
				{
					// Since i is not equal to 0, it denotes that it is the first child index of another node is found. According to the capacity of the node, it is convenient to get its rightmost child. 11/10/2014, Bing Li
					endChildIndex = firstChildIndex + branchCount - 1;
				}

				// After the leftmost, firstChildIndex, and the rightmost, endChildIndex, are found, it is easy to get all of the other node keys between them. 11/10/2014, Bing Li
				for (int j = firstChildIndex; j <= endChildIndex; j ++)
				{
					// Check whether j is larger than the node size. If it is, it denotes that all of the nodes have got their children. Then, the algorithm is terminated. 11/10/2014, Bing Li
					if (j < nodeSize)
					{
						// If j is less than the node size, get the key according to the index, j. 11/10/2014, Bing Li
						childNodeKey = nodeKeys.get(j - 1);
						// Check if the current parent key is put into the collection, tree, to be returned. 11/10/2014, Bing Li
						if (!tree.containsKey(parentNodeKey))
						{
							// If the collection, tree, does not include the parent key, add it and its child key. 11/10/2014, Bing Li
							tree.put(parentNodeKey, new LinkedList<String>());
							tree.get(parentNodeKey).add(childNodeKey);
						}
						else
						{
							// If the collection, tree, includes the parent key, add its child key. 11/10/2014, Bing Li
							tree.get(parentNodeKey).add(childNodeKey);
						}
					}
					else
					{
						// Return the tree. 11/10/2014, Bing Li
						return tree;
					}
				}
			}
			else
			{
				// Return the tree. 11/10/2014, Bing Li
				return tree;
			}
		}
		// Return the tree. 11/10/2014, Bing Li
		return tree;
	}
	
	/*
	 * This method aims to construct a tree, in which each node, including the root, has the identical capacity or the branch count. The first node in nodeKeys is regarded as the root. 11/10/2014, Bing Li
	 */
	public static Map<String, List<String>> constructTree(List<String> nodeKeys, int branchCount)
	{
		// Define a collection to take the tree. 11/10/2014, Bing Li
		Map<String, List<String>> tree = new HashMap<String, List<String>>();
		// Besides the root, the tree consists of has a number of children. All of them are numbered from the largest to the smallest according to their capacities. Moreover, for the root, it must has the count, rootBranchCount, of children. They are also numbered from the left to the right. The integer, firstChildIndex, represents the index of the leftmost one and the one, endChildIndex represents the rightmost one. 11/10/2014, Bing Li
		int firstChildIndex;
		int endChildIndex;
		// When constructing the tree, it is necessary to select the parent node. The parentNodeKey keeps the parent node temporarily. 11/10/2014, Bing Li
		String parentNodeKey;
		// The childNodeKey keeps the child node temporarily. 11/10/2014, Bing Li
		String childNodeKey;
		// For each node, a certain number of children are assigned. Therefore, the tree is constructed. 11/10/2014, Bing Li
		for (int i = 0; i < nodeKeys.size(); i++)
		{
			// To get the leftmost child for the node i, it is required to input the root children count and all of the nodes' capacities, which are the same in this case. 11/10/2014, Bing Li
			firstChildIndex = getFirstChildIndex(i, branchCount);
			// Check whether the first child index is less than the total node count to ensure it is valid. Otherwise, it means all of the nodes have got their children such that the procedure can be terminated. 11/10/2014, Bing Li
			if (firstChildIndex < nodeKeys.size())
			{
				// Get the parent node key by retrieving the node index. 11/10/2014, Bing Li
				parentNodeKey = nodeKeys.get(i);
				// Since each node has the identical capacity or the branch count, it is easy to estimate the rightmost child index. 11/10/2014, Bing Li
				endChildIndex = firstChildIndex + branchCount - 1;
				// After the leftmost, firstChildIndex, and the rightmost, endChildIndex, are found, it is easy to get all of the other node keys between them. 11/10/2014, Bing Li
				for (int j = firstChildIndex; j <= endChildIndex; j++)
				{
					// Check whether j is larger than the node size. If it is, it denotes that all of the nodes have got their children. Then, the algorithm is terminated. 11/10/2014, Bing Li
					if (j < nodeKeys.size())
					{
						// If j is less than the node size, get the key according to the index, j. 11/10/2014, Bing Li
						childNodeKey = nodeKeys.get(j);
						// Check if the current parent key is put into the collection, tree, to be returned. 11/10/2014, Bing Li
						if (!tree.containsKey(parentNodeKey))
						{
							// If the collection, tree, does not include the parent key, add it and its child key. 11/10/2014, Bing Li
							tree.put(parentNodeKey, new LinkedList<String>());
							tree.get(parentNodeKey).add(childNodeKey);
						}
						else
						{
							// If the collection, tree, includes the parent key, add its child key. 11/10/2014, Bing Li
							tree.get(parentNodeKey).add(childNodeKey);
						}
					}
					else
					{
						// Return the tree. 11/10/2014, Bing Li
						return tree;
					}
				}
			}
			else
			{
				// Return the tree. 11/10/2014, Bing Li
				return tree;
			}
		}
		// Return the tree. 11/10/2014, Bing Li
		return tree;
	}

	/*
	 * This method aims to construct a tree, in which each node, including the root, has the identical capacity or the branch count. The root key is specified, excluding from the node keys. 11/10/2014, Bing Li
	 */
	public static Map<String, List<String>> constructTree(String rootKey, List<String> nodeKeys, int branchCount)
	{
		// Define a collection to take the tree. 11/10/2014, Bing Li
		Map<String, List<String>> tree = new HashMap<String, List<String>>();
		// Besides the root, the tree consists of has a number of children. All of them are numbered from the largest to the smallest according to their capacities. Moreover, for the root, it must has the count, rootBranchCount, of children. They are also numbered from the left to the right. The integer, firstChildIndex, represents the index of the leftmost one and the one, endChildIndex represents the rightmost one. 11/10/2014, Bing Li
		int firstChildIndex;
		int endChildIndex;
		// When constructing the tree, it is necessary to select the parent node. The parentNodeKey keeps the parent node temporarily. 11/10/2014, Bing Li
		String parentNodeKey;
		// The childNodeKey keeps the child node temporarily. 11/10/2014, Bing Li
		String childNodeKey;
		// The rootKey should also be counted. So plus one here. 11/10/2014, Bing Li
		int nodeSize = nodeKeys.size() + 1;
		// For each node, a certain number of children are assigned. Therefore, the tree is constructed. 11/10/2014, Bing Li
		for (int i = 0; i < nodeSize; i ++)
		{
			// To get the leftmost child for the node i, it is required to input the root children count and all of the nodes' capacities, which are the same in this case. 11/10/2014, Bing Li
			firstChildIndex = getFirstChildIndex(i, branchCount);
			// Check whether the first child index is less than the total node count to ensure it is valid. Otherwise, it means all of the nodes have got their children such that the procedure can be terminated. 11/10/2014, Bing Li
			if (firstChildIndex < nodeSize)
			{
				// If i is equal to 0, it denotes that the current parent node key is the root key. 11/10/2014, Bing Li
				if (i == 0)
				{
					// Assign the root key to the parent node key. 11/10/2014, Bing Li
					parentNodeKey = rootKey;
				}
				else
				{
					// If i is not equal to 0, it is convenient to get the key by its index. 11/10/2014, Bing Li
					parentNodeKey = nodeKeys.get(i - 1);
				}
				// Since each node has the identical capacity or the branch count, it is easy to estimate the rightmost child index. 11/10/2014, Bing Li
				endChildIndex = firstChildIndex + branchCount - 1;
				// After the leftmost, firstChildIndex, and the rightmost, endChildIndex, are found, it is easy to get all of the other node keys between them. 11/10/2014, Bing Li
				for (int j = firstChildIndex; j <= endChildIndex; j ++)
				{
					// Check whether j is larger than the node size. If it is, it denotes that all of the nodes have got their children. Then, the algorithm is terminated. 11/10/2014, Bing Li
					if (j < nodeSize)
					{
						// If j is less than the node size, get the key according to the index, j. 11/10/2014, Bing Li
						childNodeKey = nodeKeys.get(j - 1);
						// Check if the current parent key is put into the collection, tree, to be returned. 11/10/2014, Bing Li
						if (!tree.containsKey(parentNodeKey))
						{
							// If the collection, tree, does not include the parent key, add it and its child key. 11/10/2014, Bing Li
							tree.put(parentNodeKey, new LinkedList<String>());
							tree.get(parentNodeKey).add(childNodeKey);
						}
						else
						{
							// If the collection, tree, includes the parent key, add its child key. 11/10/2014, Bing Li
							tree.get(parentNodeKey).add(childNodeKey);
						}
					}
					else
					{
						// Return the tree. 11/10/2014, Bing Li
						return tree;
					}
				}
			}
			else
			{
				// Return the tree. 11/10/2014, Bing Li
				return tree;
			}
		}
		// Return the tree. 11/10/2014, Bing Li
		return tree;
	}

	/*
	 * With the constructed tree, get all of the children keys, from the immediate ones to the leaves, of a parent node. 11/10/2014, Bing Li
	 */
	public static List<String> getAllChildrenKeys(Map<String, List<String>> tree, String parentNodeKey)
	{
		// Check whether the tree contains the parent node. 11/10/2014, Bing Li
		if (tree.containsKey(parentNodeKey))
		{
			// Initialize a list to keep the children keys. 11/10/2014, Bing Li
			List<String> allChildrenKeys = new LinkedList<String>();
			// Get the immediate children keys. 11/10/2014, Bing Li
			List<String> childrenKeys = tree.get(parentNodeKey);
			// Keep the immediate children keys. 11/10/2014, Bing Li
			allChildrenKeys.addAll(childrenKeys);
			// For each of the immediate child, get its children keys, from the immediate ones to the leaves. 11/10/2014, Bing Li
			for (String childrenKey : childrenKeys)
			{
				// Invoke the method recursively. 11/10/2014, Bing Li
				childrenKeys = getAllChildrenKeys(tree, childrenKey);
				// Check if the children keys are valid. 11/10/2014, Bing Li
				if (childrenKeys != UtilConfig.NO_CHILDREN_KEYS)
				{
					// Keep the immediate children keys. 11/10/2014, Bing Li
					allChildrenKeys.addAll(childrenKeys);
				}
			}
			// Return the children list. 11/10/2014, Bing Li
			return allChildrenKeys;
		}
		// Return null if the parent node key is not included in the tree. 11/10/2014, Bing Li
		return UtilConfig.NO_CHILDREN_KEYS;
	}
	
	/*
	 * Get the tree level of interior nodes. 11/10/2014, Bing Li
	 */
	private static int getTreeInteriorLevelCount(int treeNodeCount, int treeBranchCount)
	{
		// Check if the root branch is equal to 0. 11/10/2014, Bing Li
		if (treeBranchCount == 0)
		{
			// If the root branch is 0, it does not make sense in practice. Return 0. 11/10/2014, Bing Li
			return 0;
		}
		else
		{
			// Initialize the interior level as 0. 11/10/2014, Bing Li
			int interiorLevel = 0;
			// Initialize the currently node count in the tree as 0. 11/10/2014, Bing Li
			int currentNodeCount = 0;
			// Check whether the current node count in the tree is greater than the tree node count to be added. 11/10/2014, Bing Li
			while (currentNodeCount < treeNodeCount)
			{
				// If the current node count in the tree is still less than the tree node count to be added, the interior level should be incremented. 11/10/2014, Bing Li
				interiorLevel++;
				// The count of the interior nodes is equal to that of the root capacity multiplying the interior level to the power of each node's capacity. 11/10/2014, Bing Li
				currentNodeCount += Math.pow(treeBranchCount, interiorLevel);
			}
			// Since the current node count in the tree is estimated from 0 and the interior level is incremented at the value, the returned value should be subtracted 1. 11/10/2014, Bing Li
			return interiorLevel - 1;
		}
	}

	/*
	 * Get the tree level of interior nodes. 11/10/2014, Bing Li
	 */
	private static int getTreeInteriorLevelCount(int treeNodeCount, int rootBranchCount, int treeBranchCount)
	{
		// Check if the root branch is equal to 0. 11/10/2014, Bing Li
		if (rootBranchCount == 0)
		{
			// If the root branch is 0, it does not make sense in practice. Return 0. 11/10/2014, Bing Li
			return 0;
		}
		else
		{
			// Initialize the interior level as 0. 11/10/2014, Bing Li
			int interiorLevel = 0;
			// Initialize the currently node count in the tree as 0. 11/10/2014, Bing Li
			int currentNodeCount = 0;
			// Check whether the current node count in the tree is greater than the tree node count to be added. 11/10/2014, Bing Li
			while (currentNodeCount < treeNodeCount)
			{
				// If the current node count in the tree is still less than the tree node count to be added, the interior level should be incremented. 11/10/2014, Bing Li
				interiorLevel++;
				// Since each node, except the root, has the identical capacity, the node count on the certain level of the tree is easy to be estimated. 11/10/2014, Bing Li
				if (interiorLevel == 1)
				{
					// When the level is 1, the count of the interior nodes is equal to the root capacity. 11/10/2014, Bing Li
					currentNodeCount += rootBranchCount;
				}
				else
				{
					// Otherwise, the count of the interior nodes is equal to that of the root capacity multiplying the interior level to the power of each node's capacity. 11/10/2014, Bing Li
					currentNodeCount += rootBranchCount * Math.pow(treeBranchCount, interiorLevel);
				}
			}
			// Since the current node count in the tree is estimated from 0 and the interior level is incremented at the value, the returned value should be subtracted 1. 11/10/2014, Bing Li
			return interiorLevel - 1;
		}
	}

	/*
	 * Get the tree level of interior nodes. 11/10/2014, Bing Li
	 */
	private static int getTreeInteriorLevelCount(int treeNodeCount, int rootBranchCount, Map<String, Integer> nodeCapacityMap)
	{
		// Check if the root branch is equal to 0. 11/10/2014, Bing Li
		if (rootBranchCount == 0)
		{
			// If the root branch is 0, it does not make sense in practice. Return 0. 11/10/2014, Bing Li
			return 0;
		}
		else
		{
			// Initialize the interior level as 0. 11/10/2014, Bing Li
			int interiorLevel = 0;
			// Initialize the currently node count in the tree as 0. 11/10/2014, Bing Li
			int currentNodeCount = 0;
			// Check whether the current node count in the tree is greater than the tree node count to be added. 11/10/2014, Bing Li
			while (currentNodeCount < treeNodeCount)
			{
				// If the current node count in the tree is still less than the tree node count to be added, the interior level should be incremented. 11/10/2014, Bing Li
				interiorLevel++;
				// With the predefined interior level, each node's capacity and the root branch, invoke the method to get the current node count in the tree. 11/10/2014, Bing Li
				currentNodeCount = getCompleteTreeNodeCount(nodeCapacityMap, rootBranchCount, interiorLevel);
			}
			// Since the current node count in the tree is estimated from 0 and the interior level is incremented at the value, the returned value should be subtracted 1. 11/10/2014, Bing Li
			return interiorLevel - 1;
		}
	}

	/*
	 * Get all of the tree node count in the form of the complete tree upon the tree level, each node's capacity and the root branch. 11/10/2014, Bing Li
	 */
	private static int getCompleteTreeNodeCount(Map<String, Integer> nodeCapacityMap, int rootBranchCount, int levelCount)
	{
		// If the level count is 0, the tree node count is 1. 11/10/2014, Bing Li
		if (levelCount == 0)
		{
			return 1;
		}
		// If the level count is 1, the tree node count is equal to the sum of the root node plus the root branch count. 11/10/2014, Bing Li
		else if (levelCount == 1)
		{
			return rootBranchCount + 1;
		}
		else
		{
			// Get the tree node count in the form of the complete tree of the father level. This is a recursive call. 11/10/2014, Bing Li
			int upperFatherLevelsTreeNodeCount = getCompleteTreeNodeCount(nodeCapacityMap, rootBranchCount, levelCount - 1);
			// Get the tree node count in the form of the complete tree of the grandfather level. This is a recursive call. 11/10/2014, Bing Li
			int upperGrandfatherLevelsTreeNodeCount = getCompleteTreeNodeCount(nodeCapacityMap, rootBranchCount, levelCount - 2);
			// Initialize the complete tree node count as the value of the tree node count in the form of the complete tree of the father level. 11/10/2014, Bing Li
			int completeTreeNodeCount = upperFatherLevelsTreeNodeCount;
			// Save the node keys into a list so as to retrieve the node key by the node index. 11/10/2014, Bing Li
			List<String> nodeKeys = new LinkedList<String>(nodeCapacityMap.keySet());
			// Add the capacities of nodes on the father level to the tree node count on the father level. The total count is the node count of the current level. 11/10/2014, Bing Li
			for (int i = upperGrandfatherLevelsTreeNodeCount; i < upperFatherLevelsTreeNodeCount; i++)
			{
				// The index, i, is valid only if it is less than the total node size minus 1. 11/10/2014, Bing Li
				if (i - 1< nodeKeys.size())
				{
					// Add the capacity of one node's capacity on the father level. 11/10/2014, Bing Li
					completeTreeNodeCount += nodeCapacityMap.get(nodeKeys.get(i - 1));
				}
				else
				{
					// If the index, i, is not valid, jump out the loop. 11/10/2014, Bing Li
					break;
				}
			}
			// Return the tree node in the form of the complete tree. 11/10/2014, Bing Li
			return completeTreeNodeCount;
		}
	}
	
	/*
	 * Get the count of the tree level once if the tree has the certain number of nodes and their capacities are identical. 11/10/2014, Bing Li
	 */
	private static int getTreeLevelCount(int treeNodeCount, int treeBranchCount)
	{
		// Invoke the method to get the tree level of interior nodes. To get the total level of the tree, add one. 11/10/2014, Bing Li
		return getTreeInteriorLevelCount(treeNodeCount, treeBranchCount) + 1;
	}
	
	/*
	 * Get the count of the tree level once if the tree has the certain number of nodes and their capacities are identical except that of the root. 11/10/2014, Bing Li
	 */
	private static int getTreeLevelCount(int treeNodeCount, int rootBranchCount, int treeBranchCount)
	{
		// Invoke the method to get the tree level of interior nodes. To get the total level of the tree, add one. 11/10/2014, Bing Li
		return getTreeInteriorLevelCount(treeNodeCount, rootBranchCount, treeBranchCount) + 1;
	}

	/*
	 * Get the count of the tree level once if the tree has the certain number of nodes and their capacities are known. 11/10/2014, Bing Li
	 */
	private static int getTreeLevelCount(int treeNodeCount, int rootBranchCount, Map<String, Integer> nodeCapacityMap)
	{
		// Invoke the method to get the tree level of interior nodes. To get the total level of the tree, add one. 11/10/2014, Bing Li
		return getTreeInteriorLevelCount(treeNodeCount, rootBranchCount, nodeCapacityMap) + 1;
	}

	/*
	 * Once if the tree level is known and each node has the identical capacity or the identical branch count, the count of the tree nodes in the form of the complete tree is the sum of each node's capacity. It can be estimated with the support of power. 11/10/2014, Bing Li
	 */
	private static int getCompleteTreeNodeCount(int treeBranchCount, double treeLevelCount)
	{
		int interiorNodeCount = 0;
		for (int i = 0; i <= treeLevelCount; i++)
		{
			interiorNodeCount += Math.pow(treeBranchCount, i);
		}
		return interiorNodeCount;
	}
	
	/*
	 * Once if the tree level is known and each node has the identical capacity or the identical branch count except that of the root, the count of the tree nodes in the form of the complete tree is the sum of each node's capacity. It can be estimated with the support of power. 11/10/2014, Bing Li
	 */
	private static int getCompleteTreeNodeCount(int rootBranchCount, int treeBranchCount, double treeLevelCount)
	{
		int interiorNodeCount = 0;
		for (int i = 0; i <= treeLevelCount; i++)
		{
			if (i == 0)
			{
				interiorNodeCount += 1;
			}
			else
			{
				interiorNodeCount += rootBranchCount * Math.pow(treeBranchCount, i - 1);
			}
		}
		return interiorNodeCount;
	}
	
	/*
	 * The method gets the tree level a node resides. 11/10/2014, Bing Li
	 */
	private static int getTreeLevelResided(int nodeIndex, int treeBranchCount)
	{
		// Get the count of the tree level once if the tree has the certain number of nodes and their capacities are identical. Since the node index starts from zero, it is required to add one to the index to invoke the method. 11/10/2014, Bing Li
		return getTreeLevelCount(nodeIndex + 1, treeBranchCount);
	}
	
	/*
	 * The method gets the tree level a node resides. 11/10/2014, Bing Li
	 */
	private static int getTreeLevelResided(int nodeIndex, int rootBranchCount, int treeBranchCount)
	{
		// Get the count of the tree level once if the tree has the certain number of nodes and their capacities are identical except that of the root. Since the node index starts from zero, it is required to add one to the index to invoke the method. 11/10/2014, Bing Li
		return getTreeLevelCount(nodeIndex + 1, rootBranchCount, treeBranchCount);
	}

	/*
	 * The method gets the tree level a node resides. The node is represented by its index after sorting by node capacities in the descendant order. 11/10/2014, Bing Li
	 */
	private static int getTreeLevelResided(int nodeIndex, int rootBranchCount, Map<String, Integer> nodeCapacityMap)
	{
		// Get the count of the tree level once if the tree has the certain number of nodes and their capacities are known. Since the node index starts from zero, it is required to add one to the index to invoke the method. 11/10/2014, Bing Li
		return getTreeLevelCount(nodeIndex + 1, rootBranchCount, nodeCapacityMap);
	}
	
	/*
	 * The method gets the leftmost index, firstChildIndex, of a node's children. Each node has the identical capacity or the branch count. 11/10/2014, Bing Li 
	 */
	private static int getFirstChildIndex(int currentNodeIndex, int treeBranchCount)
	{
		// Check whether the index of the current node is greater than 0. 11/10/2014, Bing Li
		if (currentNodeIndex > 0)
		{
			// Get the level of the tree the current node resides. 11/10/2014, Bing Li
			int treeLevelResided = getTreeLevelResided(currentNodeIndex, treeBranchCount);
			// Check if the resided tree level is greater than 0. 11/10/2014, Bing Li
			if (treeLevelResided > 0)
			{
				// If the resided tree level is obtained, it is reasonable to get the node count of all of upper levels. 11/10/2014, Bing Li
				int upperLevelNodeCount = getCompleteTreeNodeCount(treeBranchCount, treeLevelResided - 1);
				// After getting the node count of all of upper levels, it is reasonable to get the index of the current node is sorted on its own level. 11/10/2014, Bing Li
				int currentLevelNodeIndex = currentNodeIndex - upperLevelNodeCount;
				/*
				 *  Since except the root node, each node has the same capacity, to get the leftmost child index, the value is the sum of
				 *
				 *  		The total count of the nodes before the current node on the level multiplying the branch count;
				 *  
				 *  		The count of all of the nodes in the tree of the level.
				 *  
				 *  11/10/2014, Bing Li
				 *  
				 */
				return currentLevelNodeIndex * treeBranchCount + getCompleteTreeNodeCount(treeBranchCount, treeLevelResided);
			}
			else
			{
				// According to the algorithm, the resided tree level is equal to 0 only if the tree contains only one node, i.e., the root node. It does not make sense in practice. Therefore, if the level is not greater than 0, the leftmost node index of 0. 11/10/2014, Bing Li 
				return 0;
			}
		}
		else
		{
			// If the index of the current node is not greater than 0, it denotes it is the root. Then, its leftmost child index is 1. Return 1. 11/10/2014, Bing Li
			return 1;
		}
	}

	/*
	 * The method gets the leftmost index, firstChildIndex, of a node's children. 11/10/2014, Bing Li 
	 */
	private static int getFirstChildIndex(int currentNodeIndex, int rootBranchCount, int treeBranchCount)
	{
		// Check whether the index of the current node is greater than 0. 11/10/2014, Bing Li
		if (currentNodeIndex > 0)
		{
			// Get the level of the tree the current node resides. 11/10/2014, Bing Li
			int treeLevelResided = getTreeLevelResided(currentNodeIndex, rootBranchCount, treeBranchCount);
			// Check if the resided tree level is greater than 0. 11/10/2014, Bing Li
			if (treeLevelResided > 0)
			{
				// If the resided tree level is obtained, it is reasonable to get the node count of all of upper levels. 11/10/2014, Bing Li
				int upperLevelNodeCount = getCompleteTreeNodeCount(rootBranchCount, treeBranchCount, treeLevelResided - 1);
				// After getting the node count of all of upper levels, it is reasonable to get the index of the current node is sorted on its own level. 11/10/2014, Bing Li
				int currentLevelNodeIndex = currentNodeIndex - upperLevelNodeCount;
				/*
				 *  Since except the root node, each node has the same capacity, to get the leftmost child index, the value is the sum of
				 *
				 *  		The total count of the nodes before the current node on the level multiplying the branch count;
				 *  
				 *  		The count of all of the nodes in the tree of the level.
				 *  
				 *  11/10/2014, Bing Li
				 *  
				 */
				return currentLevelNodeIndex * treeBranchCount + getCompleteTreeNodeCount(rootBranchCount, treeBranchCount, treeLevelResided);
			}
			else
			{
				// According to the algorithm, the resided tree level is equal to 0 only if the tree contains only one node, i.e., the root node. It does not make sense in practice. Therefore, if the level is not greater than 0, the leftmost node index of 0. 11/10/2014, Bing Li 
				return 0;
			}
		}
		else
		{
			// If the index of the current node is not greater than 0, it denotes it is the root. Then, its leftmost child index is 1. Return 1. 11/10/2014, Bing Li
			return 1;
		}
	}

	/*
	 * The method gets the leftmost index, firstChildIndex, of a node's children. 11/10/2014, Bing Li 
	 */
	private static int getFirstChildIndex(int currentNodeIndex, int rootBranchCount, Map<String, Integer> nodeCapacityMap)
	{
		// Check whether the index of the current node is greater than 0. 11/10/2014, Bing Li
		if (currentNodeIndex > 0)
		{
			// Get the level of the tree the current node resides. 11/10/2014, Bing Li
			int treeLevelResided = getTreeLevelResided(currentNodeIndex, rootBranchCount, nodeCapacityMap);
			// Check if the resided tree level is greater than 0. 11/10/2014, Bing Li
			if (treeLevelResided > 0)
			{
				// If the resided tree level is obtained, it is reasonable to get the node count of all of upper levels. 11/10/2014, Bing Li
				int upperLevelsNodeCount = getCompleteTreeNodeCount(nodeCapacityMap, rootBranchCount, treeLevelResided - 1);
				// After getting the node count of all of upper levels, it is reasonable to get the index of the current node is sorted on its own level. 11/10/2014, Bing Li
				int currentLevelNodeIndex = currentNodeIndex - upperLevelsNodeCount;
				// Then, get the count of nodes after the new level the current node resides has added. 11/10/2014, Bing Li
				int currentTreeNodeCount = getCompleteTreeNodeCount(nodeCapacityMap, rootBranchCount, treeLevelResided);
				// Declare the node index to retrieve the corresponding node key. 11/10/2014, Bing Li
				int nodeIndex;
				// If the current node happens to be the first one on its level, then the index of its leftmost child is equal to the count of nodes in all of the upper levels, including the one the current node resides. 11/10/2014, Bing Li
				int firstChildIndex = currentTreeNodeCount;
				// If the current node is not the first one on its level, which is the common case, then it is necessary to add the total capacities of the nodes before the current node on the same level. Initializing the following list is convenient to get the node key on the node index. 11/10/2014, Bing Li 
				List<String> nodeKeys = new LinkedList<String>(nodeCapacityMap.keySet());
				// Add the total capacities of the nodes before the current node on the same level is not the first one on the level. 11/10/2014, Bing Li
				for (int i = 0; i < currentLevelNodeIndex; i ++)
				{
					// Get the node index of the node before the current node on the same level. 11/10/2014, Bing Li
					nodeIndex = i + upperLevelsNodeCount - 1;
					// Check whether the node index is less than the node size. 11/10/2014, Bing Li
					if (nodeIndex < nodeCapacityMap.size())
					{
						// Add the capacity of one node that resides before the current node on the same level. 11/10/2014, Bing Li
						firstChildIndex += nodeCapacityMap.get(nodeKeys.get(nodeIndex));
					}
					else
					{
						// If the the node index is not less than the node size, it does not make sense. Just break here. 11/10/2014, Bing Li
						break;
					}
				}
				// Return the index of leftmost child of the current node. 11/10/2014, Bing Li
				return firstChildIndex;
			}
			else
			{
				// According to the algorithm, the resided tree level is equal to 0 only if the tree contains only one node, i.e., the root node. It does not make sense in practice. Therefore, if the level is not greater than 0, the leftmost node index of 0. 11/10/2014, Bing Li 
				return 0;
			}
		}
		else
		{
			// If the index of the current node is not greater than 0, it denotes it is the root. Then, its leftmost child index is 1. Return 1. 11/10/2014, Bing Li
			return 1;
		}
	}
	
	public static void printRootTree(Map<String, List<String>> tree, SyncRemoteEventer<ServerMessage> clientPool)
	{
		System.out.println("\n-------------------------------------------------------------------");
		System.out.println("The Root has " + clientPool.getClientSize() + " children to create a tree");
		for (Map.Entry<String, List<String>> entry : tree.entrySet())
		{
			if (!entry.getKey().equals(UtilConfig.ROOT_KEY))
			{
				System.out.println("->Parent: " + clientPool.getIPAddress(entry.getKey()));
			}
			else
			{
				System.out.println("->Parent: Root");
			}
			if (entry.getValue() != null)
			{
				for (String child : entry.getValue())
				{
					System.out.println("\t\tChild: " + clientPool.getIPAddress(child));
				}
			}
			else
			{
				System.out.println("\t\tChild: NULL");
			}
		}
		System.out.println("-------------------------------------------------------------------\n");
	}
	
	public static void printSubTree(String subRootIP, int subRootPort, Map<String, IPAddress> ips)
	{
		System.out.println("\n-------------------------------------------------------------------");
		System.out.println("Just received nodes from the parent ...");
		System.out.println("The sub root: " + subRootIP + ":" + subRootPort + " has " + ips.size() + " children to create a tree");
		for (IPAddress entry : ips.values())
		{
			System.out.println("\t\tChild: " + entry);
		}
		System.out.println("-------------------------------------------------------------------\n");
	}
	
	public static void printSubTree(String subRootIP, int subRootPort, Map<String, List<String>> tree, Map<String, IPAddress> ips)
	{
		System.out.println("\n-------------------------------------------------------------------");
		System.out.println("Tree constructed on the received nodes from the parent ...");
		System.out.println("The sub root: " + subRootIP + ":" + subRootPort + " has " + ips.size() + " children to create a tree");
		for (Map.Entry<String, List<String>> entry : tree.entrySet())
		{
			if (!entry.getKey().equals(UtilConfig.LOCAL_KEY))
			{
				System.out.println("->Parent: " + ips.get(entry.getKey()));
			}
			else
			{
				System.out.println("->Parent: Root");
			}
			if (entry.getValue() != null)
			{
				for (String child : entry.getValue())
				{
					System.out.println("\t\tChild: " + ips.get(child));
				}
			}
		}
		System.out.println("-------------------------------------------------------------------\n");
	}
}
