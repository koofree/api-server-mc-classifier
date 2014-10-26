package kr.ac.korea.mobide.sigma.engine.map;

import java.util.ArrayList;
import java.util.HashMap;

import kr.ac.korea.mobide.sigma.common.ScoreData;
import kr.ac.korea.mobide.sigma.engine.QueryAnalyzer;

/**
 * A subclass of QueryAnalyzer
 * 
 * @author okcomputer
 *
 */
public class QueryAnalyzerMap extends QueryAnalyzer {
	/**
	 * Constructs a QueryAnalyzer object.
	 */
	public QueryAnalyzerMap() {
		this.mapSectionListContent = new HashMap<String, ArrayList<String>>();
		this.listScore = new ArrayList<ScoreData>(0);
	}
	
	@Override
	public void addQuery(String content, String section) {
		if (this.mapSectionListContent.containsKey(section)) {
			this.mapSectionListContent.get(section).add(content);
		} else {
			ArrayList<String> listContent = new ArrayList<String>();
			listContent.add(content);
			this.mapSectionListContent.put(section, listContent);
		}
	}

	@Override
	protected HashMap<String, ArrayList<String>> getMapSectionListContent() {
		return this.mapSectionListContent;
	}
	
	@Override
	protected void setClassificationResult(ArrayList<ScoreData> listScore) {
		this.listScore = listScore;
	}

	@Override
	protected ArrayList<ScoreData> getClassificationResult() {
		return this.listScore;
	}

	private HashMap<String, ArrayList<String>> mapSectionListContent;
	private ArrayList<ScoreData> listScore;
}
