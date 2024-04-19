package org.somuga.message;

public class Messages {

    public static final String USER_NOT_FOUND = "No user found with id: ";
    public static final String REVIEW_NOT_FOUND = "No review found with id: ";
    public static final String NON_EMPTY_USERNAME = "Username cannot be empty";
    public static final String INVALID_USERNAME = "Username must be between 4 and 20 characters, and can only contain letters, numbers, - and spaces";
    public static final String NON_EMPTY_EMAIL = "Email cannot be empty";
    public static final String INVALID_EMAIL = "Invalid email";
    public static final String DUPLICATE_EMAIL = "Email already exists";
    public static final String DUPLICATE_USERNAME = "Username already exists";
    public static final String ID_GREATER_THAN_0 = "Id must be greater than 0";
    public static final String ALREADY_LIKED = "User already liked this media";
    public static final String ALREADY_REVIED = "User already reviewed this media";
    public static final String INVALID_SCORE = "The review score must be between 1 and 10";
    public static final String MAX_REVIEW_CHARACTERS = "Review must be lower that 1024 characters";


    private Messages() {
    }
}
