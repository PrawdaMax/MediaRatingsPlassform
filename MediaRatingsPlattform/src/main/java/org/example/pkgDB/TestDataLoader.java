package org.example.pkgDB;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.pkgMisc.MediaType;
import org.example.pkgObj.*;

import java.io.FileReader;
import java.sql.Timestamp;
import java.util.UUID;

public class TestDataLoader {

    private final Database db;

    public TestDataLoader(Database db) {
        this.db = db;
    }

    /**
     * Loads test data from a JSON file and inserts it into the database.
     */
    public void loadFromJson(String filePath) {
        try {
            Gson gson = new GsonBuilder().create();
            TestDataWrapper data = gson.fromJson(new FileReader(filePath), TestDataWrapper.class);

            if (data == null) {
                System.out.println("No data found in JSON file!");
                return;
            }

            System.out.println("Loading test data...");

            // --- Insert Media first ---
            if (data.media != null) {
                for (TestDataWrapper.MediaJson m : data.media) {
                    try {
                        Media media = new Media(
                                m.title,
                                m.description,
                                MediaType.valueOf(m.mediaType),
                                m.year,
                                m.genres,
                                m.ageRestriction
                        );
                        media.setId(UUID.fromString(m.id));
                        db.addMedia(media);
                        System.out.println("Added media: " + m.title);
                    } catch (Exception e) {
                        System.err.println("Failed to add media: " + m.title + " → " + e.getMessage());
                    }
                }
            }

            // --- Insert Users ---
            if (data.users != null) {
                for (TestDataWrapper.UserJson u : data.users) {
                    try {
                        User user = new User(UUID.fromString(u.id), u.username, u.password);
                        db.addUser(user);
                        System.out.println("Added user: " + u.username);
                    } catch (Exception e) {
                        System.err.println("Failed to add user: " + u.username + " → " + e.getMessage());
                    }
                }
            }

            // --- Insert Ratings ---
            if (data.ratings != null) {
                for (TestDataWrapper.RatingJson r : data.ratings) {
                    try {
                        // Convert ISO 8601 to SQL Timestamp
                        String timestampStr = r.timestamp
                                .replace("T", " ")
                                .replace("Z", "");
                        Timestamp ts = Timestamp.valueOf(timestampStr);

                        Rating rating = new Rating(
                                r.value,
                                r.comment,
                                ts.toString(),
                                UUID.fromString(r.userId),
                                UUID.fromString(r.mediaId)
                        );
                        rating.setId(UUID.fromString(r.id));
                        rating.setConfirmed(r.confirmed);
                        db.addRating(rating);
                        System.out.println("Added rating: " + r.comment);
                    } catch (Exception e) {
                        System.err.println("Failed to add rating: " + r.comment + " → " + e.getMessage());
                    }
                }
            }

            System.out.println("Test data successfully loaded into database!");

        } catch (Exception e) {
            System.err.println("Failed to load test data: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
