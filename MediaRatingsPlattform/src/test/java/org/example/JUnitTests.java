package org.example;

import org.example.pkgMisc.MediaType;
import org.example.pkgObj.Media;
import org.example.pkgObj.Rating;
import org.example.pkgObj.User;
import org.example.pkgServer.pkgRepositories.MediaRepository;
import org.example.pkgServer.pkgRepositories.RatingRepository;
import org.example.pkgServer.pkgRepositories.TokenRepository;
import org.example.pkgServer.pkgRepositories.UserRepository;
import org.example.pkgService.MediaService;
import org.example.pkgService.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class JUnitTests {

    private UserService userService;
    private MediaService mediaService;

    @Mock
    private UserRepository userRepo;
    @Mock
    private TokenRepository tokenRepo;
    @Mock
    private RatingRepository ratingRepo;
    @Mock
    private MediaRepository mediaRepo;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepo, tokenRepo, ratingRepo, mediaRepo);
        mediaService = new MediaService(mediaRepo, userRepo, ratingRepo);
    }

    // --- UserService Tests ---

    @Test
    public void testGetAllUsers() {
        User user1 = new User("user1", "pass1");
        User user2 = new User("user2", "pass2");
        when(userRepo.getAll()).thenReturn(Arrays.asList(user1, user2));

        Map<String, Object> response = userService.getAllUsers();

        assertEquals(200, response.get("statusCode"));
        String body = (String) response.get("response");
        assertTrue(body.contains("user1"));
        assertTrue(body.contains("user2"));
    }

    @Test
    public void testRegisterUser_Success() {
        String jsonBody = "{\"username\":\"newUser\", \"password\":\"newPass\"}";
        when(userRepo.addUser(any(User.class))).thenReturn(true);

        Map<String, Object> response = userService.registerUser(jsonBody);

        assertEquals(201, response.get("statusCode"));
        String body = (String) response.get("response");
        assertTrue(body.contains("newUser"));
    }

    @Test
    public void testRegisterUser_Conflict() {
        String jsonBody = "{\"username\":\"existingUser\", \"password\":\"pass\"}";
        when(userRepo.addUser(any(User.class))).thenReturn(false);

        Map<String, Object> response = userService.registerUser(jsonBody);

        assertEquals(409, response.get("statusCode"));
        assertTrue(((String) response.get("response")).contains("Username already in use"));
    }

    @Test
    public void testLoginUser_Success() {
        String jsonBody = "{\"username\":\"validUser\", \"password\":\"validPass\"}";
        User user = new User("validUser", "validPass");
        UUID userId = user.getId();
        
        when(userRepo.getAll()).thenReturn(Collections.singletonList(user));
        when(tokenRepo.getTokenByUserId(userId)).thenReturn("validToken");

        Map<String, Object> response = userService.loginUser(jsonBody);

        assertEquals(200, response.get("statusCode"));
        String body = (String) response.get("response");
        assertTrue(body.contains("token"));
    }

    @Test
    public void testLoginUser_InvalidCredentials() {
        String jsonBody = "{\"username\":\"wrongUser\", \"password\":\"wrongPass\"}";
        when(userRepo.getAll()).thenReturn(Collections.emptyList());

        Map<String, Object> response = userService.loginUser(jsonBody);

        assertEquals(404, response.get("statusCode"));
        assertTrue(((String) response.get("response")).contains("Invalid credentials"));
    }

    @Test
    public void testGetUserProfile_Found() {
        UUID userId = UUID.randomUUID();
        User user = new User("testUser", "pass");
        user.setId(userId);
        when(userRepo.getById(userId)).thenReturn(user);

        Map<String, Object> response = userService.getUserProfile(userId);

        assertEquals(200, response.get("statusCode"));
        assertTrue(((String) response.get("response")).contains("testUser"));
    }

    @Test
    public void testGetUserProfile_NotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepo.getById(userId)).thenReturn(null);

        Map<String, Object> response = userService.getUserProfile(userId);

        assertEquals(404, response.get("statusCode"));
        assertTrue(((String) response.get("response")).contains("User not found"));
    }

    @Test
    public void testUpdateUserProfile_Success() {
        UUID userId = UUID.randomUUID();
        User user = new User("oldName", "oldPass");
        user.setId(userId);
        String jsonBody = "{\"username\":\"newName\", \"password\":\"newPass\"}";

        when(userRepo.getById(userId)).thenReturn(user);

        Map<String, Object> response = userService.updateUserProfile(userId, jsonBody);

        assertEquals(200, response.get("statusCode"));
        String body = (String) response.get("response");
        assertTrue(body.contains("newName"));
        assertEquals("newName", user.getUsername());
    }

    @Test
    public void testUpdateUserProfile_NotFound() {
        UUID userId = UUID.randomUUID();
        String jsonBody = "{\"username\":\"newName\", \"password\":\"newPass\"}";
        when(userRepo.getById(userId)).thenReturn(null);

        Map<String, Object> response = userService.updateUserProfile(userId, jsonBody);

        assertEquals(404, response.get("statusCode"));
    }

    // --- MediaService Tests ---
    @Test
    public void testGetAllMedia() {
        List<String> genres = new ArrayList<>();
        genres.add("Genre1");
        genres.add("Genre2");
        Media m1 = new Media("Movie1", "Desc1", MediaType.movie, 2020, genres, 7);
        Media m2 = new Media("Movie2", "Desc2", MediaType.movie, 2012, genres, 12);
        when(mediaRepo.getAll()).thenReturn(Arrays.asList(m1, m2));

        Map<String, Object> response = mediaService.getAllMedia(Collections.emptyMap());

        assertEquals(200, response.get("statusCode"));
        String body = (String) response.get("response");
        assertTrue(body.contains("Movie1"));
        assertTrue(body.contains("Movie2"));
    }

    @Test
    public void testPostMedia_Success() {
        String jsonBody = "{\"title\":\"NewMovie\", \"type\":\"MOVIE\", \"releaseYear\":2022, \"description\":\"NewDesc\"}";
        when(mediaRepo.getAll()).thenReturn(Collections.emptyList());

        Map<String, Object> response = mediaService.postMedia(jsonBody);

        assertEquals(201, response.get("statusCode"));
        String body = (String) response.get("response");
        assertTrue(body.contains("NewMovie"));
        verify(mediaRepo, times(1)).save(any(Media.class));
    }

    @Test
    public void testPostMedia_Conflict() {
        String jsonBody = "{\"title\":\"Inception\", \"mediaType\":\"movie\", \"releaseYear\":2010, \"description\":\"A mind-bending thriller about dream invasion.\"}";
        List<String> genres = new ArrayList<>();
        genres.add("Genre1");
        genres.add("Genre2");
        Media existing = new Media("Inception", " A mind-bending thriller about dream invasion.", MediaType.movie, 2010, genres, 13);
        when(mediaRepo.getAll()).thenReturn(Collections.singletonList(existing));

        Map<String, Object> response = mediaService.postMedia(jsonBody);

        assertEquals(409, response.get("statusCode"));
        assertTrue(((String) response.get("response")).contains("Media already exists"));
    }

    @Test
    public void testGetMedia_Found() {
        UUID mediaId = UUID.randomUUID();
        List<String> genres = new ArrayList<>();
        genres.add("Genre1");
        genres.add("Genre2");
        Media media = new Media("FoundMovie", "Desc", MediaType.movie, 2020, genres, 12);
        media.setId(mediaId);
        when(mediaRepo.getAll()).thenReturn(Collections.singletonList(media));

        Map<String, Object> response = mediaService.getMedia(mediaId);

        assertEquals(200, response.get("statusCode"));
        assertTrue(((String) response.get("response")).contains("FoundMovie"));
    }

    @Test
    public void testGetMedia_NotFound() {
        UUID mediaId = UUID.randomUUID();
        when(mediaRepo.getAll()).thenReturn(Collections.emptyList());

        Map<String, Object> response = mediaService.getMedia(mediaId);

        assertEquals(404, response.get("statusCode"));
        assertTrue(((String) response.get("response")).contains("Not found"));
    }

    @Test
    public void testUpdateMedia_Success() {
        UUID mediaId = UUID.randomUUID();
        List<String> genres = new ArrayList<>();
        genres.add("Genre1");
        genres.add("Genre2");
        Media media = new Media("OldTitle", "Desc", MediaType.movie, 2020, genres, 18);
        media.setId(mediaId);
        String jsonBody = "{\"title\":\"NewTitle\"}";

        when(mediaRepo.getAll()).thenReturn(Collections.singletonList(media));
        when(mediaRepo.getById(mediaId)).thenReturn(media);
        when(mediaRepo.updateMedia(anyMap(), eq(media))).thenAnswer(invocation -> {
            media.setTitle("NewTitle");
            return media;
        });

        Map<String, Object> response = mediaService.updateMedia(mediaId, jsonBody);

        assertEquals(200, response.get("statusCode"));
        assertTrue(((String) response.get("response")).contains("NewTitle"));
    }

    @Test
    public void testDeleteMedia_Success() {
        UUID mediaId = UUID.randomUUID();
        List<String> genres = new ArrayList<>();
        genres.add("Genre1");
        genres.add("Genre2");
        Media media = new Media("ToDelete", "Desc", MediaType.movie, 2012, genres, 4);
        media.setId(mediaId);
        when(mediaRepo.getAll()).thenReturn(Collections.singletonList(media));

        Map<String, Object> response = mediaService.deleteMedia(mediaId);

        assertEquals(204, response.get("statusCode"));
        verify(mediaRepo, times(1)).deleteMedia(mediaId);
    }

    @Test
    public void testAddRating_Success() {
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String jsonBody = "{\"user\":\"" + userId + "\", \"stars\":5, \"comment\":\"Great!\"}";

        Map<String, Object> response = mediaService.addRating(mediaId, jsonBody);

        assertEquals(201, response.get("statusCode"));
        verify(ratingRepo, times(1)).addRating(any(Rating.class));
    }

    @Test
    public void testMarkAsFavorite_Success() {
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = new User("user", "pass");
        user.setId(userId);
        String jsonBody = "{\"user\":\"" + userId + "\"}";

        when(userRepo.getById(userId)).thenReturn(user);

        Map<String, Object> response = mediaService.markAsFavorite(mediaId, jsonBody);

        assertEquals(200, response.get("statusCode"));
        assertTrue(user.getFavorites().contains(mediaId));
    }

    @Test
    public void testMarkAsFavorite_AlreadyFavorite() {
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = new User("user", "pass");
        user.setId(userId);
        user.addFavorite(mediaId);
        String jsonBody = "{\"user\":\"" + userId + "\"}";

        when(userRepo.getById(userId)).thenReturn(user);

        Map<String, Object> response = mediaService.markAsFavorite(mediaId, jsonBody);

        assertEquals(400, response.get("statusCode"));
        assertTrue(((String) response.get("response")).contains("Already favorite"));
    }

    @Test
    public void testUnmarkAsFavorite_Success() {
        UUID mediaId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        User user = new User("user", "pass");
        user.setId(userId);
        user.addFavorite(mediaId);
        String jsonBody = "{\"user\":\"" + userId + "\"}";

        when(userRepo.getById(userId)).thenReturn(user);

        Map<String, Object> response = mediaService.unmarkAsFavorite(mediaId, jsonBody);

        assertEquals(200, response.get("statusCode"));
        assertFalse(user.getFavorites().contains(mediaId));
    }
}
