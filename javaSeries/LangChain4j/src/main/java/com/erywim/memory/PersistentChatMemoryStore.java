package com.erywim.memory;

import com.erywim.dao.ChatMemoryDao;
import com.erywim.entity.ChatMemory;
import dev.langchain4j.data.message.*;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Date 2025/12/29
 */
@Component
@Slf4j
public class PersistentChatMemoryStore implements ChatMemoryStore {
    @Autowired
    private ChatMemoryDao memoryDao;


    @Override
    public List<ChatMessage> getMessages(Object o) {
        log.info("查询用户 {} 的对话记录", o);
        List<ChatMemory> chatMemories = memoryDao.queryByUserId(o);
        List<ChatMessage> list = new ArrayList<>();
        for (ChatMemory chatMemory : chatMemories) {
            if (chatMemory.getRole().equals(ChatMessageType.USER.name())) {
                list.add(new UserMessage(chatMemory.getMessage()));
            } else if (chatMemory.getRole().equals(ChatMessageType.AI.name())) {
                list.add(new AiMessage(chatMemory.getMessage()));
            } 
        }
        return list;
    }

    @Override
    public void updateMessages(Object o, List<ChatMessage> list) {
        //这里为了实现简单就不做saveOrUpdate了，直接删了加
        memoryDao.deletaByUserId(o);

        System.out.println("message list size = " + list.size());
        List<ChatMemory> chatMemoryList = new ArrayList<>();
        for (ChatMessage chatMessage : list) {
            if (chatMessage.type() == ChatMessageType.USER) {
                UserMessage message = (UserMessage) chatMessage;
                message.contents().forEach(content -> {
                    TextContent textContent = (TextContent) content;
                    String text = textContent.text();
                    ChatMemory chatMemory = new ChatMemory(Long.valueOf(String.valueOf(o)), text, ChatMessageType.USER.name());
                    chatMemoryList.add(chatMemory);
                });
            }else if(chatMessage.type() == ChatMessageType.AI){
                AiMessage message = (AiMessage) chatMessage;
                ChatMemory chatMemory = new ChatMemory(Long.valueOf(String.valueOf(o)), message.text(), ChatMessageType.AI.name());
                chatMemoryList.add(chatMemory);
            }else if(chatMessage.type() == ChatMessageType.TOOL_EXECUTION_RESULT){
                ToolExecutionResultMessage message = (ToolExecutionResultMessage) chatMessage;
                ChatMemory chatMemory = new ChatMemory(Long.valueOf(String.valueOf(o)), message.text(), ChatMessageType.TOOL_EXECUTION_RESULT.name());
                chatMemoryList.add(chatMemory);
            }else if (chatMessage.type() == ChatMessageType.CUSTOM){
                CustomMessage message = (CustomMessage) chatMessage;
                System.out.println("message.attributes() = " + message.attributes());
            }else if (chatMessage.type() == ChatMessageType.SYSTEM){
                //系统提示词应该只保存一次
                SystemMessage message = (SystemMessage) chatMessage;
                ChatMemory chatMemory = new ChatMemory(Long.valueOf(String.valueOf(o)), message.text(), ChatMessageType.SYSTEM.name());
                chatMemoryList.add(chatMemory);
            }
        }

        memoryDao.save(o, chatMemoryList);
        log.info("更新用户 {} 的对话记录", o);
    }

    @Override
    public void deleteMessages(Object o) {
        memoryDao.deletaByUserId(o);
        log.info("删除用户 {} 的对话记录", o);
    }
}
