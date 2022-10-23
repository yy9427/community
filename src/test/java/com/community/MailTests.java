package com.community;

import com.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class MailTests {


    @Autowired
    private MailClient mailClient;

    @Test
    public void testMail() {
        mailClient.sendMail("78697841@qq.com", "欢迎注册此网站", "验证码为:" + (int) (100000 + Math.random() * 900000));

    }


}
