package com.erywim.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Date 2025/12/27
 */
public class ChatTest {
    OpenAiChatModel model = OpenAiChatModel.builder()
            .apiKey(System.getenv("deepseek-key"))
            .modelName("deepseek-chat")
            .baseUrl("https://api.deepseek.com")
            .build();
    OpenAiStreamingChatModel streamingModel = OpenAiStreamingChatModel.builder()
            .apiKey(System.getenv("deepseek-key"))
            .modelName("deepseek-chat")
            .baseUrl("https://api.deepseek.com")
            .build();
    @Test
    public void chat1(){
        ChatRequest request = ChatRequest.builder()
                .messages(UserMessage.from("你好，你是谁，你的模型名称是什么"))
                .modelName("deepseek-reasoner").build();
//        System.out.println("model.chat(\"你好，你是谁\") = " + model.chat("你好，你是谁"));
        ChatResponse chatResponse = model.chat(request);
        System.out.println("model.chat(request) = " + chatResponse);
        TokenUsage tokenUsage = chatResponse.tokenUsage();
        System.out.println("输入token = " + tokenUsage.inputTokenCount());
        System.out.println("输出token = " + tokenUsage.outputTokenCount());
        System.out.println("总token = " + tokenUsage.totalTokenCount());

        AiMessage aiMessage = chatResponse.aiMessage();
        System.out.println("aiMessage.thinking() = " + aiMessage.thinking());
        System.out.println("aiMessage.type() = " + aiMessage.type());
    }

    @Test
    public void testChatRequestStream() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AiServiceChatWithMemory service = AiServices.builder(AiServiceChatWithMemory.class)
                .streamingChatModel(streamingModel)
                .chatRequestTransformer((chatRequest, object) ->
                        chatRequest.toBuilder()
                                .modelName("deepseek-reasoner")
                                .parameters(null)
                                .build())
                .build();
        Flux<String> chat = service.chat("你好，你是谁");

        chat.doOnSubscribe(subscription -> System.out.println("开始订阅"))
                .doOnNext(token -> System.out.print(token))
                .doOnError(error -> System.err.println("错误: " + error))
                .doOnComplete(() -> {
                    System.out.println("完成");
                    countDownLatch.countDown();
                })
                .subscribe();
        countDownLatch.await();
    }

    @Test
    public void testChatStreamResponse() throws InterruptedException {
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        AtomicReference<ChatResponse> metadataHolder = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 2. 手动调底层 API（仅在这个方法回落）
        streamingModel.chat("你好，你是谁", new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String token) {
//                System.out.println("token = " + token);
                sink.tryEmitNext(token);
            }

            @Override
            public void onCompleteResponse(ChatResponse response) {
                System.out.println("\n1. 进入 onCompleteResponse");
                sink.tryEmitComplete();
                System.out.println("2. tryEmitComplete 返回");
                // 3. 在这里才能拿到元数据
                metadataHolder.set(ChatResponse.builder()
                        .aiMessage(response.aiMessage())
                        .tokenUsage(response.tokenUsage())
                        .finishReason(response.finishReason())
                        .build());
                System.out.println("3. metadataHolder 已设置");
                // 3. 最后 countDown（确保 set 已完成）
                countDownLatch.countDown();
            }

            @Override
            public void onError(Throwable error) {
                sink.tryEmitError(error);
            }
        });

        sink.asFlux()
                .doOnSubscribe(subscription -> System.out.println("开始订阅"))
                .doOnNext(System.out::print)
                .doOnError(error -> System.err.println("错误: " + error))
                .doOnComplete(() -> {
                    System.out.println("完成");
                })
                .subscribe();
        countDownLatch.await();
        System.out.println("metadataHolder.get() = " + metadataHolder.get());
    }

    @Test
    public void testEasyRag(){
        //1. 加载文档
        Document document = FileSystemDocumentLoader.loadDocument("src/main/resources/static/王麻子自传.txt");
        // 2. 为文档及其嵌入创建了一个空的内存存储。
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        // 3. 将文档导入到内存存储中
        EmbeddingStoreIngestor.ingest(document,embeddingStore);
        // 4. 创建一个在内存存储的检索器
        EmbeddingStoreContentRetriever embeddingRetriever = EmbeddingStoreContentRetriever.from(embeddingStore);
        //5. 给对话提供检索能力
        String result = AiServices.builder(AiServiceChat.class)
                .chatModel(model)
                .contentRetriever(embeddingRetriever)
                .build().chat("王麻子是谁");
        System.out.println("result = " + result);
    }

    @Test
    public void testNaiveRag(){
        //1. 创建对话模型
        OpenAiChatModel model = OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com")
                .apiKey(System.getenv("deepseek-key"))
                .modelName("deepseek-chat")
                .build();
        //2. 创建需要通过RAG检索的文档
        DocumentParser documentParser = new TextDocumentParser();
        Document document = FileSystemDocumentLoader.loadDocument("src/main/resources/static/王麻子自传.txt", documentParser);

        //3. 切割document为更小的segment，我们也称其为chunks
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segmentList = splitter.split(document);

        //4. 向量化这些segments
        OpenAiEmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .apiKey(System.getenv("qwen-key"))
                .modelName("text-embedding-v4")
                .build();
        List<Embedding> embeddings = embeddingModel.embedAll(segmentList).content();

        //5. 将向量化的内容放入向量库中
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segmentList);

        //6. 创建内容检索器负责将用户提问的内容从向量库中检索出相关内容
        // 当前版本能够检索文本chunk，之后可能会支持多模态检索
        EmbeddingStoreContentRetriever retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .maxResults(2)
                .minScore(0.5)
                .build();

        AiServiceChat build = AiServices.builder(AiServiceChat.class)
                .chatModel(model)
                .contentRetriever(retriever)
                .build();

        System.out.println("build.chat= " + build.chat("简要概述王麻子的历程"));

    }



}
