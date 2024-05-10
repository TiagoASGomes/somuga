package org.somuga.util.message;

public class Messages {

    public static final String USER_NOT_FOUND = "No user found with id: ";
    public static final String REVIEW_NOT_FOUND = "No review found with id: ";
    public static final String MOVIE_NOT_FOUND = "No movie found with id: ";
    public static final String GAME_NOT_FOUND = "No game found with id: ";
    public static final String MEDIA_NOT_FOUND = "No media found with id: ";
    public static final String LIKE_NOT_FOUND = "No media found with id: ";
    public static final String PLATFORM_NOT_FOUND = "No platform found with id: ";
    public static final String PLATFORM_NOT_FOUND_NAME = "No platform found with fullName: ";
    public static final String DEVELOPER_NOT_FOUND = "No developer found with id: ";
    public static final String DEVELOPER_NOT_FOUND_NAME = "No developer found with fullName: ";
    public static final String GENRE_NOT_FOUND = "No genre found with id: ";
    public static final String GENRE_NOT_FOUND_NAME = "No genre found with fullName: ";
    public static final String MOVIE_CREW_NOT_FOUND = "No crew member found with id: ";
    public static final String NON_EMPTY_USERNAME = "Username cannot be empty";
    public static final String INVALID_USERNAME = "Username must be between 4 and 20 characters, and can only contain letters, numbers and underscores";
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
    public static final String INVALID_DEVELOPER_NAME = "Developer fullName must be between 1 and 50 characters, and can only contain letters, numbers and spaces";
    public static final String INVALID_PLATFORM_NAME = "Platform fullName must be between 1 and 50 characters, and can only contain letters, numbers and spaces";
    public static final String INVALID_GENRE_NAME = "Genre fullName must be between 1 and 50 characters, and can only contain letters, numbers and spaces";
    public static final String DEVELOPER_ALREADY_EXISTS = "Developer already exists with this fullName: ";
    public static final String PLATFORM_ALREADY_EXISTS = "Platform already exists with this fullName: ";
    public static final String GENRE_ALREADY_EXISTS = "Genre already exists with this fullName: ";
    public static final String INVALID_RELEASE_DATE = "Release date cannot be empty";
    public static final String INVALID_PRICE = "Price must be between 0 and 1000";
    public static final String INVALID_DESCRIPTION = "Description field cannot be empty";
    public static final String MAX_DESCRIPTION_CHARACTERS = "Description must be lower that 1000 characters";
    public static final String INVALID_NAME = "Name field cannot be empty";
    public static final String INVALID_NAME_SIZE = "Name must be between 3 and 100 characters";
    public static final String INVALID_BIRTH_DATE = "Birth date must be in the past";
    public static final String INVALID_MOVIE_ROLE = "Invalid movie role";
    public static final String INVALID_CHARACTER_NAME = "Character name must be lower than 255 characters";
    public static final String INVALID_DURATION = "Duration must be between 1 and 1440 minutes";
    public static final String INVALID_CREW_ROLE = "Crew cannot be empty";
    public static final String MAX_TITLE_CHARACTERS = "Title must be lower than 255 characters";
    public static final String UNAUTHORIZED_UPDATE = "You are not authorized to update this entity because you are not the creator";
    public static final String UNAUTHORIZED_DELETE = "You are not authorized to delete this entity because you are not the creator";
    public static final String INVALID_MEDIA_URL = "Media URL cannot be empty";
    public static final String DUPLICATE_USER = "User already exists with this id: ";
    public static final String INVALID_PLATFORMS = "Platforms cannot be empty";
    public static final String INVALID_GENRES = "Genres cannot be empty";
    public static final String INVALID_DEVELOPER = "Developer cannot be empty";
    public static final String CHARACTER_NAME_REQUIRED = "Character name is required for actors";


    private Messages() {
    }
}
