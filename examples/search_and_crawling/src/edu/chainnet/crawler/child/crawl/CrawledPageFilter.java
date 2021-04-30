package edu.chainnet.crawler.child.crawl;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.mama.util.FreeUtilConfig;
import edu.chainnet.crawler.CrawlConfig;

// Created: 04/24/2021, Bing Li
class CrawledPageFilter
{
	private HashSet<String> filterCharacterSet;
	private HashSet<String> linefeedSet;
	private Pattern chinesePattern;
	private String[] delimiterArray;

	private CrawledPageFilter()
	{
		this.filterCharacterSet = new HashSet<String>();
		this.filterCharacterSet.add(FilterCharacters.W_BRACE);
		this.filterCharacterSet.add(FilterCharacters.HTML_STR);
		this.filterCharacterSet.add(FilterCharacters.ELSE_BRACE);
		this.filterCharacterSet.add(FilterCharacters.COMPACT_ELSE_BRACE);
		this.filterCharacterSet.add(FilterCharacters.IF_BRACE);
		this.filterCharacterSet.add(FilterCharacters.VOID_BRACE);
		this.filterCharacterSet.add(FilterCharacters.NAVIGATOR_USER_AGENT);
		this.filterCharacterSet.add(FilterCharacters.BRACE_HTML);
		this.filterCharacterSet.add(FilterCharacters.ASMALL_FONT);
		this.filterCharacterSet.add(FilterCharacters.FUNCTION_MOUSE);
		this.filterCharacterSet.add(FilterCharacters.DO_POST_BACK);
		this.filterCharacterSet.add(FilterCharacters.GET_ELEMENT_BY_TAG_NAME);
		this.filterCharacterSet.add(FilterCharacters.JQUERY);
		this.filterCharacterSet.add(FilterCharacters.LOAD_BRACE);
		this.filterCharacterSet.add(FilterCharacters.HTML_BRACE);
		this.filterCharacterSet.add(FilterCharacters.EL_INNER_HTML);
		this.filterCharacterSet.add(FilterCharacters.INDEX_HTML);
		this.filterCharacterSet.add(FilterCharacters.HTTPS);
		this.filterCharacterSet.add(FilterCharacters.OPEN_WIN);
		this.filterCharacterSet.add(FilterCharacters.DOCUMENT_WRITE);
		this.filterCharacterSet.add(FilterCharacters.DOCUMENT_GETELEMENT);
		this.filterCharacterSet.add(FilterCharacters.RETURN_HTML);
		this.filterCharacterSet.add(FilterCharacters.OPEN_NEW_WIN);
		this.filterCharacterSet.add(FilterCharacters.QVT_TEMP);

		this.linefeedSet = new HashSet<String>();
		this.linefeedSet.add(FilterCharacters.LINE_FEED_1);
		this.linefeedSet.add(FilterCharacters.LINE_FEED_2);
		this.linefeedSet.add(FilterCharacters.LINE_FEED_3);

		this.chinesePattern = Pattern.compile(FreeUtilConfig.CHINESE_STRING_REGULAR_EXPRSSION);
	}

	private static CrawledPageFilter instance = new CrawledPageFilter();

	public static CrawledPageFilter FREE()
	{
		if (instance == null)
		{
			instance = new CrawledPageFilter();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public boolean isTrashAuthorityTitle(String authorityTitle)
	{
		Matcher chineseMatcher = this.chinesePattern.matcher(authorityTitle);
		if (chineseMatcher.find())
		{
			if (authorityTitle.length() <= CrawlConfig.MIN_CHINESE_AUTHORITY_TITLE_LENGTH)
			{
				return true;
			}
		}
		else
		{
			this.delimiterArray = authorityTitle.split(CrawlConfig.SPACE_WORD_DELIMITER);
			if (this.delimiterArray.length <= CrawlConfig.MIN_ENGLISH_AUTHORITY_TITLE_LENGTH)
			{
				return true;
			}
		}
		Iterator<String> filterCharacterIterator = this.filterCharacterSet.iterator();
		String filterChar;
		while (filterCharacterIterator.hasNext())
		{
			filterChar = filterCharacterIterator.next();
			if (authorityTitle.indexOf(filterChar) > 0)
			{
				return true;
			}
		}
		return false;
	}
	
	public String polishTitle(String title)
	{
		title = this.normalize(title);
		title = this.replaceLineFeed(title);
		title = this.stripNonCharacter(title);
		return title;
	}
	
	public String polishURL(String url)
	{
		url = this.normalize(url);
		url = this.stripNonCharacter(url);
		return url;
	}

	private String normalize(String authorityTitle)
	{
		// return Normalizer.normalize(authorityTitle, Normalizer.Form.NFD);
		return Normalizer.normalize(authorityTitle, Normalizer.Form.NFKD);
	}

//	public String replaceLineFeed(String authorityTitle)
	private String replaceLineFeed(String authorityTitle)
	{
		int firstIndex;
		int lastIndex;

		Iterator<String> linefeedIterator = this.linefeedSet.iterator();
		String linefeed;
		while (linefeedIterator.hasNext())
		{
			linefeed = linefeedIterator.next();
			firstIndex = authorityTitle.indexOf(linefeed);
			lastIndex = authorityTitle.lastIndexOf(linefeed);
			if (firstIndex >= 0)
			{
				if (firstIndex == lastIndex)
				{
					if (firstIndex == 0)
					{
						authorityTitle = authorityTitle.replace(linefeed, FilterCharacters.EMPTY_STRING);
					}
					else
					{
						if (lastIndex == authorityTitle.length() - linefeed.length())
						{
							authorityTitle = authorityTitle.replace(linefeed, FilterCharacters.EMPTY_STRING);
						}
						else
						{
							authorityTitle = authorityTitle.replace(linefeed, FilterCharacters.COMMA);
						}
					}
				}
				else
				{
					if (firstIndex == 0)
					{
						authorityTitle = authorityTitle.replace(linefeed, FilterCharacters.EMPTY_STRING);
					}
					if (lastIndex == authorityTitle.length() - linefeed.length())
					{
						authorityTitle = authorityTitle.substring(0, lastIndex - linefeed.length());
					}
					authorityTitle = authorityTitle.replaceAll(linefeed, FilterCharacters.COMMA);
				}
			}
		}
		return authorityTitle;
	}

	private String stripNonCharacter(String input)
	{
		StringBuilder stripped = new StringBuilder(input.length());
		char ch;
		for (int i = 0; i < input.length(); i++)
		{
			ch = input.charAt(i);
			if (ch % 0x10000 != 0xffff && // 0xffff - 0x10ffff range step 0x10000
					ch % 0x10000 != 0xfffe && // 0xfffe - 0x10fffe range
					(ch <= 0xfdd0 || ch >= 0xfdef) && // 0xfdd0 - 0xfdef
					(ch > 0x1F || ch == 0x9 || ch == 0xa || ch == 0xd))
			{
				stripped.append(ch);
			}
		}
		return stripped.toString();
	}
}
