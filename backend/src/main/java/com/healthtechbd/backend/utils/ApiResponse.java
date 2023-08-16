package com.healthtechbd.backend.utils;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse {
    private Map<String, String> responseMap = new HashMap<>();

    private ApiResponse(String key, String value) {
        responseMap.put(key, value);
    }

    public ApiResponse()
    {}


    public static ApiResponse create(String key, String value) {
        return new ApiResponse(key, value);
    }

    public boolean isEmpty()
    {
        return responseMap.size()==0?true:false;
    }


    public Map<String, String> getResponseMap() {
        return responseMap;
    }
}