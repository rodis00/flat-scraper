package com.example.flatscraper.groq;

import com.example.flatscraper.flat.FlatRequest;
import com.example.flatscraper.flat.FlatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.function.Function;

@RestController
@RequestMapping("api/v1/chat")
@Slf4j
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final ChatClient chatClient;
    private final FlatService flatService;

    public ChatController(ChatClient.Builder builder, FlatService flatService) {
        this.chatClient = builder.build();
        this.flatService = flatService;
    }

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(@RequestBody ChatRequest chatRequest) {
        String response = chatClient.prompt()
                .user(chatRequest.message())
                .call()
                .content();
        return ResponseEntity.ok(new ChatResponse(response));
    }

    @PostMapping("/predict-price")
    public ResponseEntity<ChatResponse> predictPrice(@RequestBody ChatRequest chatRequest) {
        FlatRequest flatRequest = extractFlatData(chatRequest.message());

        String response = chatClient.prompt()
                .user(flatRequest.toString())
                .functions("knnFunction")
                .call()
                .content();
        return ResponseEntity.ok(new ChatResponse(response));
    }

    public FlatRequest extractFlatData(String message) {
        String response = chatClient.prompt()
                .user("wydobądź odpowiednie dane z wiadomości: " + message +
                        " i zwróć tylko obiekt formacie JSON { area, rooms, floor, yearOfConstruction}" +
                        " jeśli nie uda się wydobyć danych zwróć pusty obiekt lub null dla brakujących danych")
                .call()
                .content();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = extractDataBetweenBrackets(response);
            return objectMapper.readValue(json, FlatRequest.class);
        } catch (Exception e) {
            log.error("Nie udało się wydobyć danych z wiadomości: ", e);
            throw new RuntimeException("Nie udało się wydobyć danych z wiadomości");
        }
    }

    public String extractDataBetweenBrackets(String response) {
        int start = response.indexOf("{");
        int end = response.indexOf("}");
        return response.substring(start, end + 1);
    }

    @Bean
    @Description("Oszacuj cenę mieszkania za pomocą knn i zwróć odpowiedź w języku polskim lub takim w jakim było pytanie")
    public Function<FlatRequest, ChatResponse> knnFunction() {
        return flatRequest -> {
            double predictedPrice = flatService.predictFlatPrice(flatRequest);
            return new ChatResponse(String.valueOf(predictedPrice));
        };
    }
}
