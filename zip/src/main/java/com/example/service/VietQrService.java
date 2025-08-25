package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class VietQrService {

    @Value("${vietqr.base-url:https://img.vietqr.io/image}")
    private String baseUrl;

    // BANK_ID phải là slug VietQR: ví dụ: vcb, bidv, vietinbank, acb, tpb, mbbank...
    @Value("${vietqr.bank-id}")
    private String bankId;

    @Value("${vietqr.account-no}")
    private String accountNo;

    // Ví dụ: compact, compact2, qr_only, ...
    @Value("${vietqr.template:compact2}")
    private String template;

    @Value("${vietqr.account-name:Hair Harmony}")
    private String accountName;

    public String getBankCode() { return bankId; }
    public String getAccount()  { return accountNo; }

    public String buildImageUrl(long amount, String addInfo) {
        String t = (template == null || template.isBlank()) ? "compact2" : template.trim();

        // BẮT BUỘC có .png/.jpg sau template
        String ext = (t.endsWith(".png") || t.endsWith(".jpg")) ? "" : ".png";

        String path = String.format("%s-%s-%s%s", bankId.trim(), accountNo.trim(), t, ext);

        // URL-encode tham số query
        String addInfoEnc = url(addInfo);
        String accNameEnc = url(accountName);

        // Ví dụ chuẩn: https://img.vietqr.io/image/vietinbank-113366668888-compact2.png?amount=790000&addInfo=...&accountName=...
        return String.format("%s/%s?amount=%d&addInfo=%s&accountName=%s",
                baseUrl, path, amount, addInfoEnc, accNameEnc);
    }

    private static String url(String v) {
        return URLEncoder.encode(v == null ? "" : v, StandardCharsets.UTF_8);
    }
}
