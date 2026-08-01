package com.erywim.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.memory.ChatMemoryAccess;
import reactor.core.publisher.Flux;

/**
 * @Date 2025/12/20
 */
public interface AiServiceChat {
    String chat(@UserMessage String question);

}
