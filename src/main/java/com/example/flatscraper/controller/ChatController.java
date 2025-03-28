package com.example.flatscraper.controller;

import com.example.flatscraper.record.FlatRequestDto;
import com.example.flatscraper.service.FlatService;
import com.example.flatscraper.record.ChatRequestDto;
import com.example.flatscraper.record.ChatResponseDto;
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
    public ResponseEntity<ChatResponseDto> ask(@RequestBody ChatRequestDto chatRequestDto) {
        String response = chatClient.prompt()
                .user(chatRequestDto.message())
                .call()
                .content();
        return ResponseEntity.ok(new ChatResponseDto(response));
    }

    @PostMapping("/predict-price")
    public ResponseEntity<ChatResponseDto> predictPrice(@RequestBody ChatRequestDto chatRequestDto) {
        FlatRequestDto flatRequestDto = extractFlatData(chatRequestDto.message());

        String response = chatClient.prompt()
                .user(flatRequestDto.toString())
                .functions("knnFunction")
                .call()
                .content();
        return ResponseEntity.ok(new ChatResponseDto(response));
    }

    public FlatRequestDto extractFlatData(String message) {
        String response = chatClient.prompt()
                .user("wydobądź odpowiednie dane z wiadomości: " + message +
                        " i zwróć tylko obiekt formacie JSON { area, rooms, floor, yearOfConstruction}" +
                        " jeśli nie uda się wydobyć danych zwróć pusty obiekt lub null dla brakujących danych")
                .call()
                .content();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = extractDataBetweenBrackets(response);
            return objectMapper.readValue(json, FlatRequestDto.class);
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
    public Function<FlatRequestDto, ChatResponseDto> knnFunction() {
        return flatRequestDto -> {
            double predictedPrice = flatService.predictFlatPrice(flatRequestDto);
            return new ChatResponseDto(String.valueOf(predictedPrice));
        };
    }
}
