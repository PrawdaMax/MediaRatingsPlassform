package org.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class JUnitTests {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final String USER_ID = "018f3c60-8cd5-7c92-a7e4-cf03a3a1aa03";
    private static final String MEDIA_ID = "018f3c60-8cd5-7c92-a7e4-cf03a3a1bb01";
    private static final String MEDIA_TODELETE_ID = "018f3c60-8cd5-7c92-a7e4-cf03a3a1bb07";
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
        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"user1\",\"password\":\"pass123\"}")
                .when()
                .post("/users/register")
                .then()
                .statusCode(201);
    }

    @Test
    public void testLoginUser() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"bob\",\"password\":\"securepass\"}")
                .when()
                .post("/users/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }

    // ------------------- USER -------------------
    @Test
    public void testGetProfile() {
        given()
                .when()
                .get("/users/" + USER_ID + "/profile")
                .then()
                .statusCode(200)
                .body("username", notNullValue());
    }

    @Test
    public void testUpdateProfile() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"updatedCarol\",\"password\":\"12345\"}")
                .when()
                .put("/users/" + USER_ID + "/profile")
                .then()
                .statusCode(200)
                .body("username", equalTo("updatedCarol"));
    }

    @Test
    public void testGetRatingHistory() {
        given()
                .when()
                .get("/users/" + USER_ID + "/ratings")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetFavorites() {
        given()
                .when()
                .get("/users/" + USER_ID + "/favorites")
                .then()
                .statusCode(200);
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

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/media")
                .then()
                .statusCode(201)
                .body("title", equalTo("Inglourious Basterds"));
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

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/media/" + MEDIA_ID)
                .then()
                .statusCode(200)
                .body("title", equalTo("Inception Updated"));
    }

    @Test
    public void testGetMediaById() {
        given()
                .when()
                .get("/media/" + MEDIA_ID)
                .then()
                .statusCode(200)
                .body("id", equalTo((MEDIA_ID)));
    }

    @Test
    public void testDeleteMediaEntry() {
        given()
                .when()
                .delete("/media/" + MEDIA_TODELETE_ID)
                .then()
                .statusCode(204);
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

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/media/" + MEDIA_ID + "/rate")
                .then()
                .statusCode(201);
    }

    @Test
    public void testUpdateRating() {
        String requestBody = """
            {
                "stars": 4,
                "comment": "Updated comment"
            }
        """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/ratings/" + RATING_ID)
                .then()
                .statusCode(200)
                .body("value", equalTo(4));
    }

    @Test
    public void testConfirmRatingComment() {
        given()
                .when()
                .post("/ratings/" + RATING_ID + "/confirm")
                .then()
                .statusCode(200);
    }

    // ------------------- FAVORITES -------------------
    @Test
    public void testMarkAsFavorite() {
        String body = "{ \"user\": \"" + USER_ID + "\" }";
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/media/" + FAVORITE_MEDIA_ID + "/favorite")
                .then()
                .statusCode(200);
    }

    @Test
    public void testUnmarkAsFavorite() {
        String body = "{ \"user\": \"" + USER_ID + "\" }";
        given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .delete("/media/" + UNFAVORITE_MEDIA_ID + "/favorite")
                .then()
                .statusCode(200);
    }

    // ------------------- RECOMMENDATION -------------------
    @Test
    public void testGetRecommendationsByGenre() {
        given()
                .when()
                .get("/users/" + USER_ID + "/recommendations?type=genre")
                .then()
                .statusCode(200);
    }

    @Test
    public void testGetRecommendationsByContent() {
        given()
                .when()
                .get("/users/" + USER_ID + "/recommendations?type=content")
                .then()
                .statusCode(400);
    }

    // ------------------- LEADERBOARD -------------------
    @Test
    public void testGetLeaderboard() {
        given()
                .when()
                .get("/leaderboard")
                .then()
                .statusCode(200);
    }
}
