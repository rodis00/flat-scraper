package com.example.flatscraper.otodomScraper;

import com.example.flatscraper.flat.Flat;
import com.example.flatscraper.flat.FlatService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class OtodomScraper implements CommandLineRunner {

    private final String baseUrl = "https://www.otodom.pl/pl/wyniki/sprzedaz/mieszkanie/warminsko--mazurskie/olsztyn/olsztyn/olsztyn?viewType=listing";

    private final FlatService flatService;

    public OtodomScraper(FlatService flatService) {
        this.flatService = flatService;
    }

    @Override
    public void run(String... args) throws Exception {
        scrapeData(baseUrl);
//        createFlat("https://www.otodom.pl/pl/oferta/mieszkanie-na-sprzedaz-ID4trjg");
//        createFlat("https://www.otodom.pl/pl/oferta/mieszkanie-z-meblami-44m2-w-olsztynie-os-zatorze-ID4sq9L");
    }

    public void scrapeData(String baseUrl) throws IOException, InterruptedException {
        Document doc = Jsoup.connect(baseUrl).get();
        int pages = getPagesNumber(doc);

        List<String> flatLinks = new ArrayList<>();
        List<Flat> flats = new ArrayList<>();

        String suffix = "&page=";
        for (int i = 1; i <= 1; i++) {
            String pageUrl = baseUrl + suffix + i;
            System.out.println("Scraping page " + i);
            flatLinks.addAll(getFlatsLinksFromPage(pageUrl));
            for (String flatLink : flatLinks) {
                System.out.println("Scraping flat: " + flatLink);
                flats.add(createFlat(flatLink));
                Thread.sleep(600);
            }
            Thread.sleep(600);
        }

        flatService.saveAll(flats.stream()
                .filter(flat -> !flatService.checkIfFlatExists(flat.getUrl()))
                .toList()
        );
    }

    private int getPagesNumber(Document doc) {
        Element pages = doc.getElementsByClass("css-43nhzf").last();
        return Integer.parseInt(pages.text());
    }

    private List<String> getFlatsLinksFromPage(String pageUrl) throws IOException {
        Document page = Jsoup.connect(pageUrl).get();
        List<String> flatsLinks = new ArrayList<>();
        String prefix = "https://www.otodom.pl";

        Elements offers = page.select("a[data-cy=listing-item-link]");
        for (Element offer : offers) {
            String link = offer.attr("href");
            flatsLinks.add(prefix + link);
        }
        return flatsLinks;
    }

    private Flat createFlat(String flatUrl) throws IOException {
        Document doc = Jsoup.connect(flatUrl).get();

        Flat flat = new Flat();
        flat.setUrl(flatUrl);

        Element price = doc.select("strong[aria-label=Cena]").first();
        String priceText = price.text();
        Integer priceInt = null;
        if (!priceText.contains("Zapytaj o cenę")) {
            priceText = priceText.substring(0, priceText.length() - 3).replace(" ", "");
            priceInt = Integer.parseInt(priceText);
        }
        flat.setPrice(priceInt);

        Element pricePerMeter = doc.select("div[aria-label=Cena za metr kwadratowy]").first();
        Integer pricePerMeterInt = null;
        if (pricePerMeter != null) {
            String pricePerMeterText = pricePerMeter.text()
                    .substring(0, pricePerMeter.text().length() - 6)
                    .replace(" ", "");
            pricePerMeterInt = Integer.parseInt(pricePerMeterText);
        }
        flat.setPricePerMeter(pricePerMeterInt);

        Element address = doc.select("a[href=#map]").first();
        String addressText = address.text();
        flat.setAddress(addressText);

        Element areaAndRooms = doc.select("div[class=css-58w8b7 eezlw8k0]").first();
        Elements areaAndRoomsButtons = areaAndRooms.select("button");

        Element area = areaAndRoomsButtons.get(0);
        String areaText = area.text()
                .substring(0, area.text().length() - 2);
        Double areaDouble = Double.parseDouble(areaText);
        flat.setArea(areaDouble);

        Element rooms = areaAndRoomsButtons.get(1);
        String roomsText = rooms.text().split("")[0];
        Integer roomsInt = Integer.parseInt(roomsText);
        flat.setRooms(roomsInt);

        Elements facilities = doc.select("div[class=css-t7cajz e15n0fyo1]");

        for (Element facility : facilities) {
            String element = facility.select("p").first().text();

            if (element.contains("Ogrzewanie")) {
                Element heating = facility.select("p").last();
                String heatingText = heating.text();
                flat.setHeating(heatingText);
            }
            if (element.contains("Piętro")) {
                Element floor = facility.select("p").last();
                String floorText = floor.text().split("/")[0];
                floorText = floorText.contains("parter") ? "0" : floorText;
                Integer floorInt = Integer.parseInt(floorText);
                flat.setFloor(floorInt);
            }
            if (element.contains("Czynsz")) {
                Element rent = facility.select("p").last();
                String rentText = rent.text();
                Double rentPrice = null;
                if (!rentText.contains("brak informacji")) {
                    rentText = rentText.substring(0, rentText.length() - 3)
                            .replace(" ", "")
                            .replace(",", ".");
                    rentPrice = Double.parseDouble(rentText);
                }
                flat.setRent(rentPrice);
            }
            if (element.contains("Stan wykończenia")) {
                Element stateOfFinishing = facility.select("p").last();
                String stateOfFinishingText = stateOfFinishing.text();
                flat.setStateOfFinishing(stateOfFinishingText);
            }
            if (element.contains("Rynek")) {
                Element market = facility.select("p").last();
                String marketText = market.text();
                flat.setMarket(marketText);
            }
            if (element.contains("Forma własności")) {
                Element formOfOwnership = facility.select("p").last();
                String formOfOwnershipText = formOfOwnership.text();
                flat.setFormOfOwnership(formOfOwnershipText);
            }
            if (element.contains("Dostępne od")) {
                Element availableFrom = facility.select("p").last();
                String availableFromText = availableFrom.text();
                LocalDate availableFromDate = null;
                if (!availableFromText.contains("brak informacji")) {
                    availableFromDate = LocalDate.parse(availableFromText);
                }
                flat.setAvailableFrom(availableFromDate);
            }
            if (element.contains("Typ ogłoszeniodawcy")) {
                Element advertiserType = facility.select("p").last();
                String advertiserTypeText = advertiserType.text();
                flat.setAdvertiserType(advertiserTypeText);
            }
            if (element.contains("Informacje dodatkowe")) {
                Element additionalInfo = facility.select("p").last();
                String additionalInfoText = additionalInfo.text();
                List<String> additionalInfoList = new ArrayList<>();
                if (!additionalInfoText.contains("brak informacji")) {
                    if (!additionalInfo.select("span").isEmpty()) {
                        for (Element span : additionalInfo.select("span")) {
                            additionalInfoList.addAll(Arrays.stream(span.text().split("/")).toList());
                        }
                    } else {
                        additionalInfoList.addAll(Arrays.stream(additionalInfoText.split("/")).toList());
                    }
                }
                flat.setAdditionalInfo(additionalInfoList);
            }
            if (element.contains("Rok budowy")) {
                Element yearOfConstruction = facility.select("p").last();
                int yearOfConstructionText = Integer.parseInt(yearOfConstruction.text());
                flat.setYearOfConstruction(yearOfConstructionText);
            }
            if (element.contains("Winda")) {
                Element elevator = facility.select("p").last();
                String elevatorText = elevator.text();
                flat.setElevator(elevatorText);
            }
            if (element.contains("Rodzaj zabudowy")) {
                Element buildingType = facility.select("p").last();
                String buildingTypeText = buildingType.text();
                flat.setBuildingType(buildingTypeText);
            }
            if (element.contains("Bezpieczeństwo")) {
                Element safety = facility.select("p").last();
                String safetyText = safety.text();
                List<String> safetyList = new ArrayList<>();
                if (!safetyText.contains("brak informacji")) {
                    if (!safety.select("span").isEmpty()) {
                        for (Element span : safety.select("span")) {
                            safetyList.addAll(Arrays.stream(span.text().split(" / ")).toList());
                        }
                    } else {
                        safetyList.addAll(Arrays.stream(safetyText.split(" / ")).toList());
                    }
                }
                flat.setSafety(safetyList);
            }
            if (element.contains("Materiał budynku")) {
                Element buildingMaterial = facility.select("p").last();
                String buildingMaterialText = buildingMaterial.text();
                flat.setBuildingMaterial(buildingMaterialText);
            }
            if (element.contains("Okna")) {
                Element windows = facility.select("p").last();
                String windowsText = windows.text();
                flat.setWindows(windowsText);
            }
            if (element.contains("Certyfikat energetyczny")) {
                Element energyCertificate = facility.select("p").last();
                String energyCertificateText = energyCertificate.text();
                flat.setEnergyCertificate(energyCertificateText);
            }
            if (element.contains("Wyposażenie")) {
                Element equipment = facility.select("p").last();
                Elements equipmentElements = equipment.select("span");
                List<String> equipmentList = new ArrayList<>();
                if (!equipmentElements.isEmpty()) {
                    for (Element equipmentElement : equipmentElements) {
                        String txt = equipmentElement.text();
                        equipmentList.addAll(Arrays.stream(txt.split(" / ")).toList());
                    }
                } else {
                    equipmentList.addAll(Arrays.stream(equipment.text().split("/")).toList());
                }
                flat.setEquipment(equipmentList);
            }
            if (element.contains("Zabezpieczenia")) {
                Element security = facility.select("p").last();
                Elements securityElements = security.select("span");
                List<String> securityList = new ArrayList<>();
                if (!securityElements.isEmpty()) {
                    for (Element securityElement : securityElements) {
                        String txt = securityElement.text();
                        securityList.addAll(Arrays.stream(txt.split(" / ")).toList());
                    }
                } else {
                    securityList.addAll(Arrays.stream(security.text().split("/")).toList());
                }
                flat.setSecurity(securityList);
            }
        }

        Element description = doc.select("div[data-cy=adPageAdDescription]").first();
        String descriptionText = description.text();
        flat.setDescription(descriptionText);

        return flat;
    }
}
