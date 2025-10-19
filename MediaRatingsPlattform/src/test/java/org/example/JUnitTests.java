package org.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class JUnitTests {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final String USER_ID = "018f3c60-8cd5-7c92-a7e4-cf03a3a1aa03";
    private static final String MEDIA_ID = "018f3c60-8cd5-7c92-a7e4-cf03a3a1bb01";
    private static final String MEDIA_TO_DELETE_ID = "018f3c60-8cd5-7c92-a7e4-cf03a3a1bb07";
    private static final String RATING_ID = "018f3d00-a001-7d12-a8f4-cf03a3a1cc01";
    private static final String FAVORITE_MEDIA_ID = "018f3c60-8cd5-7c92-a7e4-cf03a3a1bb01";
    private static final String UNFAVORITE_MEDIA_ID = "018f3c60-8cd5-7c92-a7e4-cf03a3a1bb0a";

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    // ------------------- AUTH -------------------
    @Test
    public void testRegisterUser() {
        String reqBody = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"user1\",\"password\":\"pass123\"}")
                .when()
                .post("/users/register")
                .then()
                .statusCode(201)
                .extract()
                .asString();

        System.out.println("\n-----RegisterUser");
        System.out.println(reqBody);
    }

    @Test
    public void testLoginUser() {
        String reqBody = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"bob\",\"password\":\"securepass\"}")
                .when()
                .post("/users/login")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        System.out.println("\n-----LoginUser");
        System.out.println(reqBody);
    }

    // ------------------- USER -------------------
    @Test
    public void testGetProfile() {
        String reqBody = given()
                .when()
                .get("/users/" + USER_ID + "/profile")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        System.out.println("\n-----getUserProfile");
        System.out.println(reqBody);
    }

    @Test
    public void testUpdateProfile() {
        String reqBody = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"updatedCarol\",\"password\":\"12345\"}")
                .when()
                .put("/users/" + USER_ID + "/profile")
                .then()
                .statusCode(200)
                .extract()
                .asString();

        System.out.println("\n-----updateUserProfile");
        System.out.println(reqBody);
    }

    @Test
    public void testGetRatingHistory() {
        String reqBody = given()
                .when()
                .get("/users/" + USER_ID + "/ratings")
                .then()
                .extract()
                .asString();

        System.out.println("\n-----getRatingHistory");
        System.out.println(reqBody);
    }

    @Test
    public void testGetFavorites() {
        String reqBody = given()
                .when()
                .get("/users/" + USER_ID + "/favorites")
                .then()
                .extract()
                .asString();

        System.out.println("\n-----getFavorites");
        System.out.println(reqBody);
    }

    // ------------------- MEDIA -------------------
    @Test
    public void testCreateMediaEntry() {
        String requestBody = """
            {
                "title": "Inglourious Basterds",
                "description": "A group of Jewish-American soldiers plot to assassinate Nazi leaders in WWII.",
                "mediaType": "movie",
                "releaseYear": 2009,
                "genres": ["war", "drama", "action"],
                "ageRestriction": 18
            }
        """;

        String reqBody = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/media")
                .then()
                .statusCode(201)
                .extract()
                .asString();

        System.out.println("\n-----createMediaEntry");
        System.out.println(reqBody);
    }

    @Test
    public void testUpdateMediaEntry() {
        String requestBody = """
            {
                "title": "Inception Updated",
                "description": "Updated description",
                "mediaType": "movie",
                "releaseYear": 2010,
                "genres": [
                  "sci-fi",
                  "action"
                ],
                "ageRestriction": 16
            }
        """;

        String reqBody = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/media/" + MEDIA_ID)
                .then()
                .statusCode(200)
                .extract()
                .asString();

        System.out.println("\n-----updateRatingHistory");
        System.out.println(reqBody);
    }

    @Test
    public void testGetMediaById() {
        String reqBody = given()
                .when()
                .get("/media/" + MEDIA_ID)
                .then()
                .statusCode(200)
                .extract()
                .asString();

        System.out.println("\n-----getMediaById");
        System.out.println(reqBody);
    }

    @Test
    public void testDeleteMediaEntry() {
        String reqBody = given()
                .when()
                .delete("/media/" + MEDIA_TO_DELETE_ID)
                .then()
                .extract()
                .asString();

        System.out.println("\n-----deleteMediaEntry");
        System.out.println(reqBody);
    }

    // ------------------- RATING -------------------
    @Test
    public void testRateMedia() {
        String requestBody = """
            {
                "stars": 5,
                "comment": "Amazing movie!",
                "user": "%s"
            }
        """.formatted(USER_ID);

        String reqBody = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/media/" + MEDIA_ID + "/rate")
                .then()
                .extract()
                .asString();

        System.out.println("\n-----rateMedia");
        System.out.println(reqBody);
    }

    @Test
    public void testUpdateRating() {
        String requestBody = """
            {
                "stars": 4,
                "comment": "Updated comment"
            }
        """;

        String reqBody = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/ratings/" + RATING_ID)
                .then()
                .statusCode(200)
                .extract()
                .asString();

        System.out.println("\n-----updateRating");
        System.out.println(reqBody);
    }

    @Test
    public void testConfirmRatingComment() {
        String reqBody = given()
                .when()
                .post("/ratings/" + RATING_ID + "/confirm")
                .then()
                .extract()
                .asString();

        System.out.println("\n-----confirmComment");
        System.out.println(reqBody);
    }

    // ------------------- FAVORITES -------------------
    @Test
    public void testMarkAsFavorite() {
        String body = "{ \"user\": \"" + USER_ID + "\" }";
        String reqBody = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/media/" + FAVORITE_MEDIA_ID + "/favorite")
                .then()
                .extract()
                .asString();

        System.out.println("\n-----markAsFavorite");
        System.out.println(reqBody);
    }

    @Test
    public void testUnmarkAsFavorite() {
        String body = "{ \"user\": \"" + USER_ID + "\" }";
        String reqBody = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .delete("/media/" + UNFAVORITE_MEDIA_ID + "/favorite")
                .then()
                .extract()
                .asString();

        System.out.println("\n-----unmarkAsFavorite");
        System.out.println(reqBody);
    }

    // ------------------- RECOMMENDATION -------------------
    @Test
    public void testGetRecommendationsByGenre() {
        String reqBody = given()
                .when()
                .get("/users/" + USER_ID + "/recommendations?type=genre")
                .then()
                .extract()
                .asString();

        System.out.println("\n-----getRecommendationsByGenre");
        System.out.println(reqBody);
    }

    @Test
    public void testGetRecommendationsByContent() {
        String reqBody = given()
                .when()
                .get("/users/" + USER_ID + "/recommendations?type=content")
                .then()
                .extract()
                .asString();

        System.out.println("\n-----getRecommendationsByContent");
        System.out.println(reqBody);
    }

    // ------------------- LEADERBOARD -------------------
    @Test
    public void testGetLeaderboard() {
        String reqBody = given()
                .when()
                .get("/leaderboard")
                .then()
                .extract()
                .asString();

        System.out.println("\n-----getLeaderboard");
        System.out.println(reqBody);
    }
}
