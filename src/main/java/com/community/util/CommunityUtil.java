package com.community.util;

import com.github.pagehelper.util.StringUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {
    //生成随机字符串
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    //MD5加密
    public static String md5(String key) {
        if (StringUtils.isNullOrEmpty(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

}
