package kr.ac.korea.mobide.sigma.common;

/**
 * A simple class used as a unit of classification/ranking result. It stores an int as id and
 * double as score. In the context of classification, id is categoryID and score is cosine 
 * similarity of a query vector and MC(AD) vector of a id=categoryID. In the context of item
 * ranking, id is itemID and score is GraphScore value among query categories and item categories.
 * 
 * @author okcomputer
 *
 */
public class ScoreData implements Comparable<ScoreData> {
	/**
	 * Constructs a ScoreData object
	 * @param id An id
	 * @param score The corresponding score
	 */
	public ScoreData(int id, double score) {
		this.id = id;
		this.score = score;
	}
	
	/**
	 * Returns categoryID as a classification result, or itemID as a ranking result.
	 * @return An id
	 */
	public int getID() {
		return this.id;
	}
	
	/**
	 * Returns a score which corresponds to the id of a classification or ranking result.
	 * @return A score
	 */
	public double getScore() {
		return this.score;
	}
	
	/**
	 * Implemented for Comparable interface to sort Collections of ScoreData in descending
	 * order.
	 */
	public int compareTo(ScoreData other) {
		if (this.score > other.score) return -1;
		else if (this.score < other.score) return 1;
		else return 0;
	}
	
	private int id;
	private double score;
}
