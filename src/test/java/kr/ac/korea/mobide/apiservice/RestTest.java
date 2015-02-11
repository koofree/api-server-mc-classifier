package kr.ac.korea.mobide.apiservice;

import com.jayway.restassured.RestAssured;
import kr.ac.korea.mobide.apiservice.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Arrays;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

/**
 * Created by Koo Lee on 12/1/2014.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:8083")
public class RestTest {

    @Value("${server.port}")
    int port;

    @Before
    public void before() {
        RestAssured.port = port;
    }

    @Test
    public void centroidTest() {
        String[] targets = {"soccer", "fruit", "science"};
        when().get("/category").print();
        given()
                .param("query", "null")
                .param("targets", Arrays.asList(targets))
                .when()
                .get("/similarity").print();
    }

}
