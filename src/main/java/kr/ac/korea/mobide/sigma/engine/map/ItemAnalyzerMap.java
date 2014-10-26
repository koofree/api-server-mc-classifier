package kr.ac.korea.mobide.sigma.engine.map;

import java.util.HashMap;

import kr.ac.korea.mobide.sigma.engine.ItemAnalyzer;

/**
 * A subclass of ItemAnalyzer
 * 
 * @author okcomputer
 *
 */
public class ItemAnalyzerMap extends ItemAnalyzer {
	/**
	 * Constructs an ItemAnalyzer object
	 */
	public ItemAnalyzerMap() {
		super();
		this.mapItemIDSectionContent = new HashMap<Integer, HashMap<String, String>>();
		this.mapTermTermID = new HashMap<String, Integer>();
		this.mapTermIDDF = new HashMap<Integer, Integer>();
		this.numDoc = 0;
		this.mapTermIDPosting = new HashMap<Integer, HashMap<Integer, Double>>();
		this.mapItemIDScalar = new HashMap<Integer, Double>();
		this.mapCIDItemIDScore = new HashMap<Integer, HashMap<Integer, Double>>();
	}
	
	@Override
	public int getTermID(String term) {
		int termID = -1;
		if (term == null) return termID;
		if (this.mapTermTermID.containsKey(term)) {
			termID = this.mapTermTermID.get(term);
		}
		return termID;
	}

	@Override
	public int getDF(int termID) {
		int df = 1;
		if (this.mapTermIDDF.containsKey(termID)) {
			df = this.mapTermIDDF.get(termID);
		}
		return df;
	}

	@Override
	public int getNumDoc() {
		return this.numDoc;
	}

	@Override
	public HashMap<Integer, Double> getPosting(int termID) {
		return this.mapTermIDPosting.get(termID);
	}

	@Override
	public double getScalar(int itemID) {
		return this.mapItemIDScalar.get(itemID);
	}

	@Override
	public HashMap<Integer, Double> getMapItemIDScore(int cid) {
		HashMap<Integer, Double> mapItemIDScore = new HashMap<Integer, Double>();
		if (this.mapCIDItemIDScore.containsKey(cid)) {
			mapItemIDScore = this.mapCIDItemIDScore.get(cid);
		}
		return mapItemIDScore;
	}
	
	@Override
	protected void addItem(int itemID, String content, String section) {
		super.addItem(itemID, content, section);
		if (this.mapItemIDSectionContent.containsKey(itemID)) {
			HashMap<String, String> mapSectionContent = this.mapItemIDSectionContent.get(itemID);
			if (mapSectionContent.containsKey(section)) {
				mapSectionContent.put(section, mapSectionContent.get(section).concat(" "+content));
			} else {
				mapSectionContent.put(section, content);
			}
		} else {
			HashMap<String, String> mapSectionContent = new HashMap<String, String>();
			mapSectionContent.put(section, content);
			this.mapItemIDSectionContent.put(itemID, mapSectionContent);
		}
	}
	
	protected String getSectionContent(int itemID, String section) {
		String content = "";
		if (this.mapItemIDSectionContent.containsKey(itemID)) {
			HashMap<String, String> mapSectionContent = this.mapItemIDSectionContent.get(itemID);
			if (mapSectionContent.containsKey(section)) {
				content = mapSectionContent.get(section);
			}
		}
		return content;
	}
	
	@Override
	protected void setTermTermIDDF(String term, int termID, int df) {
		this.mapTermTermID.put(new String(term), termID);
		this.mapTermIDDF.put(termID, df);
	}

	@Override
	protected void setNumDoc(int numDoc) {
		this.numDoc = numDoc;
	}

	@Override
	protected void setTermIDPosting(int termID, int itemID, double weight) {
		if (this.mapTermIDPosting.containsKey(termID)) {
			HashMap<Integer, Double> mapPosting = this.mapTermIDPosting.get(termID);
			mapPosting.put(itemID, weight);
		} else {
			HashMap<Integer, Double> mapPosting = new HashMap<Integer, Double>();
			mapPosting.put(itemID, weight);
			this.mapTermIDPosting.put(termID, mapPosting);
		}
	}

	@Override
	protected void setItemIDScalar(int itemID, double scalar) {
		this.mapItemIDScalar.put(itemID, scalar);
	}

	@Override
	protected void setCIDItemIDScore(int cid, int itemID, double score) {
		if (this.mapCIDItemIDScore.containsKey(cid)) {
			HashMap<Integer, Double> mapItemIDScore = this.mapCIDItemIDScore.get(cid);
			mapItemIDScore.put(itemID, score);
		} else {
			HashMap<Integer, Double> mapItemIDScore = new HashMap<Integer, Double>();
			mapItemIDScore.put(itemID, score);
			this.mapCIDItemIDScore.put(cid, mapItemIDScore);
		}
	}

	@Override
	protected HashMap<String, String> getMapSectionContent(int itemID) {
		if (this.mapItemIDSectionContent.containsKey(itemID)) {
			return this.mapItemIDSectionContent.get(itemID);
		} else {
			return new HashMap<String, String>(0);
		}
	}
	
	private HashMap<Integer, HashMap<String, String>> mapItemIDSectionContent;
	private HashMap<String, Integer> mapTermTermID;
	private HashMap<Integer, Integer> mapTermIDDF;
	private int numDoc;
	private HashMap<Integer, HashMap<Integer, Double>> mapTermIDPosting;
	private HashMap<Integer, Double> mapItemIDScalar;
	private HashMap<Integer, HashMap<Integer, Double>> mapCIDItemIDScore;
}
