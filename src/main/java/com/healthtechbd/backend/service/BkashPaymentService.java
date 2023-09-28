package com.healthtechbd.backend.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.healthtechbd.backend.utils.*;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.healthtechbd.backend.utils.BkashStaticVariable.token;

@Service
public class BkashPaymentService {

    private final String API_BASE_URL = "https://tokenized.sandbox.bka.sh/v1.2.0-beta";
    @Value("${bkash.payment.app.key}")
    private String APP_KEY;

    @Value("${bkash.payment.app.secret}")
    private String APP_SECRET;

    public String grantToken() {

        OkHttpClient client = new OkHttpClient();

        final String CREATE_TOKEN_ENDPOINT = "/tokenized/checkout/token/grant";

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"app_key\":\"" + APP_KEY + "\",\"app_secret\":\"" + APP_SECRET + "\"}");
        Request request = new Request.Builder()
                .url(API_BASE_URL + CREATE_TOKEN_ENDPOINT)
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
            }
        } catch (Exception e) {
            return "Error";
        }

        return "Error";

    }

    public BkashCreateResponse createPayment(String amount) {

        final String CREATE_PAYMENT_ENDPOINT = "/tokenized/checkout/create";

        String token = grantToken();

        BkashStaticVariable.token = token;

        if (token.equals("Error")) {
            return new BkashCreateResponse("error", null, null, null, null);
        }

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
        paymentRequest.put("payerReference", "reference");
        paymentRequest.put("callbackURL", BkashStaticVariable.callBackURL);
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

            BkashCreateResponse bkashCreateResponse = new BkashCreateResponse();

            bkashCreateResponse.setStatus("success");

            bkashCreateResponse.setBkashURL(jsonResponse.get("bkashURL").getAsString());

            bkashCreateResponse.setPaymentId(jsonResponse.get("paymentID").getAsString());


            return bkashCreateResponse;


        } else {
            return new BkashCreateResponse("failure", null, null, null, null);
        }
    }

    public BkashQueryResponse queryPayment(String paymentID) {
        final String QUERY_PAYMENT_ENDPOINT = "/tokenized/checkout/payment/status";

        String AUTHORIZATION_TOKEN = "Bearer " + token;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", AUTHORIZATION_TOKEN);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("x-app-key", APP_KEY);

        Map<String, Object> queryRequest = new HashMap<>();

        queryRequest.put("paymentID", paymentID);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(queryRequest, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                API_BASE_URL + QUERY_PAYMENT_ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {

            String responseBody = responseEntity.getBody();

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonResponse = jsonParser.parse(responseBody).getAsJsonObject();
            BkashQueryResponse bkashQueryResponse = new BkashQueryResponse();
            bkashQueryResponse.setStatus("success");
            bkashQueryResponse.setVerificationStatus(jsonResponse.get("verificationStatus").getAsString());
            bkashQueryResponse.setTransactionStatus(jsonResponse.get("transactionStatus").getAsString());

            return bkashQueryResponse;


        } else {
            return new BkashQueryResponse("error", null, null);
        }
    }

    public BkashExecuteResponse executePayment(String paymentId) {

        final String QUERY_PAYMENT_ENDPOINT = "/tokenized/checkout/execute";


        String AUTHORIZATION_TOKEN = "Bearer " + BkashStaticVariable.token;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", AUTHORIZATION_TOKEN);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("x-app-key", APP_KEY);

        Map<String, Object> executeRequest = new HashMap<>();

        executeRequest.put("paymentID", paymentId);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(executeRequest, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                API_BASE_URL + QUERY_PAYMENT_ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {

            String responseBody = responseEntity.getBody();

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonResponse = jsonParser.parse(responseBody).getAsJsonObject();

            BkashExecuteResponse bkashExecuteResponse = new BkashExecuteResponse();

            bkashExecuteResponse.setStatus("success");

            bkashExecuteResponse.setTrxID(jsonResponse.get("trxID").getAsString());

            bkashExecuteResponse.setTransactionStatus(jsonResponse.get("transactionStatus").getAsString());

            return bkashExecuteResponse;

        } else {
            return new BkashExecuteResponse("error", null, null);
        }

    }

    public BkashRefundResponse refundPayment(String paymentId, String trxID, String amount) {

        final String REFUND_PAYMENT_ENDPOINT = "/tokenized/checkout/payment/refund";

        String token = grantToken();

        if (token.equals("Error")) {
            return new BkashRefundResponse("error", null, null);
        }

        String AUTHORIZATION_TOKEN = "Bearer " + token;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", AUTHORIZATION_TOKEN);
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("x-app-key", APP_KEY);

        Map<String, Object> refundRequest = new HashMap<>();
        refundRequest.put("paymentID", paymentId);
        refundRequest.put("amount", amount);
        refundRequest.put("trxID", trxID);
        refundRequest.put("sku", "healtechBd");
        refundRequest.put("reason", "Refund issues");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(refundRequest, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                API_BASE_URL + REFUND_PAYMENT_ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonResponse = jsonParser.parse(responseBody).getAsJsonObject();

            BkashRefundResponse bkashRefundResponse = new BkashRefundResponse();

            bkashRefundResponse.setStatus("success");
            bkashRefundResponse.setRefundTrxID(jsonResponse.get("refundTrxID").getAsString());
            bkashRefundResponse.setTransactionStatus(jsonResponse.get("transactionStatus").getAsString());

            return bkashRefundResponse;
        } else {
            return new BkashRefundResponse("error", null, null);
        }
    }

}

