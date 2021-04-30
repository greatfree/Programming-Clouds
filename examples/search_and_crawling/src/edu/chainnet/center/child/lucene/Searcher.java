package edu.chainnet.center.child.lucene;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import edu.chainnet.center.CenterConfig;

// Created: 04/27/2021, Bing Li
public class Searcher
{
	private final static Logger log = Logger.getLogger("edu.chainnet.datacenter.child.lucene");

//	public static void perform(String indexPath, String field, String keyword, int repeat, int hitsPerPage) throws IOException, ParseException
	public static List<String> perform(String docPath, String indexPath, String keyword, int hitsPerPage) throws IOException, ParseException
	{
		IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
		IndexSearcher searcher = new IndexSearcher(reader);
		Analyzer analyzer = new IKAnalyzer();
		QueryParser parser = new QueryParser(CenterConfig.CONTENT_FIELD, analyzer);
		Query query = parser.parse(keyword);
		/*
		if (repeat > 0)
		{ // repeat & time as benchmark
			Date start = new Date();
			for (int i = 0; i < repeat; i++)
			{
				searcher.search(query, 100);
			}
			Date end = new Date();
			log.info("Time: " + (end.getTime() - start.getTime()) + "ms");
		}
		*/
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
		List<String> results = searchByPage(in, searcher, query, hitsPerPage, docPath);
		reader.close();
		return results;
	}

	private static List<String> searchByPage(BufferedReader in, IndexSearcher searcher, Query query, int hitsPerPage, String docPath) throws IOException
	{
		// Collect enough docs to show 5 pages
		TopDocs results = searcher.search(query, 5 * hitsPerPage);
		
		ScoreDoc[] hits = results.scoreDocs;

		Document targetDoc;
		List<String> titles = new ArrayList<String>();
		String title;
		for (int i = 0; i < results.totalHits.value; i++)
		{
			targetDoc = searcher.doc(hits[i].doc);
//			System.out.println("Content: " + targetDoc.toString());
			title = targetDoc.get(CenterConfig.PATH_FIELD);
			title = title.substring(title.indexOf(docPath) + docPath.length());
			titles.add(title);
		}

		int numTotalHits = Math.toIntExact(results.totalHits.value);
		log.info(numTotalHits + " total matching documents");
		return titles;
	}
}

