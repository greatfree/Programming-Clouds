package edu.chainnet.center.child.lucene;

// Created: 04/26/2021, Bing Li
class FilePathReadOnlyException extends Exception
{
	private static final long serialVersionUID = -4834094355856135489L;
	
	private String filePath;
	
	public FilePathReadOnlyException(String fp)
	{
		this.filePath = fp;
	}
	
	public String getFilePath()
	{
		return this.filePath;
	}

}
