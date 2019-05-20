package com.diamong.happytalk.model;


import java.util.HashMap;
import java.util.Map;

public class ChatModel {
    public Map<String, Boolean> users = new HashMap<>(); //챗방 유저들
    public Map<String, Comment> comments = new HashMap<>(); //챗방 내용

    public static class Comment {
        public String uid;

        public String message;
    }
}
