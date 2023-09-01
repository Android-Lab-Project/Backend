package com.healthtechbd.backend.utils;

import org.springframework.beans.factory.annotation.Value;

public class BkashStaticVariable {
    public static String token;

    @Value("${app.bkash.callback.url}")
    public static String callBackURL;
}
