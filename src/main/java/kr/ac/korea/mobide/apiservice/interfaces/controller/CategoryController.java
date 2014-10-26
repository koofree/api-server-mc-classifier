package kr.ac.korea.mobide.apiservice.interfaces.controller;

import kr.ac.korea.mobide.apiservice.interfaces.dto.Category;
import kr.ac.korea.mobide.apiservice.application.CategoryService;
import kr.ac.korea.mobide.apiservice.interfaces.exception.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Koo Lee on 2014-08-29.
 */
@RestController
public class CategoryController {

    private static final String[] except_strings = {null, "null"};

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/category")
    public List<Category> query(
            @RequestParam(value = "query", required = false, defaultValue = "world") String text,
            @RequestParam(value = "count", required = false, defaultValue = "5") int count) {

        if (Arrays.asList(except_strings).contains(text))
            throw new ObjectNotFoundException();

        return categoryService.generateCategory(text, count);
    }

}
