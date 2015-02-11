package kr.ac.korea.mobide.apiservice.interfaces.controller;

import kr.ac.korea.mobide.apiservice.application.CategoryService;
import kr.ac.korea.mobide.apiservice.application.SimilarityService;
import kr.ac.korea.mobide.apiservice.interfaces.dto.Category;
import kr.ac.korea.mobide.apiservice.interfaces.dto.SimilarityScore;
import kr.ac.korea.mobide.apiservice.interfaces.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Koo Lee on 11/11/2014.
 */

@RestController
public class SimilarityController {
    private static final String[] except_strings = {null, "null"};

    @Autowired
    private SimilarityService similarityService;

    @RequestMapping("/similarity")
    public List<SimilarityScore> query(
            @RequestParam(value = "query", required = true) String text,
            @RequestParam(value = "targets", required = true) String[] targets) {

        if (Arrays.asList(except_strings).contains(text))
            throw new ObjectNotFoundException();
        
        return similarityService.score(text, targets);
    }
}
