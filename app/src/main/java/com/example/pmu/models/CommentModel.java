package com.example.pmu.models;

public class CommentModel {

    private String user;
    private String comment;

    private String profilePic;



    public CommentModel() {
        this.comment = "";
        this.user = "";
        this.profilePic = "";
    }

    public CommentModel(String user, String comment,String profilePic) {
        this.user = user;
        this.comment = comment;
        this.profilePic = profilePic;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
