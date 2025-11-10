package org.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    private void printResponse(String title, Response response) {
        System.out.println("\n----- " + title + " -----");
        System.out.println("Status Code: " + response.statusCode());
        System.out.println("Status Line: " + response.statusLine());
        System.out.println("Body:\n" + response.getBody().asString());
    }

    // ------------------- AUTH -------------------
    @Test
    public void testRegisterUser() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"user1\",\"password\":\"pass123\"}")
                .when()
                .post("/users/register")
                .then()
                .extract()
                .response();

        printResponse("RegisterUser", response);
    }

    @Test
    public void testLoginUser() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"bob\",\"password\":\"securepass\"}")
                .when()
                .post("/users/login")
                .then()
                .extract()
                .response();

        printResponse("LoginUser", response);
        assertEquals(200, response.statusCode(), "Expected HTTP 200 OK");
        assertTrue(response.getBody().asString().contains("token"), "Login should return a token");
    }

    // ------------------- USER -------------------
    @Test
    public void testGetProfile() {
        Response response = given()
                .when()
                .get("/users/" + USER_ID + "/profile")
                .then()
                .extract()
                .response();

        printResponse("GetUserProfile", response);
    }

    @Test
    public void testUpdateProfile() {
        Response response = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"updatedCarol\",\"password\":\"12345\"}")
                .when()
                .put("/users/" + USER_ID + "/profile")
                .then()
                .extract()
                .response();

        printResponse("UpdateUserProfile", response);
    }

    @Test
    public void testGetRatingHistory() {
        Response response = given()
                .when()
                .get("/users/" + USER_ID + "/ratings")
                .then()
                .extract()
                .response();

        printResponse("GetRatingHistory", response);
    }

    @Test
    public void testGetFavorites() {
        Response response = given()
                .when()
                .get("/users/" + USER_ID + "/favorites")
                .then()
                .extract()
                .response();

        printResponse("GetFavorites", response);
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

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/media")
                .then()
                .extract()
                .response();

        printResponse("CreateMediaEntry", response);
    }

    @Test
    public void testUpdateMediaEntry() {
        String requestBody = """
            {
                "title": "Inception Updated",
                "description": "Updated description",
                "mediaType": "movie",
                "releaseYear": 2010,
                "genres": ["sci-fi", "action"],
                "ageRestriction": 16
            }
        """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/media/" + MEDIA_ID)
                .then()
                .extract()
                .response();

        printResponse("UpdateMediaEntry", response);
    }

    @Test
    public void testGetMediaById() {
        Response response = given()
                .when()
                .get("/media/" + MEDIA_ID)
                .then()
                .extract()
                .response();

        printResponse("GetMediaById", response);
    }

    @Test
    public void testDeleteMediaEntry() {
        Response response = given()
                .when()
                .delete("/media/" + MEDIA_TO_DELETE_ID)
                .then()
                .extract()
                .response();

        printResponse("DeleteMediaEntry", response);
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

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/media/" + MEDIA_ID + "/rate")
                .then()
                .extract()
                .response();

        printResponse("RateMedia", response);
    }

    @Test
    public void testUpdateRating() {
        String requestBody = """
            {
                "stars": 4,
                "comment": "Updated comment"
            }
        """;

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/ratings/" + RATING_ID)
                .then()
                .extract()
                .response();

        printResponse("UpdateRating", response);
    }

    @Test
    public void testConfirmRatingComment() {
        Response response = given()
                .when()
                .post("/ratings/" + RATING_ID + "/confirm")
                .then()
                .extract()
                .response();

        printResponse("ConfirmRatingComment", response);
    }

    // ------------------- FAVORITES -------------------
    @Test
    public void testMarkAsFavorite() {
        String body = "{ \"user\": \"" + USER_ID + "\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post("/media/" + FAVORITE_MEDIA_ID + "/favorite")
                .then()
                .extract()
                .response();

        printResponse("MarkAsFavorite", response);
    }

    @Test
    public void testUnmarkAsFavorite() {
        String body = "{ \"user\": \"" + USER_ID + "\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .delete("/media/" + UNFAVORITE_MEDIA_ID + "/favorite")
                .then()
                .extract()
                .response();

        printResponse("UnmarkAsFavorite", response);
    }

    // ------------------- RECOMMENDATION -------------------
    @Test
    public void testGetRecommendationsByGenre() {
        Response response = given()
                .when()
                .get("/users/" + USER_ID + "/recommendations?type=genre")
                .then()
                .extract()
                .response();

        printResponse("GetRecommendationsByGenre", response);
    }

    @Test
    public void testGetRecommendationsByContent() {
        Response response = given()
                .when()
                .get("/users/" + USER_ID + "/recommendations?type=content")
                .then()
                .extract()
                .response();

        printResponse("GetRecommendationsByContent", response);
    }

    // ------------------- LEADERBOARD -------------------
    @Test
    public void testGetLeaderboard() {
        Response response = given()
                .when()
                .get("/leaderboard")
                .then()
                .extract()
                .response();

        printResponse("GetLeaderboard", response);
    }
}
