package kr.ac.korea.mobide.apiservice.application;

import kr.ac.korea.mobide.apiservice.interfaces.dto.SimilarityScore;
import kr.ac.korea.mobide.sigma.classifier.sqlite.CentroidClassifierSQLite;
import kr.ac.korea.mobide.sigma.common.ScoreData;
import kr.ac.korea.mobide.sigma.wppr.sqlite.WPPRSQLite;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Koo Lee on 11/11/2014.
 */
@Service
public class SimilarityService {

    @Autowired
    private WPPRSQLite wppr;

    @Autowired
    private CentroidClassifierSQLite classifier;

    private static final Map<String, List<ScoreData>> cacheCategoryScores = new HashMap<>();
    private static final Map<SimilarityKey, Double> cacheSimilarityScores = new HashMap<>();


    public List<SimilarityScore> score(String fromString, String[] toStrings) {
        List<ScoreData> fromScores = cacheScores(fromString);
        List<SimilarityScore> scores = new ArrayList<>();
        for (String toString : toStrings) {
            List<ScoreData> toScores = cacheScores(toString);
            double sum = 0.0;
            for (ScoreData fromScore : fromScores) {
                for (ScoreData toScore : toScores) {
                    sum += cacheSimilarity(fromScore.getID(), toScore.getID());
                }
            }
            sum = sum / fromScores.size();
            scores.add(new SimilarityScore(toString, sum));
        }

        Collections.sort(scores);

        return scores;
    }

    private List<ScoreData> cacheScores(String str) {
        synchronized (SimilarityService.class) {
            if (cacheCategoryScores.containsKey(str)) {
                return cacheCategoryScores.get(str);
            } else {
                List<ScoreData> scores = classifier.topK(100, str);
                cacheCategoryScores.put(str, scores);
                return scores;
            }
        }
    }

    private double cacheSimilarity(int from, int to) {
        SimilarityKey key = new SimilarityKey(from, to);
        synchronized (SimilarityService.class) {
            if (cacheSimilarityScores.containsKey(key)) {
                return cacheSimilarityScores.get(key);
            } else {
                double score = wppr.getScore(from, to);
                cacheSimilarityScores.put(key, score);
                return score;
            }
        }
    }

    @EqualsAndHashCode(of = {"from", "to"})
    class SimilarityKey {
        private final int from;
        private final int to;

        public SimilarityKey(int from, int to) {
            this.from = from;
            this.to = to;
        }
    }
}
