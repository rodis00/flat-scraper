package com.example.flatscraper.controller;

import com.example.flatscraper.dto.FlatRequestDto;
import com.example.flatscraper.service.FlatService;
import com.example.flatscraper.dto.ChatRequestDto;
import com.example.flatscraper.dto.ChatResponseDto;
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
        String message = chatRequestDto.message().toLowerCase();

        boolean hasEnoughData = message.contains("powierzchnia") &&
                message.contains("pokoi") &&
                (message.contains("rok budowy") || message.contains("rok") || message.contains("zbudowane")) &&
                message.contains("piętro");

        if (!hasEnoughData) {
            return ResponseEntity.ok(new ChatResponseDto(
                    "<p class='text-warning'>Twoja wiadomość nie zawiera wystarczających informacji o mieszkaniu. " +
                            "Podaj proszę przynajmniej: <strong>powierzchnię</strong>, <strong>liczbę pokoi</strong>, " +
                            "<strong>rok budowy</strong> i <strong>piętro</strong>.</p>"
            ));
        }

        FlatRequestDto flatRequestDto = extractFlatData(message);
        double predictedPrice = flatService.predictFlatPrice(flatRequestDto);

        if (message.contains("ogłoszenie") || message.contains("opis")) {
            String adPrompt = String.format(
                    "Na podstawie następujących danych o mieszkaniu:\n" +
                            "- Powierzchnia: %.2f m2\n" +
                            "- Liczba pokoi: %d\n" +
                            "- Rok budowy: %d\n" +
                            "- Piętro: %d\n" +
                            "- Cena: %.0f PLN\n\n" +
                            "Wygeneruj profesjonalny opis ogłoszenia w HTML w języku rozmówcy",
                    flatRequestDto.area(),
                    flatRequestDto.rooms(),
                    flatRequestDto.yearOfConstruction(),
                    flatRequestDto.floor(),
                    predictedPrice
            );

            String adHtml = chatClient.prompt().user(adPrompt).call().content();
            return ResponseEntity.ok(new ChatResponseDto(adHtml));
        }

        return ResponseEntity.ok(new ChatResponseDto(
                "Szacowana cena mieszkania to około <strong>" + (long) predictedPrice + " PLN</strong>."
        ));
    }


    public FlatRequestDto extractFlatData(String message) {
        String response = chatClient.prompt()
                .user("Z wiadomości: \"" + message +
                        "\" wydobądź dane i zwróć **tylko poprawny JSON** w formacie: " +
                        "{ \"area\": liczba, \"rooms\": liczba, \"floor\": liczba, \"yearOfConstruction\": liczba }." +
                        " Pomiń jednostki (np. m2) i nie dodawaj komentarzy, tekstu przed ani po JSONie."
                )
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
    @Description("Oszacuj cenę mieszkania za pomocą KNN i zwróć odpowiedź")
    public Function<FlatRequestDto, ChatResponseDto> knnFunction() {
        return flatRequestDto -> {
            double predictedPrice = flatService.predictFlatPrice(flatRequestDto);
            return new ChatResponseDto(String.valueOf(predictedPrice));
        };
    }
}
