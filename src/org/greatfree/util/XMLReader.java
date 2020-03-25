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

/*
 * This is a tool to read information from an XML, which is usually managed by administrators manually. 11/25/2014, Bing Li
 */

// Created: 11/25/2014, Bing Li
public class XMLReader
{
	private String xmlResource;
	private Document xmlDocument;
	private XPath xPath;
	private DocumentBuilder xmlDocumentBuilder;
	private InputSource xmlInMemorySource;

	/*
	 * Initialize. 11/25/2014, Bing Li
	 */
	public XMLReader(String xmlFile, boolean isXMLInMemory)
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

	/*
	 * Close the reader. 11/25/2014, Bing Li
	 */
	public void close()
	{
		this.xmlResource = null;
		this.xmlDocument = null;
		this.xmlDocumentBuilder = null;
		this.xmlInMemorySource = null;
		this.xPath = null;
	}

	/*
	 * Reset the XML to be parsed. 11/25/2014, Bing Li
	 */
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
	}

	/*
	 * Parse the objects upon XML on the disk. 11/25/2014, Bing Li
	 */
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
	
	/*
	 * Parse the objects upon XML in the memory. 11/25/2014, Bing Li
	 */
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

	/*
	 * Parse again from the XML on the disk. 11/25/2014, Bing Li
	 */
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
	
	/*
	 * Parse again from the XML in the memory. 11/25/2014, Bing Li
	 */
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

	/*
	 * Read an object from the XML. 11/25/2014, Bing Li
	 */
	public Object read(String expression, QName returnType)
	{
		try
		{
			return this.xPath.compile(expression).evaluate(this.xmlDocument, returnType);
		}
		catch (XPathExpressionException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	/*
	 * Read a value encoded in the form of ASCII from the XML. 11/25/2014, Bing Li
	 */
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

	/*
	 * Retrieve information from the XML. Since the result must not be a unique, it is necessary to save them into a list. 11/25/2014, Bing Li
	 */
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

	/*
	 * Retrieve information from the XML. Since the result must not be a unique, it is necessary to save them in the encoded ASCII into a list. 11/25/2014, Bing Li
	 */
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
	 * Remove the format of the XML. 11/25/2014, Bing Li
	 */
	private String trim(String xml)
	{
		return xml.trim().replaceFirst(UtilConfig.TRIM_EXPRESSION, UtilConfig.LESS_THAN);
	}
}
