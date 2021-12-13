package ru.annachemic.tests.lesson4;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.BeforeAll;

import java.util.Properties;

import static java.lang.System.getProperties;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public abstract class BaseTest {
    static ResponseSpecification positiveResponseSpecification;
    static RequestSpecification requestWithAuth;


    static Properties properties = new Properties();
    static String token;
    static String username;

    @BeforeAll
    static void beforeAll() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());
        RestAssured.baseURI = "https://api.imgur.com/3";
        getProperties();
        token = properties.getProperty("token");
        username = properties.getProperty("username");

        positiveResponseSpecification = new ResponseSpecBuilder()
                .expectBody("status", equalTo(200))
                .expectBody("success", is(true))
//                   .expectResponseTime(LessThanOrEqualTo(500L))
                .expectContentType(ContentType.JSON)
                .expectStatusCode(200)
                .build();

        requestWithAuth = new RequestSpecBuilder()
                .addHeader("Authorisation", token)
                .build();


    }
}
