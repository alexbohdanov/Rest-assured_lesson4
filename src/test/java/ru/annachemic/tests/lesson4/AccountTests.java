package ru.annachemic.tests.lesson4;

import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public class AccountTests extends BaseTest{


    @Test
    void getAccountInfoTest() {
        given(requestWithAuth, positiveResponseSpecification)
                .get("/account/{username}", username);
    }


    @Test
    void getAccountInfoWithLoggingTest() {
        given()
                .header("Authorization", "Bearer 81ed217eee6d991be324edc8754a07e4ce686bb9")
                .log()
                .method()
                .log()
                .uri()
                .when()
                .get("https://api.imgur.com/3/account/{username}", username)
                .prettyPeek()
                .then()
                .statusCode(200);
    }
}
