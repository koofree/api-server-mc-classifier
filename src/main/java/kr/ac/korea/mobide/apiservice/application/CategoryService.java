package kr.ac.korea.mobide.apiservice.application;

import kr.ac.korea.mobide.apiservice.interfaces.dto.Category;
import kr.ac.korea.mobide.sigma.classifier.sqlite.CentroidClassifierSQLite;
import kr.ac.korea.mobide.sigma.common.ScoreData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Koo Lee on 2014-08-29.
 */
@Service
public class CategoryService {

    @Autowired
    private CentroidClassifierSQLite classifier;

    public List<Category> generateCategory(String query, int count) {
        List<Category> categories = new ArrayList<Category>();

        for (ScoreData data : classifier.topK(count, query)) {
            categories.add(new Category(classifier.getCategoryName(data.getID()), data.getScore()));
        }
        return categories;
    }
}
