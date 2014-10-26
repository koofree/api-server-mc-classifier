package kr.ac.korea.mobide.sigma.vsm;

import java.util.HashMap;

public interface InvertedIndex {
	public abstract HashMap<Integer, Double> getPosting(int termID);
	
	public abstract double getScalar(int id);
}
