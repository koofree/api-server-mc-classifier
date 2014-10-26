package kr.ac.korea.mobide.sigma.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import kr.ac.korea.mobide.sigma.classifier.CentroidClassifier;
import kr.ac.korea.mobide.sigma.common.ScoreData;
import kr.ac.korea.mobide.sigma.vsm.Document;
import kr.ac.korea.mobide.sigma.vsm.InvertedIndex;
import kr.ac.korea.mobide.sigma.vsm.VectorGenerator;
import kr.ac.korea.mobide.sigma.vsm.VectorSpaceModel;

/**
 * An analyzer that stores a set of items to be ranked by ItemRanker.
 * Use addTitle(), addCategory(), and addDescription() methods to add items. Each item is
 * specified by its unique itemID. 
 * After ranking, use getTitle(), getCategory(), and getDescription() methods to refer to
 * the items in the result.
 * 
 * @author okcomputer
 * 
 */
public abstract class ItemAnalyzer implements VectorGenerator, InvertedIndex, GraphScoreIndex {
	protected ItemAnalyzer() {
		this.model = new VectorSpaceModel();
		this.setItemID = new HashSet<Integer>();
		this.isNotIndexed = true;
		this.isNotAnalyzed = true;
	}
	
	/**
	 * Adds text content of an item specified by itemID. If it is called several times with the
	 * same itemID, each text is appended with the previously appended text.
	 * @param itemID
	 * @param content
	 */
	public void addItem(int itemID, String content) {
		this.addItem(itemID, content, Document.SECTION_PLAIN);
	}
	
	/**
	 * Returns HashSet<<Integer> that contains a set of itemID added by
	 * addTitle(int, String), addCategory(int, String), and addDescription(int, String) method
	 * @return
	 */
	public HashSet<Integer> getSetItemID() {
		return this.setItemID;
	}
	
	/**
	 * Returns a string as the content of an item specified by itemID.
	 * @param itemID ID of an item
	 * @return A string that contains all previously appended text by addItem(int, String) method
	 */
	public String getItem(int itemID) {
		HashMap<String, String> mapSectionContent = this.getMapSectionContent(itemID);
		StringBuffer buffer = new StringBuffer("");
		for (String content : mapSectionContent.values()) {
			buffer.append(content);
		}
		return new String(buffer);
	}
	
	/**
	 * Returns top-k classification result of an item specified by itemID.
	 * @param itemID ID of an item
	 * @param k Number of top-ranked classification result
	 * @param classifier A CentroidClassifier object
	 * @return A sorted ArrayList<ScoreData> object that contains categoryID along with the
	 * classification score
	 */
	public ArrayList<ScoreData> topKCategory(int itemID, int k, CentroidClassifier classifier) {
		return classifier.topK(k, this.getMapSectionContent(itemID));
	}
	
	protected void addItem(int itemID, String content, String section) {
		this.model.addDocument(itemID, content, section);
		this.setItemID.add(itemID);
	}
	
	/**
	 * Adds title text of an item specified by itemID. If it is called several times with the
	 * same itemID, each text is appended with the previously added title. Title text has more
	 * term weights compared to category and description.
	 * @param itemID ID of an item
	 * @param content A text to be appended in the title of the item
	 */
	protected void addTitle(int itemID, String content) {
		this.addItem(itemID, content, Document.SECTION_ITEM_TITLE);
	}
	
	/**
	 * Adds category of an item specified by itemID. If it is called several times with the 
	 * same itemID, each text is appended with the previously added category.
	 * @param itemID ID of an item
	 * @param content A text to be appended in the category of the item
	 */
	protected void addCategory(int itemID, String content) {
		this.addItem(itemID, content, Document.SECTION_ITEM_CATEGORY);
	}
	
	/**
	 * Adds description of an item specified by itemID. If it is called several times with the
	 * same itemID, each text is appended with the previously added description.
	 * @param itemID ID of an item
	 * @param content A text to be appended in the description of the item
	 */
	protected void addDescription(int itemID, String content) {
		this.addItem(itemID, content, Document.SECTION_ITEM_DESCRIPTION);
	}
	
	protected abstract String getSectionContent(int itemID, String section);
	
	/**
	 * Returns title text of an item specified by itemID.
	 * @param itemID ID of an item
	 * @return The title text of an item
	 */
	protected String getTitle(int itemID) {
		return this.getSectionContent(itemID, Document.SECTION_ITEM_TITLE);
	}
	
	/**
	 * Returns category text of an item specified by itemID.
	 * @param itemID ID of an item
	 * @return The category text of an item
	 */
	protected String getCategory(int itemID) {
		return this.getSectionContent(itemID, Document.SECTION_ITEM_CATEGORY);
	}
	
	/**
	 * Returns description text of an item specified by itemID.
	 * @param itemID ID of an item
	 * @return The description text of an item
	 */
	protected String getDescription(int itemID) {
		return this.getSectionContent(itemID, Document.SECTION_ITEM_DESCRIPTION);
	}
	
	protected void index() {
		if (this.isNotIndexed) {
			this.setVectorGenerator();
			this.setInvertedIndex();
			this.isNotIndexed = false;
		}
	}
	
	protected void analyze(CentroidClassifier classifier) {
		if (this.isNotAnalyzed) {
			this.setGraphScoreIndex(classifier);
			this.isNotAnalyzed = false;
		}
	}
	
	protected abstract void setTermTermIDDF(String term, int termID, int df);
	
	protected abstract void setNumDoc(int numDoc);
	
	protected abstract void setTermIDPosting(int termID, int itemID, double weight);
	
	protected abstract void setItemIDScalar(int itemID, double scalar);
	
	protected abstract void setCIDItemIDScore(int cid, int itemID, double score);
	
	protected abstract HashMap<String, String> getMapSectionContent(int itemID);
		
	protected void setVectorGenerator() {
		this.model.setMapDocIDVector();
		this.setItemID.addAll(this.model.getMapDocIDVector().keySet());
		for (String term : this.model.getMapTermTermID().keySet()) {
			int termID = this.model.getTermID(term);
			int df = this.model.getDF(termID);
			this.setTermTermIDDF(term, termID, df);
		}
		this.setNumDoc(this.model.getNumDoc());
	}

	protected void setInvertedIndex() {
		HashMap<Integer, HashMap<Integer, Double>> mapTermIDPosting = VectorSpaceModel.getMapTermIDPosting(this.model.getMapDocIDVector());
		for (int termID : mapTermIDPosting.keySet()) {
			HashMap<Integer, Double> mapPosting = mapTermIDPosting.get(termID);
			for (int itemID : mapPosting.keySet()) {
				double weight = mapPosting.get(itemID);
				this.setTermIDPosting(termID, itemID, weight);
			}
		}
		HashMap<Integer, Double> mapItemIDScalar = VectorSpaceModel.getMapIDScalar(this.model.getMapDocIDVector());
		for (int itemID : mapItemIDScalar.keySet()) {
			double scalar = mapItemIDScalar.get(itemID);
			this.setItemIDScalar(itemID, scalar);
		}
		this.model.clear();
	}
	
	protected void setGraphScoreIndex(CentroidClassifier classifier) {
		for (int itemID : this.setItemID) {
			ArrayList<ScoreData> listScore = this.topKCategory(itemID, ItemRanker.DEFAULT_TOPK_CATEGORY, classifier);
			double normFactor = 0.0;
			for (ScoreData data : listScore) {
				normFactor += data.getScore();
			}
			for (ScoreData data : listScore) {
				int cid = data.getID();
				double score = data.getScore()/normFactor;
				this.setCIDItemIDScore(cid, itemID, score);
			}
		}
	}
	
	private VectorSpaceModel model;
	private HashSet<Integer> setItemID;
	protected boolean isNotIndexed;
	protected boolean isNotAnalyzed;
	
	protected static final String[]	SQL_CREATE_DB_ITEM_ANALYZER	= {"BEGIN TRANSACTION;",
		   														   "DROP TABLE IF EXISTS android_metadata;",
		   														   "DROP TABLE IF EXISTS scalar;",
		   														   "DROP TABLE IF EXISTS postings;",
		   														   "DROP TABLE IF EXISTS vocabulary;",
		   														   "DROP TABLE IF EXISTS scoreindex;",
		   														   "CREATE TABLE android_metadata (locale TEXT);",
		   														   "INSERT INTO android_metadata VALUES('en_US');",
		   														   "CREATE TABLE scalar (itemID integer, scalar real);",
		   														   "CREATE TABLE postings (termID integer, itemID integer, weight real);",
		   														   "CREATE TABLE vocabulary (term text not null, df integer, termID integer);",
		   														   "CREATE TABLE scoreindex (cid integer, itemID integer, score real);",
		   														   "CREATE UNIQUE INDEX [IDX_SCALAR_ITEMID] ON [scalar]([itemID]  ASC);",
		   														   "CREATE INDEX [IDX_POSTINGS_ITEMID] ON [postings]([itemID]  ASC);",
		   														   "CREATE INDEX [IDX_POSTINGS_TERMID] ON [postings]([termID]  ASC);",
		   														   "CREATE INDEX [IDX_VOCABULARY_TERM] ON [vocabulary]([term]  ASC);",
		   														   "CREATE INDEX [IDX_SCOREINDEX_CID] ON [scoreindex]([cid] ASC);",
		   														   "COMMIT;"};
}
