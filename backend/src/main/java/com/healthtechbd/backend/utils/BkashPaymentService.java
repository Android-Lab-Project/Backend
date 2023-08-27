package com.healthtechbd.backend.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BkashPaymentService {

    private static final String API_BASE_URL = "https://tokenized.sandbox.bka.sh/v1.2.0-beta";

    private static final String APP_KEY = "4f6o0cjiki2rfm34kfdadl1eqq";

    public static String paymentID;

    public static String createPayment(String callbackURL, String amount) {

        final String CREATE_PAYMENT_ENDPOINT = "/tokenized/checkout/create";

        String token = grantToken();

        String AUTHORIZATION_TOKEN = "Bearer " + token;


        //Invoice generator
        String uniqueIdentifier = UUID.randomUUID().toString().substring(0, 6);
        String timestamp = Instant.now().toString();
        String truncatedTimestamp = timestamp.substring(timestamp.length() - 6);
        String merchantInvoiceNumber = "INV" + uniqueIdentifier + truncatedTimestamp;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", AUTHORIZATION_TOKEN);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("x-app-key", APP_KEY);

        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("mode", "0011");
        paymentRequest.put("payerReference", "###");
        paymentRequest.put("callbackURL", callbackURL);
        paymentRequest.put("amount", amount);
        paymentRequest.put("currency", "BDT");
        paymentRequest.put("intent", "sale");
        paymentRequest.put("merchantInvoiceNumber", merchantInvoiceNumber);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(paymentRequest, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                API_BASE_URL + CREATE_PAYMENT_ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonResponse = jsonParser.parse(responseBody).getAsJsonObject();
            String bkashURL = jsonResponse.get("bkashURL").getAsString();
            paymentID = jsonResponse.get("paymentID").getAsString();

            return bkashURL;
        } else {
            return "Error";
        }
    }

    public static String queryPayment() {
        final String QUERY_PAYMENT_ENDPOINT = "/tokenized/checkout/payment/status";

        String token = grantToken();
        String AUTHORIZATION_TOKEN = "Bearer " + token;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", AUTHORIZATION_TOKEN);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("x-app-key", APP_KEY);

        Map<String, Object> queryRequest = new HashMap<>();
        System.out.println(paymentID);
        queryRequest.put("paymentID", paymentID);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(queryRequest, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                API_BASE_URL + QUERY_PAYMENT_ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String queryResponse = responseEntity.getBody();

            String responseBody = responseEntity.getBody();

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonResponse = jsonParser.parse(responseBody).getAsJsonObject();
            String verificationStatus = jsonResponse.get("verificationStatus").getAsString();
            return verificationStatus;

        } else {
            return "Error";
        }
    }

    public static String executePayment() {
        final String QUERY_PAYMENT_ENDPOINT = "/tokenized/checkout/execute";

        String token = grantToken();
        String AUTHORIZATION_TOKEN = "Bearer " + token;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", AUTHORIZATION_TOKEN);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("x-app-key", APP_KEY);

        Map<String, Object> queryRequest = new HashMap<>();
        System.out.println(paymentID);
        queryRequest.put("paymentID", paymentID);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(queryRequest, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                API_BASE_URL + QUERY_PAYMENT_ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String executeResponse = responseEntity.getBody();

            String responseBody = responseEntity.getBody();

            System.out.println(responseBody);

//            JsonParser jsonParser = new JsonParser();
//            JsonObject jsonResponse = jsonParser.parse(responseBody).getAsJsonObject();
//            String verificationStatus = jsonResponse.get("verificationStatus").getAsString();

        } else {
            return "Error";
        }

        return null;
    }

    public static String grantToken() {

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"app_key\":\"4f6o0cjiki2rfm34kfdadl1eqq\",\"app_secret\":\"2is7hdktrekvrbljjh44ll3d9l1dtjo4pasmjvs5vl5qr3fug4b\"}");
        Request request = new Request.Builder()
                .url("https://tokenized.sandbox.bka.sh/v1.2.0-beta/tokenized/checkout/token/grant")
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("username", "sandboxTokenizedUser02")
                .addHeader("password", "sandboxTokenizedUser02@12345")
                .addHeader("content-type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                com.google.gson.JsonObject jsonResponse = parser.parse(responseBody).getAsJsonObject();
                String idToken = jsonResponse.get("id_token").getAsString();

                return idToken;
            } else {
                return "Error";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {

        //String paymentResponse = BkashPaymentService.createPayment("https://www.google.com","300");

        //System.out.println("Payment Response: " + paymentResponse);

        //System.out.println(BkashPaymentService.paymentID);

        BkashPaymentService.paymentID = "TR0011kIRbq631692897287567";

        System.out.println(BkashPaymentService.queryPayment());

        BkashPaymentService.executePayment();

        System.out.println(BkashPaymentService.queryPayment());


    }
}
