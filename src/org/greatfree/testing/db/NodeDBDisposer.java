package org.greatfree.testing.db;

import org.greatfree.reuse.Disposable;

/*
 * A disposer to dispose instances of NodeDB. 11/04/2014, Bing Li
 */

// Created: 11/04/2014, Bing Li
public class NodeDBDisposer implements Disposable<NodeDB>
{
	@Override
	public void dispose(NodeDB rsc)
	{
		rsc.dispose();
	}
}
