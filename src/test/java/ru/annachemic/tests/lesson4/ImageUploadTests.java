package ru.annachemic.tests.lesson4;

import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.annachemic.dto.PostImageResponse;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static ru.annachemic.Endpoints.UPLOAD_IMAGE;

public class ImageUploadTests extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/resources/luca_02.jpeg";
    static String encodedFile;
    String uploadedImageId;

    MultiPartSpecification base64MultiPartSpec;
    MultiPartSpecification multiPartSpecWithFile;

    static RequestSpecification requestSpecificationWithAuthAndMultipartImage;
    static RequestSpecification requestSpecificationWithAuthWithBase64;

    @BeforeEach
    void beforeTest() {

        byte[] byteArray = getFileContent(PATH_TO_IMAGE);
        encodedFile = Base64.getEncoder().encodeToString(byteArray);

        base64MultiPartSpec = new MultiPartSpecBuilder(encodedFile)
                .controlName("image")
                .build();

        multiPartSpecWithFile = new MultiPartSpecBuilder(new File("src/test/resources/luca_02.jpeg"))
                .controlName("image")
                .build();

        requestSpecificationWithAuthAndMultipartImage = new RequestSpecBuilder()
                .addHeader("Authorization", token)
                .addFormParam("title", "Picture")
                .addFormParam("type", "gif")
                .addMultiPart(multiPartSpecWithFile)
                .build();
    }

    @Test
    void uploadFileTest() {
        uploadedImageId = given(requestSpecificationWithAuthWithBase64, positiveResponseSpecification)
                .post(UPLOAD_IMAGE)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");

    }

     @Test
     void setUploadedImageId() {
        uploadedImageId = given(requestSpecificationWithAuthAndMultipartImage)
            .expect()
            .statusCode(200)
            .when()
            .post("https://api.imgur.com/3/upload")
            .prettyPeek()
            .then()
            .extract()
            .body()
            .as(PostImageResponse.class)
            .getData().getDeletehash();

}
    @Test
    void uploadWithMultiPart() {
    uploadedImageId = given(requestSpecificationWithAuthAndMultipartImage)
            .post("https://api.imgur.com/3/image")
            .prettyPeek()
            .then()
            .extract()
            .response()
            .jsonPath()
            .getString("data.deletehash");
    }
    @AfterEach
    void tearDown() {
        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", "testprogmath", uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);
    }

    private byte[] getFileContent(String PATH_TO_IMAGE) {
        byte[] byteArray = new byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(this.PATH_TO_IMAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }


}