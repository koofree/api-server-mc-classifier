package kr.ac.korea.mobide.sigma.vsm;

public interface VectorGenerator {
	public abstract int getTermID(String term);
	
	public abstract int getDF(int termID);
	
	public abstract int getNumDoc();
}
