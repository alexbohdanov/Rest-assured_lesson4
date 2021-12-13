package ru.annachemic.tests.lesson4;

import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.annachemic.dto.PostImageResponse;

import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.annachemic.Endpoints.UPLOAD_IMAGE;

public class UpdateImageTests extends BaseTest {


    private final String PATH_TO_IMAGE = "src/test/resources/luca_02.jpeg";
    private String imageId;
    private MultiPartSpecification base64MultiPartSpec;
    private String encodeFile;
    private Response response;
    private String delateHash;
    private RequestSpecification requestSpecificationWithAuthWithBase64;

    @BeforeEach
    void setUp(){
        byte[] byteArray = getFileContent(PATH_TO_IMAGE);
        String encodedFile = Base64.getEncoder().encodeToString(byteArray);

        base64MultiPartSpec = new MultiPartSpecBuilder(encodeFile)
                .controlName("image")
                .build();

        requestSpecificationWithAuthWithBase64 = new RequestSpecBuilder()
                .addHeader("Authorisation", token)
                .addMultiPart(base64MultiPartSpec)
                .build();

        response = given(requestSpecificationWithAuthWithBase64, positiveResponseSpecification)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response();

        imageId = response.jsonPath().getString("data.id");
        String deleteHash = response.jsonPath().getString("data.deletehash");
    }

    private byte[] getFileContent(String path_to_image) {
        return new byte[0];
    }

    @DisplayName("Изменение title")
    @Test
    void updateFileTest() {
        given()
                .headers("Authorisation", token)
                .param("title", "Heart")
                .expect()
                .statusCode(200)
                .when()
                .put(("https://api.imgur.com/3/image/{imageHash}"), imageId)
                .prettyPeek()
                .then()
                .statusCode(200)
                .extract()
                .response();

        String title = given()
                .headers("Authorisation", token)
                .expect()
                .statusCode(200)
                .when()
                .get("https://api.imgur.com/3/image/{imageHash}", imageId)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .body()
                .as(PostImageResponse.class)
                .getData().getTitle();



        assertThat("Title", title, equalTo("Heart"));

    }

    private RequestSpecification prettyPeak() {
        return null;
    }
}
