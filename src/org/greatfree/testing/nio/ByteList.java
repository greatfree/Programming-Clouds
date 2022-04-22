package org.greatfree.testing.nio;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Bing Li
 * 
 * 02/01/2022, Bing Li
 *
 */
class ByteList
{
	private List<byte[]> bytes;
	private int totalLength;
	
	public ByteList()
	{
		this.bytes = new ArrayList<byte[]>();
		this.totalLength = 0;
	}

	public synchronized void add(byte[] objBytes)
	{
		this.bytes.add(objBytes);
		this.totalLength += objBytes.length;
	}

	public synchronized byte[] getAllBytes()
	{
		ByteBuffer buffer = ByteBuffer.allocate(this.totalLength);
		for (int i = 0; i < this.bytes.size(); i++)
		{
			buffer.put(this.bytes.get(i));
		}
		return buffer.array();
	}
}
