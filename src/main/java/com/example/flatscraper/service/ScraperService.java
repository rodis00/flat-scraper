package com.example.flatscraper.service;

import com.example.flatscraper.model.Flat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    private void scrapeFlats() throws IOException {
        int pages = 32;
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
            flatsLinks.add(prefix + link);
        }
        return flatsLinks;
    }

    private Flat scrapeFlat(String link) throws IOException {

        if (flatService.checkIfFlatExists(link)) {
            log.info("Flat already exists in database");
            return null;
        }

        Document doc = Jsoup.connect(link).get();

        String title = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[1]/h1").text();
        String price = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[1]/div[1]/div[1]/strong").text();
        String pricePerMeter = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[1]/div[1]/div[2]/div").text();
        String address = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[1]/div[2]/a").text();
        String area = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[1]/button[1]/div[2]").text();
        String rooms = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[1]/button[2]/div[2]").text();
        String heating = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[2]/div[1]/p[2]").text();
        String floor = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[2]/div[3]/p[2]").text();
        String rent = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[2]/div[5]/p[2]").text();
        String stateOfFinishing = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[2]/div[7]/p[2]").text();
        String market = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[2]/div[9]/p[2]").text();
        String formOfOwnership = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[2]/div[11]/p[2]").text();
        String availableFrom = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[2]/div[13]/p[2]").text();
        String advertiserType = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[2]/div[15]/p[2]").text();
        String additionalInfo = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[2]/div[17]/p[2]").text();

        Element picture = doc.selectFirst("picture");
        String imageUrl = null;
        if (picture != null) {
            Element source = picture.selectFirst("source[media=\"(min-width: 768px)\"]");
            imageUrl = (source != null) ? source.attr("srcset") : picture.selectFirst("img").attr("src");
        }

        String yearOfConstruction = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[3]/div/div/div[1]/p[2]").text();
        String elevator = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[3]/div/div/div[3]/p[2]").text();
        String buildingType = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[3]/div/div/div[5]/p[2]").text();
        String buildingMaterial = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[3]/div/div/div[7]/p[2]").text();

        String equipment = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[3]/div[2]/div/div[1]/p[2]").text();
        String security = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[3]/div[1]/div/div[9]/p[2]").text();
        String media = doc.selectXpath("/html/body/div[1]/div[1]/main/div[2]/div[1]/div[2]/div[3]/div[2]/div/div[5]/p[2]").text();


        Flat flat = new Flat();
        flat.setTitle(title);
        flat.setUrl(link);
        flat.setImageUrl(imageUrl);
        flat.setPrice(price);
        flat.setPricePerMeter(pricePerMeter);
        flat.setAddress(address);
        flat.setArea(area);
        flat.setRooms(rooms);
        flat.setHeating(heating);
        flat.setFloor(floor);
        flat.setRent(rent);
        flat.setStateOfFinishing(stateOfFinishing);
        flat.setMarket(market);
        flat.setFormOfOwnership(formOfOwnership);
        flat.setAvailableFrom(availableFrom);
        flat.setAdvertiserType(advertiserType);
        flat.setAdditionalInfo(additionalInfo);
        flat.setYearOfConstruction(yearOfConstruction);
        flat.setElevator(elevator);
        flat.setBuildingType(buildingType);
        flat.setBuildingMaterial(buildingMaterial);
        flat.setEquipment(equipment);
        flat.setSecurity(security);
        flat.setMedia(media);

        return flat;
    }
}
