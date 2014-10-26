package kr.ac.korea.mobide.sigma.engine;

import java.util.HashMap;

public interface GraphScoreIndex {
	public abstract HashMap<Integer, Double> getMapItemIDScore(int cid);
}
