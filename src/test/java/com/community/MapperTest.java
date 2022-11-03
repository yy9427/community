package com.community;

import com.community.entity.DiscussPost;
import com.community.entity.Message;
import com.community.mapper.DiscussPostMapper;
import com.community.mapper.MessageMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MapperTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void testSelectOists() {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 10);
        for (DiscussPost post : list) {
            System.out.println(post);

        }
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println("总行数为：" + rows);
    }


    @Test
    public void messageTest() {

        List<Message> list = messageMapper.selectConversation(111, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }

        System.out.println(messageMapper.selectConversationCount(111));

        List<Message> messages = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message : messages) {
            System.out.println(message);
        }

        int count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

    }
}
