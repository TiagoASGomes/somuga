package org.somuga.message;

public class Messages {

    public static final String USER_NOT_FOUND = "No user found with id: ";
    public static final String REVIEW_NOT_FOUND = "No review found with id: ";
    public static final String MOVIE_NOT_FOUND = "No movie found with id: ";
    public static final String GAME_NOT_FOUND = "No game found with id: ";
    public static final String MEDIA_NOT_FOUND = "No media found with id: ";
    public static final String LIKE_NOT_FOUND = "No media found with id: ";
    public static final String PLATFORM_NOT_FOUND = "No platform found with id: ";
    public static final String PLATFORM_NOT_FOUND_NAME = "No platform found with name: ";
    public static final String DEVELOPER_NOT_FOUND = "No developer found with id: ";
    public static final String DEVELOPER_NOT_FOUND_NAME = "No developer found with name: ";
    public static final String GENRE_NOT_FOUND = "No genre found with id: ";
    public static final String GENRE_NOT_FOUND_NAME = "No genre found with name: ";
    public static final String NON_EMPTY_USERNAME = "Username cannot be empty";
    public static final String INVALID_USERNAME = "Username must be between 4 and 20 characters, and can only contain letters, numbers, - and spaces";
    public static final String NON_EMPTY_EMAIL = "Email cannot be empty";
    public static final String INVALID_EMAIL = "Invalid email";
    public static final String DUPLICATE_EMAIL = "User with already exists with this email: ";
    public static final String DUPLICATE_USERNAME = "User with already exists with this username: ";
    public static final String ID_GREATER_THAN_0 = "Id must be greater than 0";
    public static final String ALREADY_LIKED = "User already liked this media";
    public static final String ALREADY_REVIEWED = "User already reviewed this media";
    public static final String INVALID_SCORE = "The review score must be between 1 and 10";
    public static final String MAX_REVIEW_CHARACTERS = "Review must be lower that 1024 characters";
    public static final String INVALID_TITLE = "Title field cannot be empty";
    public static final String INVALID_DEVELOPER_NAME = "Developer name must be between 3 and 50 characters, and can only contain letters, numbers and spaces";
    public static final String INVALID_PLATFORM_NAME = "Platform name must be between 3 and 50 characters, and can only contain letters, numbers and spaces";
    public static final String INVALID_GENRE_NAME = "Genre name can only contain letters, numbers and spaces";
    public static final String DEVELOPER_ALREADY_EXISTS = "Developer already exists with this name: ";
    public static final String PLATFORM_ALREADY_EXISTS = "Platform already exists with this name: ";
    public static final String GENRE_ALREADY_EXISTS = "Genre already exists with this name: ";


    private Messages() {
    }
}
