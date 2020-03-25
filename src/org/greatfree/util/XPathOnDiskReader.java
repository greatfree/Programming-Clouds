package org.greatfree.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

// Created: 09/10/2013, Bing Li
public class XPathOnDiskReader
{
	private String xmlResource;
	private Document xmlDocument;
	private XPath xPath;
	private DocumentBuilder xmlDocumentBuilder;
	private InputSource xmlInMemorySource;

	public XPathOnDiskReader(String xmlFile, boolean isXMLInMemory)
	{
		if (!isXMLInMemory)
		{
			this.xmlResource = xmlFile;
			this.initObjects();
		}
		else
		{
			try
			{
				this.xmlResource = FileManager.loadText(xmlFile);
				this.xmlResource = this.trim(this.xmlResource);
				this.initObjectsFromMemory();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void close()
	{
		this.xmlResource = null;
		this.xmlDocument = null;
		this.xmlDocumentBuilder = null;
		this.xmlInMemorySource = null;
		this.xPath = null;
	}
	
	public boolean reset(String xmlResource, boolean isXMLInMemory)
	{
		this.xmlResource = xmlResource;
		if (!isXMLInMemory)
		{
			return this.resetObjects();
		}
		else
		{
			this.xmlResource = this.trim(this.xmlResource);
			return this.resetObjectsFromMemory();
		}
//		this.isNew = false;
	}
	
	private boolean initObjects()
	{
		try
		{
			this.xmlDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.xmlDocument = this.xmlDocumentBuilder.parse(this.xmlResource);
			this.xPath = XPathFactory.newInstance().newXPath();
			return true;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			return false;
		}
		catch (SAXException ex)
		{
			ex.printStackTrace();
			return false;
		}
		catch (ParserConfigurationException ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	private boolean initObjectsFromMemory()
	{
		this.xmlInMemorySource = new InputSource();
		this.xmlInMemorySource.setCharacterStream(new StringReader(this.xmlResource));
		try
		{
			this.xmlDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			this.xmlDocument = this.xmlDocumentBuilder.parse(this.xmlInMemorySource);
			this.xPath =  XPathFactory.newInstance().newXPath();
			return true;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			return false;
		}
		catch (SAXException ex)
		{
			ex.printStackTrace();
			return false;
		}
		catch (ParserConfigurationException ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	private boolean resetObjects()
	{
		try
		{
			this.xmlDocument = this.xmlDocumentBuilder.parse(this.xmlResource);
			return true;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			return false;
		}
		catch (SAXException ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	private boolean resetObjectsFromMemory()
	{
		this.xmlInMemorySource.setCharacterStream(new StringReader(this.xmlResource));
		try
		{
			this.xmlDocument = this.xmlDocumentBuilder.parse(this.xmlInMemorySource);
			return true;
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
			return false;
		}
		catch (SAXException ex)
		{
			ex.printStackTrace();
			return false;
		}
	}

	public Object read(String expression, QName returnType)
	{
		try
		{
//			XPathExpression xPathExpression = this.xPath.compile(expression);
			return this.xPath.compile(expression).evaluate(this.xmlDocument, returnType);
		}
		catch (XPathExpressionException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	public String read(String expression)
	{
		try
		{
			return (String)this.xPath.compile(expression).evaluate(this.xmlDocument, XPathConstants.STRING);
		}
		catch (XPathExpressionException ex)
		{
			ex.printStackTrace();
			return UtilConfig.EMPTY_STRING;
		}
	}
	
	public NodeList readMany(String expression)
	{
		try
		{
			return (NodeList)this.xPath.compile(expression).evaluate(this.xmlDocument, XPathConstants.NODESET);
		}
		catch (XPathExpressionException ex)
		{
			ex.printStackTrace();
			return UtilConfig.NO_MULTI_RESULTS;
		}
	}
	
	public List<String> readStrings(String expression)
	{
		try
		{
			NodeList nodes = (NodeList)this.xPath.compile(expression).evaluate(this.xmlDocument, XPathConstants.NODESET);
			List<String> nodeList = new LinkedList<String>();
			for (int i = 0; i < nodes.getLength(); i ++)
			{
				nodeList.add(nodes.item(i).getNodeValue());
			}
			return nodeList;
		}
		catch (XPathExpressionException ex)
		{
			ex.printStackTrace();
			return UtilConfig.NO_STRINGS;
		}
	}

	/*
	public boolean IsNew()
	{
		return this.isNew;
	}
	*/
	
	private String trim(String xml)
	{
		return xml.trim().replaceFirst(UtilConfig.TRIM_EXPRESSION, UtilConfig.LESS_THAN);
	}
}
