package kr.ac.korea.mobide.sigma.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import kr.ac.korea.mobide.sigma.classifier.CentroidClassifier;
import kr.ac.korea.mobide.sigma.common.ScoreData;
import kr.ac.korea.mobide.sigma.vsm.InvertedIndex;
import kr.ac.korea.mobide.sigma.vsm.Vector;
import kr.ac.korea.mobide.sigma.vsm.VectorSpaceModel;
import kr.ac.korea.mobide.sigma.wppr.WPPR;

/**
 * A SigmaRanker that ranks items given a query and a set of items according to the GraphScore.
 * A query is defined by a QueryAnalyer object, and a set of item is defined by an ItemAnalyzer object.
 * 
 * @author okcomputer
 * 
 */
public class ItemRanker {
	/**
	 * Constructs an ItemRanker object given a classifier and a wppr.
	 * @param classifier A CentroidClassifer object
	 * @param wppr A WPPR object
	 */
	public ItemRanker(CentroidClassifier classifier, WPPR wppr) {
		this.classifier = classifier;
		this.wppr = wppr;
		this.alpha = ItemRanker.DEFAULT_ALPHA;
	}
	
	protected ItemRanker(CentroidClassifier classifier, WPPR wppr, double alpha) {
		this.classifier = classifier;
		this.wppr = wppr;
		if (alpha <= 1.0 && alpha >= 0.0) {
			this.alpha = alpha;
		} else {
			this.alpha = ItemRanker.DEFAULT_ALPHA;
		}
	}
	
	/**
	 * Given the query text added in queryAnalyzer, it ranks all items added in itemAnalyzer
	 * according to the GraphScore.
	 * @param queryAnalyzer A QueryAnalyzer that contains query text
	 * @param itemAnalyzer An ItemAnalyzer that contains a set of items to be ranked
	 * @return a ranked ArrayList<ScoreData> object 
	 */
	public ArrayList<ScoreData> rank(QueryAnalyzer queryAnalyzer, ItemAnalyzer itemAnalyzer) {
		// Calculate KeywordScore
		HashMap<Integer, Double> mapItemIDKeywordScore;
		if (this.alpha == 1.0) {
			mapItemIDKeywordScore = new HashMap<Integer, Double>(0);
		} else {
			itemAnalyzer.index();
			Vector queryVector = queryAnalyzer.getVector(itemAnalyzer);
			mapItemIDKeywordScore = ItemRanker.calculateKeywordScore(queryVector, itemAnalyzer);
		}

		// Calculate GraphScore
		HashMap<Integer, Double> mapItemIDGraphScore;
		if (this.alpha == 0.0) {
			mapItemIDGraphScore = new HashMap<Integer, Double>(0);
		} else {
			queryAnalyzer.analyze(this.classifier);
			itemAnalyzer.analyze(this.classifier);
			ArrayList<ScoreData> listScoreQuery = queryAnalyzer.topKCategory(ItemRanker.DEFAULT_TOPK_CATEGORY, this.classifier);
			mapItemIDGraphScore = ItemRanker.calculateGraphScore(listScoreQuery, itemAnalyzer, wppr);
		}

		// Calculate FinalScore
		ArrayList<ScoreData> listScore = ItemRanker.calculateFinalScore(mapItemIDKeywordScore, mapItemIDGraphScore, this.alpha);
		mapItemIDKeywordScore.clear();
		mapItemIDGraphScore.clear();
		return listScore;
	}
	
	/**
	 * Given the query text added in queryAnalyzer, it ranks all items added in itemAnalyzer
	 * and returns top-k items ranked by GraphScore.
	 * @param k Specifies top-k
	 * @param queryAnalyzer A queryAnalyzer that contains query text
	 * @param itemAnalyzer An ItemAnalyzer that contains a set of items to be ranked
	 * @return a ranked ArrayList<ScoreData> object whose size is at most k
	 */
	public ArrayList<ScoreData> topK(int k, QueryAnalyzer queryAnalyzer, ItemAnalyzer itemAnalyzer) {
		ArrayList<ScoreData> listScore = this.rank(queryAnalyzer, itemAnalyzer);
		return ItemRanker.getTopKListClearSortedList(k, listScore);
	}
	
	protected static HashMap<Integer, Double> calculateKeywordScore(Vector queryVector, InvertedIndex index) {
		return VectorSpaceModel.getMapIDCosine(queryVector, index);
	}
	
	protected static HashMap<Integer, Double> calculateGraphScore(ArrayList<ScoreData> listScoreQuery, GraphScoreIndex index, WPPR wppr) {
		HashMap<Integer, Double> mapItemIDGraphScore = new HashMap<Integer, Double>();
		double normFactor = 0.0;
		for (ScoreData data : listScoreQuery) {
			normFactor += data.getScore();
		}
		if (normFactor <= 0.0) return mapItemIDGraphScore;
		for (ScoreData query : listScoreQuery) {
			int cidQuery = query.getID();
			double cwQuery = query.getScore()/normFactor;
			ArrayList<ScoreData> listTopKWPPR = wppr.topK(ItemRanker.DEFAULT_TOPK_WPPR, cidQuery);
			for (ScoreData item : listTopKWPPR) {
				int cidItem = item.getID();
				double wpprItemQuery = item.getScore();
				HashMap<Integer, Double> mapItemIDScore = index.getMapItemIDScore(cidItem);
				for (int itemID : mapItemIDScore.keySet()) {
					double cwItem = mapItemIDScore.get(itemID);
					double graphScore = cwQuery*cwItem*wpprItemQuery;
					if (mapItemIDGraphScore.containsKey(itemID)) {
						mapItemIDGraphScore.put(itemID, mapItemIDGraphScore.get(itemID)+graphScore);
					} else {
						mapItemIDGraphScore.put(itemID, graphScore);
					}
				}
			}
		}
		return mapItemIDGraphScore;
	}
	
	protected static ArrayList<ScoreData> calculateFinalScore(HashMap<Integer, Double> mapItemIDKeywordScore, HashMap<Integer, Double> mapItemIDGraphScore, double alpha) {
		ArrayList<ScoreData> listScore = new ArrayList<ScoreData>();
		HashSet<Integer> setItemID = new HashSet<Integer>();
		setItemID.addAll(mapItemIDKeywordScore.keySet());
		setItemID.addAll(mapItemIDGraphScore.keySet());
		for (int itemID : setItemID) {
			double keywordScore = 0.0, graphScore = 0.0;
			if (mapItemIDKeywordScore.containsKey(itemID)) {
				keywordScore = mapItemIDKeywordScore.get(itemID);
			}
			if (mapItemIDGraphScore.containsKey(itemID)) {
				graphScore = mapItemIDGraphScore.get(itemID);
			}
			double finalScore = (1.0-alpha)*keywordScore + alpha*graphScore;
			listScore.add(new ScoreData(itemID, finalScore));
		}
		Collections.sort(listScore);
		return listScore;
	}
	
	/**
	 * Given a pair of classification results for a query and an item, it returns GraphScore
	 * of the item categories with respect to the query categories.  
	 * @param listScoreQuery An ArrayList<ScoreData> object that stores top-k classification 
	 * result of a query
	 * @param listScoreItem An ArrayList<ScoreData> object that stores top-k classification
	 * result of an item
	 * @param wppr A WPPR object required to refer wPPR values for a pair of categories
	 * @return GraphScore value
	 */
	protected static double calculateGraphScore(ArrayList<ScoreData> listScoreQuery, ArrayList<ScoreData> listScoreItem, WPPR wppr) {
		double graphScore = 0.0, normFactorQuery = 0.0, normFactorItem = 0.0;
		for (ScoreData data : listScoreQuery) {
			normFactorQuery += data.getScore();
		}
		for (ScoreData data : listScoreItem) {
			normFactorItem += data.getScore();
		}
		if (normFactorQuery <= 0.0 || normFactorItem <= 0.0) return graphScore;
		for (ScoreData scoreQuery : listScoreQuery) {
			int cidQuery = scoreQuery.getID();
			double cwQuery = scoreQuery.getScore()/normFactorQuery;
			for (ScoreData scoreItem : listScoreItem) {
				int cidItem = scoreItem.getID();
				double cwItem = scoreItem.getScore()/normFactorItem;
				graphScore += wppr.getScore(cidItem, cidQuery)*cwQuery*cwItem;
			}
		}
		return graphScore;
	}
	
	private static ArrayList<ScoreData> getTopKListClearSortedList(int k, ArrayList<ScoreData> listScore) {
		if (k >= listScore.size()) {
			return listScore;
		} else {
			ArrayList<ScoreData> listTopK = new ArrayList<ScoreData>();
			for (int index = 0; index < k; index++) {
				listTopK.add(listScore.get(index));
			}
			listScore.clear();
			return listTopK;
		}
	}
	
	private CentroidClassifier classifier;
	private WPPR wppr;
	private double alpha;
	
	public static final int			DEFAULT_TOPK_CATEGORY	= 5;
	public static final int			DEFAULT_TOPK_WPPR		= 100;
	protected static final double	DEFAULT_ALPHA			= 1.0;
}
