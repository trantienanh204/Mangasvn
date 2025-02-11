package com.example.demo.Controller.thanhtoanonline;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class VNPayService {

    //    @Value("${vnpay.hash-secret}")
//    private String vnpHashSecret;
//
//    @Value("${vnpay.tmn-code}")
//    private String vnpTmnCode;
//
//    @Value("${vnpay.pay-url}")
//    private String vnpPayUrl;
//
//    @Value("${vnpay.return-url}")
//    private String vnpReturnUrl;
    private static String vnpHashSecret = "QTSXO9PQEVDJCIRCWJVTDEKW4DLPIB17";
    private static String vnpPayUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private static String vnp_TmnCode = "0AR5ALJG";
    private static String vnp_ReturnUrl = "http://localhost:8080/api/vnpay-payment";

    public static String createOrder(int total, String orderInfo) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_TxnRef = UUID.randomUUID().toString();
        String vnp_IpAddr = "127.0.0.1";
        String orderType = "order-type";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(total * 100));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName).append('=').append(encode(fieldValue)).append('&');
                query.append(encode(fieldName)).append('=').append(encode(fieldValue)).append('&');
            }
        }

        hashData.deleteCharAt(hashData.length() - 1);
        query.deleteCharAt(query.length() - 1);

        String vnp_SecureHash = hmacSHA512(vnpHashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(encode(vnp_SecureHash));

        return vnpPayUrl + "?" + query.toString();
    }

    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String hmacSHA512(String key, String data) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            Mac mac = Mac.getInstance("HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hashBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte b : hashBytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to calculate HMAC-SHA512 hash", e);
        }
    }

    public int orderReturn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            fields.put(entry.getKey(), entry.getValue()[0]);
        }
        String vnpSecureHash = fields.remove("vnp_SecureHash");
        StringBuilder hashData = new StringBuilder();
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        for (String fieldName : fieldNames) {
            if (!fieldName.equalsIgnoreCase("vnp_SecureHash")) {
                hashData.append(fieldName).append('=').append(fields.get(fieldName)).append('&');
            }
        }
        if (hashData.length() > 0) {
            hashData.deleteCharAt(hashData.length() - 1);
        }
//        String computedHash = hmacSHA512(vnpHashSecret, hashData.toString());
////        if (!vnpSecureHash.equalsIgnoreCase(computedHash)) {
////            System.out.println("Invalid signature: " + computedHash);
////            return 0;
////        }
        String responseCode = fields.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            System.out.println("Payment successful: " + fields);
            return 1;
        } else {
            System.out.println("Payment failed with response code: " + responseCode);
            return -1;
        }


    }
}
