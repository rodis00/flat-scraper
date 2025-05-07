package com.example.flatscraper.service;

import com.example.flatscraper.model.Flat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScraperService {
    private final String baseUrl = "https://www.otodom.pl/pl/wyniki/sprzedaz/mieszkanie/warminsko--mazurskie/olsztyn/olsztyn/olsztyn?viewType=listing";
    private final String prefix = "https://www.otodom.pl";
    private final FlatService flatService;
    private final Logger log = LoggerFactory.getLogger(ScraperService.class);

    public ScraperService(FlatService flatService) {
        this.flatService = flatService;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 60 * 5) // every 5 hours
    public void run() throws IOException {
        scrapeFlats();
    }

    @Async
    public void scrapeFlats() throws IOException {
        // TODO: jakos to ogarnac
        int pages = 33;
        for (int i = 1; i <= pages; i++) {
            log.info("Scraping page: {}", i);
            String pageUrl = baseUrl + "&page=" + i;
            List<String> flatLinks = getFlatLinksFromPage(pageUrl);

            List<Flat> flats = new ArrayList<>();
            for (String flatLink : flatLinks) {
                Flat flat = scrapeFlat(flatLink);
                if (flat != null) {
                    flats.add(flat);
                }
            }

            validateFlats(flats);
            flatService.saveAll(flats);
        }
        log.info("Scraping finished");
    }

    private void validateFlats(List<Flat> flats) {
        flats.removeIf(flat -> flat.getPrice().isEmpty() && flat.getArea().isEmpty() && flat.getRooms().isEmpty());
    }

    private List<String> getFlatLinksFromPage(String flatUrl) throws IOException {
        Document doc = Jsoup.connect(flatUrl).get();
        List<String> flatsLinks = new ArrayList<>();
        Elements offers = doc.select("a[data-cy=listing-item-link]");
        if (offers.isEmpty()) {
            log.error("Cannot find offers");
            return flatsLinks;
        }
        for (Element offer : offers) {
            String link = offer.attr("href");
            if (link.startsWith("/pl/oferta")){
                flatsLinks.add(prefix + link);}
        }
        return flatsLinks;
    }

    private Flat scrapeFlat(String link) throws IOException {

        if (flatService.checkIfFlatExists(link)) {
            log.info("Flat already exists in database");
            return null;
        }

        Document doc = Jsoup.connect(link).get();

        Flat flat = new Flat();
        flat.setUrl(link);

        String title = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[1]/h1").text();
        flat.setTitle(title);

        String price = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[1]/div[1]/div[1]/strong").text();
        flat.setPrice(price);

        String pricePerMeter = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[1]/div[1]/div[2]/div").text();
        flat.setPricePerMeter(pricePerMeter);

        String address = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[1]/div[2]/a").text();
        flat.setAddress(address);

        String area = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[1]/div[1]/p[2]").text();
        flat.setArea(area);

        String rooms = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[1]/div[3]/p[2]").text();
        flat.setRooms(rooms);

        Element picture = doc.selectFirst("picture");
        String imageUrl = null;
        if (picture != null) {
            Element source = picture.selectFirst("source[media=\"(min-width: 768px)\"]");
            imageUrl = (source != null) ? source.attr("srcset") : picture.selectFirst("img").attr("src");
        }
        flat.setImageUrl(imageUrl);

        String[] fieldNames = {
                "Ogrzewanie", "Piętro", "Czynsz", "Stan wykończenia",
                "Rynek", "Forma własności", "Dostępne od", "Typ ogłoszeniodawcy",
                "Informacje dodatkowe", "Rok budowy", "Winda", "Rodzaj zabudowy",
                "Materiał budynku", "Okna", "Certyfikat energetyczny", "Bezpieczeństwo",
                "Zabezpieczenia", "Media"
        };


        Map<String, String> attributes = new HashMap<>();
        for (String fieldName : fieldNames) {
            Element parent = doc.select("div p").stream()
                    .filter(p -> p.text().trim().equals(fieldName + ":"))
                    .findFirst()
                    .orElse(null);

            if (parent != null) {
                Element child = parent.nextElementSibling();
                if (child != null && !child.text().isEmpty() && !child.text().equals("brak informacji")) {
                    attributes.put(fieldName, child.text());
                }
            }
        }

        flat.setHeating(attributes.get(fieldNames[0]));
        flat.setFloor(attributes.get(fieldNames[1]));
        flat.setRent(attributes.get(fieldNames[2]));
        flat.setStateOfFinishing(attributes.get(fieldNames[3]));
        flat.setMarket(attributes.get(fieldNames[4]));
        flat.setFormOfOwnership(attributes.get(fieldNames[5]));
        flat.setAvailableFrom(attributes.get(fieldNames[6]));
        flat.setAdvertiserType(attributes.get(fieldNames[7]));
        flat.setAdditionalInfo(attributes.get(fieldNames[8]));
        flat.setYearOfConstruction(attributes.get(fieldNames[9]));
        flat.setElevator(attributes.get(fieldNames[10]));
        flat.setBuildingType(attributes.get(fieldNames[11]));
        flat.setBuildingMaterial(attributes.get(fieldNames[12]));
        flat.setWindows(attributes.get(fieldNames[13]));
        flat.setEnergyCertificate(attributes.get(fieldNames[14]));
        flat.setSafety(attributes.get(fieldNames[15]));
        flat.setSecurity(attributes.get(fieldNames[16]));
        flat.setMedia(attributes.get(fieldNames[17]));

        return flat;
    }
}
