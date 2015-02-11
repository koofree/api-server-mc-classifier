package kr.ac.korea.mobide.apiservice.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * Created by Koo Lee on 11/11/2014.
 */
@Getter
@AllArgsConstructor
public class SimilarityScore implements Comparable<SimilarityScore> {
    private String name;
    private double score;

    @Override
    public int compareTo(SimilarityScore o) {
        if (o.getScore() > this.score) {
            return 1;
        } else if (o.getScore() < this.score) {
            return -1;
        } else {
            return 0;
        }
    }
}
