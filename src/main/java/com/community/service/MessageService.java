package com.community.service;

import com.community.entity.Message;
import com.community.mapper.MessageMapper;
import com.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private MessageMapper messageMapper;

    //查询当前用户的会话列表，针对每个会话只返回一条最新的私信
    //    List<Message> selectConversation(int userId,  int offset, int limit);
    public List<Message> findConversations(int userId, int offset, int limit) {
        return messageMapper.selectConversation(userId, offset, limit);
    }

    //查询当前用户的会话数量
    //    int selectConversationCount(int userId);
    public int findConversationCount(int userId) {
        return messageMapper.selectConversationCount(userId);
    }

    //查询某个会话所包含的私信列表
    //    List<Message> selectLetters(String conversationId, int offset, int limit);
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    //查询某个会话所包含的私信数量
    //    int selectLetterCount(String conversationId);
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    //查询未读私信的数量
    //    int selectLetterUnreadCount(int userId, String conversaationId);
    public int findLetterUnreadCount(int userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    //发送私信功能
    //    int insertMessage(Message message);
    public int addMessage(Message message) {
        //敏感词过滤
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageMapper.insertMessage(message);

    }

    //更改消息状态
    //    int updateStatus(List<Integer> ids, int status);
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }


}
