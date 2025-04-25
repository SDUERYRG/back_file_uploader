package com.example.demo.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * 生成6位数的邮件验证码
 *
 * @Author yrg
 * @Date 2024/7/19 15:53
 */
@Component
public class CodeUtils {
    public Map<String, Object> generateCode(String email) { //生成6位随机加密验证码，同时记录生成时间
        long timestamp = System.currentTimeMillis();
        int hash = email.hashCode();
        long result = hash ^ 20221012;  //加密
        result = result ^ System.currentTimeMillis();
        String code = String.valueOf(result);
        // 为避免变量名冲突，将变量名从 result 改为 resultMap
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", code.substring(code.length() - 6));
        resultMap.put("timestamp", timestamp);
        
        return resultMap;
    }
}