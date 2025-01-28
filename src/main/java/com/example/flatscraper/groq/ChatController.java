package com.example.flatscraper.groq;

import com.example.flatscraper.flat.FlatRequest;
import com.example.flatscraper.flat.FlatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;

@RestController
@RequestMapping("api/v1/chat")
public class ChatController {

    private final ChatClient chatClient;
    private final FlatService flatService;

    public ChatController(ChatClient.Builder builder, FlatService flatService) {
        this.chatClient = builder.build();
        this.flatService = flatService;
    }

    @GetMapping("/ask")
    public ChatResponse ask(@RequestParam(value = "message") String message) {
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();
        return new ChatResponse(response);
    }

    @PostMapping("/predict-price")
    public ChatResponse predictPrice(@RequestBody FlatRequest flatRequest) {
        String response = chatClient.prompt()
                .user("Jak jest prawdopodobna cena takiego mieszkania: " + flatRequest.toString())
                .functions("knnFunction")
                .call()
                .content();
        return new ChatResponse(response);
    }

    @Bean
    @Description("Oszacuj cenę mieszkania za pomocą knn")
    public Function<FlatRequest, ChatResponse> knnFunction() {
        return flatRequest -> {
            double predictedPrice = flatService.predictFlatPrice(flatRequest);
            return new ChatResponse(String.valueOf(predictedPrice));
        };
    }
}
