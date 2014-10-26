package kr.ac.korea.mobide.sigma.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import kr.ac.korea.mobide.sigma.common.ScoreData;
import kr.ac.korea.mobide.sigma.vsm.Document;
import kr.ac.korea.mobide.sigma.vsm.InvertedIndex;
import kr.ac.korea.mobide.sigma.vsm.Vector;
import kr.ac.korea.mobide.sigma.vsm.VectorGenerator;
import kr.ac.korea.mobide.sigma.vsm.VectorSpaceModel;

/**
 * An abstract class for SigmaClassifier. It returns top-k classification result
 * given an arbitrary string.
 * 
 * @author okcomputer
 *
 */
public abstract class CentroidClassifier implements VectorGenerator, InvertedIndex {
	/**
	 * Returns category name specified by the ID of a category
	 * @param cid ID of a category
	 * @return category name
	 */
	public abstract String getCategoryName(int cid);
	
	/**
	 * Returns ID of a category specified by its name
	 * @param cname Category name
	 * @return ID of a category
	 */
	public abstract int getCategoryID(String cname);
	
	/**
	 * Returns a classification result for all categories given a query
	 * @param query A query text
	 * @return A sorted ArrayList of ScoreData object
	 */
	public ArrayList<ScoreData> classify(String query) {
		HashMap<Integer, Double> mapCIDScore = this.getMapCIDScore(query);
		return this.getSortedListClearMap(mapCIDScore);
	}
	
	/**
	 * Given a query text, it returns a classification result for all categories except 
	 * sub-categories of Top/Regional if exceptRegion is set to true.
	 * @param query A query text
	 * @param exceptRegional Specifies whether sub-categories of Top/Regional is included
	 * in the result or not
	 * @return A sorted ArrayList of ScoreData object
	 */
	public ArrayList<ScoreData> classify(String query, boolean exceptRegional) {
		HashMap<Integer, Double> mapCIDScore = this.getMapCIDScore(query);
		return this.getSortedListClearMap(mapCIDScore, exceptRegional);
	}
	
	public ArrayList<ScoreData> classify(HashMap<String, String> mapSectionContent) {
		HashMap<Integer, Double> mapCIDScore = this.getMapCIDScore(mapSectionContent);
		return this.getSortedListClearMap(mapCIDScore);
	}
	
	public ArrayList<ScoreData> classify(HashMap<String, String> mapSectionContent, boolean exceptRegional) {
		HashMap<Integer, Double> mapCIDScore = this.getMapCIDScore(mapSectionContent);
		return this.getSortedListClearMap(mapCIDScore, exceptRegional);
	}
	
	/**
	 * Returns top-k classification result given a query text.
	 * @param k Specifies top-k categories 
	 * @param query A query text
	 * @return A sorted ArrayList of ScoreData object whose size is at most k
	 */
	public ArrayList<ScoreData> topK(int k, String query) {
		ArrayList<ScoreData> listScore = this.classify(query);
		return this.getTopKListClearSortedList(k, listScore);
	}
	
	/**
	 * Given a query text, it returns top-k classification result in which sub-categories
	 * of Top/Regional are not included if exceptRegional is set to true. Otherwise, the
	 * result is identical to that of topK(k, query) method.
	 * @param k Specifies top-k categories
	 * @param query A query text
	 * @param exceptRegional Specifies whether except 'Top/Regional'
	 * @return A sorted ArrayList of ScoreData object whose size is at most k
	 */
	public ArrayList<ScoreData> topK(int k, String query, boolean exceptRegional) {
		ArrayList<ScoreData> listScore = this.classify(query, exceptRegional);
		return this.getTopKListClearSortedList(k, listScore);
	}
	
	public ArrayList<ScoreData> topK(int k, HashMap<String, String> mapSectionContent) {
		ArrayList<ScoreData> listScore = this.classify(mapSectionContent);
		return this.getTopKListClearSortedList(k, listScore);
	}
	
	public ArrayList<ScoreData> topK(int k, HashMap<String, String> mapSectionContent, boolean exceptRegional) {
		ArrayList<ScoreData> listScore = this.classify(mapSectionContent, exceptRegional);
		return this.getTopKListClearSortedList(k, listScore);
	}
	
	private HashMap<Integer, Double> getMapCIDScore(String query) {
		Vector queryVector = VectorSpaceModel.getVector(query, Document.SECTION_PLAIN, this);
		return VectorSpaceModel.getMapIDCosine(queryVector, this);
	}
	
	private HashMap<Integer, Double> getMapCIDScore(HashMap<String, String> mapSectionContent) {
		Vector queryVector = VectorSpaceModel.getVector(mapSectionContent, this);
		return VectorSpaceModel.getMapIDCosine(queryVector, this);
	}
	
	private ArrayList<ScoreData> getSortedListClearMap(HashMap<Integer, Double> mapCIDScore) {
		ArrayList<ScoreData> listScore = new ArrayList<ScoreData>();
		for (int cid : mapCIDScore.keySet()) {
			listScore.add(new ScoreData(cid, mapCIDScore.get(cid)));
		}
		mapCIDScore.clear();
		Collections.sort(listScore);
		return listScore;
	}
	
	private ArrayList<ScoreData> getSortedListClearMap(HashMap<Integer, Double> mapCIDScore, boolean exceptRegional) {
		if (!exceptRegional) {
			return this.getSortedListClearMap(mapCIDScore);
		} else {
			ArrayList<ScoreData> listScore = new ArrayList<ScoreData>();
			for (int cid : mapCIDScore.keySet()) {
				String cname = this.getCategoryName(cid);
				if (!cname.startsWith("Top/Regional")) {
					listScore.add(new ScoreData(cid, mapCIDScore.get(cid)));
				}
			}
			mapCIDScore.clear();
			Collections.sort(listScore);
			return listScore;
		}
	}
	
	private ArrayList<ScoreData> getTopKListClearSortedList(int k, ArrayList<ScoreData> listScore) {
		if (k >= listScore.size()) {
			return listScore;
		} else {
			ArrayList<ScoreData> listTopK = new ArrayList<ScoreData>();
			for (int index = 0; index < k ; index++) {
				listTopK.add(listScore.get(index));
			}
			listScore.clear();
			return listTopK;
		}
	}
	
	public static final String	FILE_MAP_TERM_TERMID	= "term_termid.map";
	public static final String	FILE_MAP_TERMID_DF		= "termid_df.map";
	public static final String	FILE_MAP_TERMID_POSTING	= "termid_posting.map";
	public static final String	FILE_MAP_CID_CNAME		= "cid_cname.map";
	public static final String	FILE_MAP_CID_SCALAR		= "cid_scalar.map";
	public static final String	FILE_DB_CLASSIFIER		= "classifier.db";
}
