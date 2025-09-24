package org.example.pkgObj;

public class Rating {
    int value;
    String comment;
    String timestamp;
    private String Username;
    private String Medianame;

    public Rating(int value, String comment, String timestamp, String Username, String Medianame) {
        this.value = value;
        this.comment = comment;
        this.Username = Username;
        this.Medianame = Medianame;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getMedianame() {
        return Medianame;
    }

    public void setMedianame(String medianame) {
        Medianame = medianame;
    }
}
