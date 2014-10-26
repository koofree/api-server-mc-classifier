package kr.ac.korea.mobide.sigma.engine;

import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.korea.mobide.sigma.classifier.CentroidClassifier;
import kr.ac.korea.mobide.sigma.common.ScoreData;
import kr.ac.korea.mobide.sigma.vsm.Document;
import kr.ac.korea.mobide.sigma.vsm.Vector;
import kr.ac.korea.mobide.sigma.vsm.VectorGenerator;
import kr.ac.korea.mobide.sigma.vsm.VectorSpaceModel;

/**
 * An analyzer that stores query text. Use addQuery() method to add text.
 * 
 * @author okcomputer
 * 
 */
public abstract class QueryAnalyzer {
	protected QueryAnalyzer() {
		this.isNotAnalyzed = true;
	}
	
	/**
	 * Adds query text given a string. If it is called several times, each string is appended
	 * with the previously appended query text.
	 * @param content A text to be added
	 */
	public void addQuery(String content) {
		this.addQuery(content, Document.SECTION_PLAIN);
	}
	
	/**
	 * Returns top-k classification result using the query text added by addQuery() method.
	 * @param k Specified top-k category
	 * @param classifier A CentroidClassifier object
	 * @return A sorted ArrayList<ScoreData> whose size is at most k.
	 */
	public ArrayList<ScoreData> topKCategory(int k, CentroidClassifier classifier) {
		if (this.isNotAnalyzed || k == ItemRanker.DEFAULT_TOPK_CATEGORY) {
			return classifier.topK(k, this.getMapSectionContent());
		} else {
			return this.getClassificationResult();
		}
	}
	
	protected void analyze(CentroidClassifier classifier) {
		if (this.isNotAnalyzed) {
			ArrayList<ScoreData> listScore = classifier.topK(ItemRanker.DEFAULT_TOPK_CATEGORY, this.getMapSectionContent());
			this.setClassificationResult(listScore);
			this.isNotAnalyzed = false;
		}
	}
	
	protected abstract void addQuery(String content, String section);
	
	protected abstract void setClassificationResult(ArrayList<ScoreData> listScore);
	
	protected abstract ArrayList<ScoreData> getClassificationResult();
	
	protected abstract HashMap<String, ArrayList<String>> getMapSectionListContent();
	
	protected Vector getVector(VectorGenerator generator) {
		return VectorSpaceModel.getVector(this.getMapSectionContent(), generator);
	}
	
	protected HashMap<String, String> getMapSectionContent() {
		HashMap<String, String> mapSectionContent = new HashMap<String, String>();
		HashMap<String, ArrayList<String>> mapSectionListContent = this.getMapSectionListContent();
		for (String section : mapSectionListContent.keySet()) {
			StringBuffer buffer = new StringBuffer("");
			for (String content : mapSectionListContent.get(section)) {
				buffer.append(content.concat(" "));
			}
			mapSectionContent.put(section, new String(buffer));
		}
		return mapSectionContent;
	}

	protected boolean isNotAnalyzed;
	
	protected static final String[]	SQL_CREATE_DB_QUERY_ANALYZER	= {"BEGIN TRANSACTION;",
																   	   "DROP TABLE IF EXISTS android_metadata;",
																   	   "DROP TABLE IF EXISTS content;",
																   	   "DROP TABLE IF EXISTS category;",
																   	   "CREATE TABLE android_metadata (locale TEXT);",
																   	   "INSERT INTO android_metadata VALUES('en_US');",
																   	   "CREATE TABLE content (section text, content text);",
																   	   "CREATE TABLE category (cid integer, score real);",
																   	   "CREATE INDEX [IDX_CONTENT_SECTION] ON [content]([section] ASC);",
																   	   "COMMIT;"};
}
