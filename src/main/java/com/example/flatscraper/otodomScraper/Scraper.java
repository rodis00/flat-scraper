package com.example.flatscraper.otodomScraper;

import com.example.flatscraper.flat.Flat;
import com.example.flatscraper.flat.FlatRecord;
import com.example.flatscraper.flat.FlatService;
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
public class Scraper {
    private final String baseUrl = "https://www.otodom.pl/pl/wyniki/sprzedaz/mieszkanie/warminsko--mazurskie/olsztyn/olsztyn/olsztyn?viewType=listing";
    private final String prefix = "https://www.otodom.pl";
    private final FlatService flatService;
    private final Logger log = LoggerFactory.getLogger(Scraper.class);

    public Scraper(FlatService flatService) {
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

            List<FlatRecord> flats = new ArrayList<>();
            for (String flatLink: flatLinks) {
                FlatRecord flat = scrapeFlat(flatLink);
                if (flat != null) {
                    flats.add(flat);
                }
            }

            validateFlats(flats);
            flatService.saveAll(flats);
        }
        log.info("Scraping finished");
    }

    private void validateFlats(List<FlatRecord> flats) {
        flats.removeIf(flat -> flat.price().isEmpty() && flat.area().isEmpty() && flat.rooms().isEmpty());
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

    private FlatRecord scrapeFlat(String link) throws IOException {

        if (flatService.checkIfFlatExists(link)) {
            log.info("Flat already exists in database");
            return null;
        }

        Document doc = Jsoup.connect(link).get();

        String price = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[1]/div[4]/div[1]/div[1]/strong").text();
        String pricePerMeter = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[1]/div[4]/div[1]/div[2]/div").text();
        String address = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[1]/div[4]/div[2]/a").text();
        String area = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[1]/button[1]/div[2]").text();
        String rooms = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[1]/button[2]/div[2]").text();
        String heating = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[2]/div[1]/p[2]").text();
        String floor = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[2]/div[3]/p[2]").text();
        String rent = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[2]/div[5]/p[2]").text();
        String stateOfFinishing = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[2]/div[7]/p[2]").text();
        String market = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[2]/div[9]/p[2]").text();
        String formOfOwnership = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[2]/div[11]/p[2]").text();
        String availableFrom = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[2]/div[13]/p[2]").text();
        String advertiserType = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[2]/div[15]/p[2]").text();
        String additionalInfo = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[2]/div[17]/p[2]").text();

        Element picture = doc.selectFirst("picture");
        String imageUrl = null;
        if (picture != null) {
            Element source = picture.selectFirst("source[media=\"(min-width: 768px)\"]");
            imageUrl = (source != null) ? source.attr("srcset") : picture.selectFirst("img").attr("src");
        }

        String yearOfConstruction = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[3]/div[1]/div/div/div[1]/p[2]").text();
        String elevator = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[3]/div[1]/div/div/div[3]/p[2]").text();
        String buildingType = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[3]/div[1]/div/div/div[5]/p[2]").text();
        String buildingMaterial = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[3]/div/div/div/div[7]/p[2]").text();
        String windows = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[3]/div[1]/div/div/div[7]/p[2]").text();
        String safety = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[3]/div[1]/div/div/div[9]/p[2]").text();

        String equipment = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[3]/div[2]/div/div/div[1]/p[2]").text();
        String security = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[3]/div[2]/div/div/div[3]/p[2]").text();
        String media = doc.selectXpath("//*[@id=\"__next\"]/div[1]/main/div[1]/div[1]/div[2]/div[3]/div[2]/div/div/div[5]/p[2]").text();

        return new FlatRecord(
                link,
                imageUrl,
                price,
                pricePerMeter,
                address,
                area,
                rooms,
                heating,
                floor,
                rent,
                stateOfFinishing,
                market,
                formOfOwnership,
                availableFrom,
                advertiserType,
                additionalInfo,
                yearOfConstruction,
                elevator,
                buildingType,
                safety,
                buildingMaterial,
                windows,
                equipment,
                security,
                media
        );
    }
}
