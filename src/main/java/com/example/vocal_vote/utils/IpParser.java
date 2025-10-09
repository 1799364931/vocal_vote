package com.example.vocal_vote.utils;

import jakarta.servlet.http.HttpServletRequest;

public class IpParser {
    public static String parse(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            return ip.split(",")[0].trim(); // 多级代理时取第一个
        }
        ip = request.getHeader("X-Real-IP");
        return (ip == null || ip.isEmpty()) ? request.getRemoteAddr() : ip;
    }
}
