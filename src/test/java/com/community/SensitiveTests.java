package com.community;

import com.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void test() {
        String text = "123赌博456嫖娼789吸毒";
        String filter = sensitiveFilter.filter(text);
        System.out.println(filter);

        String text1 = "123+赌++博+456+嫖7娼789吸1毒";
        String filter1 = sensitiveFilter.filter(text1);
        System.out.println(filter1);


    }
}
