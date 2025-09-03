package com.test.permissionusesjwt.service;

import com.test.permissionusesjwt.authUtils.AuthUtils;
import com.test.permissionusesjwt.configuration.VNPayConfig;
import com.test.permissionusesjwt.dto.request.CartCreateRequest;
import com.test.permissionusesjwt.dto.request.CartItemRequest;
import com.test.permissionusesjwt.dto.request.PaymentRequest;
import com.test.permissionusesjwt.dto.response.CartResponse;
import com.test.permissionusesjwt.entity.User;
import com.test.permissionusesjwt.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import jakarta.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class VNPayService {
    private final VNPayConfig vnpayConfig;
    AuthUtils authUtils;
    UserRepository userRepository;

    public String createPaymentUrl(String ipAddress, com.test.permissionusesjwt.entity.Order order) throws UnsupportedEncodingException {
        String vnp_TxnRef = order.getId();
        String vnp_OrderInfo = "Thanh toan khoa hoc";
        String vnp_OrderType = "other";
        String vnp_Amount = String.valueOf(order.getTotalAmount().intValue() * 100); // VNPay yêu cầu số tiền *100
        String vnp_Locale = "vn";
        String vnp_BankCode = "NCB";
        String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

        calendar.add(Calendar.MINUTE, 15);
        String expireDate = formatter.format(calendar.getTime());

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnpayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_IpAddr", ipAddress);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_ReturnUrl", vnpayConfig.getReturnUrl());
        vnp_Params.put("vnp_ExpireDate", expireDate);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_BankCode",vnp_BankCode);    


        // Sort params
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String value = vnp_Params.get(fieldName);
            if (value != null && !value.isEmpty()) {
                hashData.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append('&');
                query.append(fieldName).append('=').append(URLEncoder.encode(value, StandardCharsets.UTF_8)).append('&');
            }
        }

        // Remove last '&'
        hashData.setLength(hashData.length() - 1);
        query.setLength(query.length() - 1);

        String secureHash = hmacSHA512(vnpayConfig.getHashSecret(), hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);

        return vnpayConfig.getPayUrl() + "?" + query;
    }


    private String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : bytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString().toUpperCase();

        } catch (Exception ex) {
            throw new RuntimeException("Lỗi khi tạo chữ ký", ex);
        }
    }


    public Map<String, String> extractParams(HttpServletRequest request) {
        return request.getParameterMap().entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));
    }

    public boolean validateSignature(Map<String, String> params, String receivedHash) {
        // Bỏ hash trước khi tính toán
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        String data = buildHashData(params);
        String myHash = hmacSHA512(vnpayConfig.getHashSecret(), data);
        return myHash.equalsIgnoreCase(receivedHash);
    }

    private String buildHashData(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String k : keys) {
            String v = params.get(k);
            if (v != null && !v.isEmpty()) {
                sb.append(k).append('=')
                        .append(URLEncoder.encode(v, StandardCharsets.UTF_8)
                                .replace("%20", "+")); // quan trọng!
                sb.append('&');
            }
        }
        if (sb.length() > 0) sb.setLength(sb.length() - 1);
        return sb.toString();
    }




}
