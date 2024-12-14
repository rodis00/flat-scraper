package com.example.flatscraper.otodomScraper;

import com.example.flatscraper.flat.Flat;
import com.example.flatscraper.flat.FlatService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class OtodomScraper {

    private final String baseUrl = "https://www.otodom.pl/pl/wyniki/sprzedaz/mieszkanie/warminsko--mazurskie/olsztyn/olsztyn/olsztyn?viewType=listing";
    private final String prefix = "https://www.otodom.pl";
    private final Logger logger = LoggerFactory.getLogger(OtodomScraper.class);
    private final FlatService flatService;

    public OtodomScraper(FlatService flatService) {
        this.flatService = flatService;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 60 * 5) // every 5 hours
    private void run() throws IOException, InterruptedException {
        logger.info("Scraping started");
        List<String> flatsLinks = getAllFlatsLinks();
        List<Flat> flats = new ArrayList<>();
        List<Flat> storedFlatsInDB = flatService.findAll();
        for (String flat : flatsLinks) {
            if (storedFlatsInDB.stream().anyMatch(f -> f.getUrl().equals(flat))) {
                logger.info("Flat already exists: {}", flat);
                continue;
            }
            Thread.sleep(600);
            Document flatDoc = Jsoup.connect(flat).get();
            logger.info("Scraping flat: {}", flat);
            Flat newFlat = createFlat(flat, flatDoc);
            flats.add(newFlat);
        }
        saveFlats(flats);
        logger.info("Scraping finished");
    }

    private void saveFlats(List<Flat> flats) {
        flatService.saveAll(flats);
    }

    private List<String> getAllFlatsLinks() throws IOException {
        Document doc = Jsoup.connect(baseUrl).get();
        int pages = getPagesNumber(doc);
        List<String> flatLinks = new ArrayList<>();
        for (int i = 1; i <= pages; i++) {
            String pageUrl = baseUrl + "&page=" + i;
            logger.info("Scraping page: {}", i);
            flatLinks.addAll(getFlatLinksFromPage(pageUrl));
        }
        return flatLinks;
    }

    private Flat createFlat(String url, Document flatDoc) {
        Flat flat = new Flat();
        flat.setUrl(url);
        flat.setPrice(getPrice(flatDoc));
        flat.setPricePerMeter(getPricePerMeter(flatDoc));
        flat.setAddress(getAddress(flatDoc));
        flat.setArea(getArea(flatDoc));
        flat.setRooms(getRooms(flatDoc));
        flat.setHeating(getHeating(flatDoc));
        flat.setFloor(getFloor(flatDoc));
        flat.setRent(getRent(flatDoc));
        flat.setStateOfFinishing(getStateOfFinishing(flatDoc));
        flat.setMarket(getMarket(flatDoc));
        flat.setFormOfOwnership(getFormOfOwnership(flatDoc));
        flat.setAvailableFrom(getAvailableFrom(flatDoc));
        flat.setAdvertiserType(getAdvertiserType(flatDoc));
        flat.setAdditionalInfo(getAdditionalInfo(flatDoc));
        flat.setYearOfConstruction(getYearOfConstruction(flatDoc));
        flat.setElevator(getElevator(flatDoc));
        flat.setBuildingType(getBuildingType(flatDoc));
        flat.setSafety(getSafety(flatDoc));
        flat.setBuildingMaterial(getBuildingMaterial(flatDoc));
        flat.setWindows(getWindows(flatDoc));
        flat.setEnergyCertificate(getEnergyCertificate(flatDoc));
        flat.setEquipment(getEquipment(flatDoc));
        flat.setSecurity(getSecurity(flatDoc));
        flat.setDescription(getDescription(flatDoc));
        return flat;
    }

    private int getPagesNumber(Document doc) {
        Element pages = doc.getElementsByClass("css-43nhzf").last();
        if (pages == null) {
            logger.warn("Cannot find pages number");
            return 1;
        }
        String pagesText = pages.text();
        return Integer.parseInt(pagesText);
    }

    private List<String> getFlatLinksFromPage(String flatUrl) throws IOException {
        Document doc = Jsoup.connect(flatUrl).get();
        List<String> flatsLinks = new ArrayList<>();
        Elements offers = doc.select("a[data-cy=listing-item-link]");
        if (offers.isEmpty()) {
            logger.warn("Cannot find offers");
            return flatsLinks;
        }
        for (Element offer : offers) {
            String link = offer.attr("href");
            flatsLinks.add(prefix + link);
        }
        return flatsLinks;
    }

    private Double getPrice(Document doc) {
        Element price = doc.select("strong[aria-label=Cena]").first();
        if (price != null) {
            String priceText = price.text();
            try {
                priceText = priceText.substring(0, priceText.length() - 3).replace(" ", "");
                return Double.parseDouble(priceText);
            } catch (NumberFormatException e) {
                logger.warn("Cannot parse price: {}", priceText);
                return null;
            }
        }
        return null;
    }

    private Double getPricePerMeter(Document doc) {
        Element pricePerMeter = doc.select("div[aria-label=Cena za metr kwadratowy]").first();
        if (pricePerMeter != null) {
            String pricePerMeterText = pricePerMeter.text()
                    .substring(0, pricePerMeter.text().length() - 6)
                    .replace(" ", "");
            try {
                return Double.parseDouble(pricePerMeterText);
            } catch (NumberFormatException e) {
                logger.warn("Cannot parse price per meter: {}", pricePerMeterText);
                return null;
            }
        }
        return null;
    }

    private String getAddress(Document doc) {
        Element address = doc.select("a[href=#map]").first();
        if (address == null) {
            logger.warn("Cannot find address");
            return null;
        }
        return address.text();
    }

    private Double getArea(Document doc) {
        Element areaAndRooms = doc.select("div[class=css-58w8b7 eezlw8k0]").first();
        if (areaAndRooms == null) {
            logger.warn("Cannot find area");
            return null;
        }
        Elements buttons = areaAndRooms.select("button");
        Element area = buttons.getFirst();
        String areaText = area.text()
                .substring(0, area.text().length() - 2);
        return Double.parseDouble(areaText);
    }

    private Integer getRooms(Document doc) {
        Element areaAndRooms = doc.select("div[class=css-58w8b7 eezlw8k0]").first();
        if (areaAndRooms == null) {
            logger.warn("Cannot find rooms");
            return null;
        }
        Elements buttons = areaAndRooms.select("button");
        Element rooms = buttons.get(1);
        String roomsText = rooms.text().split("")[0];
        return Integer.parseInt(roomsText);
    }

    private Element getFacility(Document doc, String name) {
        Elements facilities = doc.select("div[class=css-t7cajz e15n0fyo1]");
        for (Element facility : facilities) {
            String element = facility.select("p").first().text();
            if (element.contains(name)) {
                return facility.select("p").last();
            }
        }
        return null;
    }

    private String getHeating(Document doc) {
        Element heating = getFacility(doc, Facility.HEATING.getName());
        if (heating == null) {
            logger.warn("Cannot find heating");
            return null;
        }
        return heating.text();
    }

    private Integer getFloor(Document doc) {
        Element floor = getFacility(doc, Facility.FLOOR.getName());
        if (floor == null) {
            logger.warn("Cannot find floor");
            return null;
        }
        try {
            String floorText = floor.text().split("/")[0];
            floorText = floorText.contains("parter") ? "0" : floorText;
            return Integer.parseInt(floorText);
        } catch (NumberFormatException e) {
            logger.warn("Cannot parse floor: {}", floor);
            return null;
        }
    }

    private Double getRent(Document doc) {
        Element rent = getFacility(doc, Facility.RENT.getName());
        if (rent == null || rent.text().contains("0")) {
            logger.warn("Cannot find rent");
            return null;
        }
        try {
            String rentText = rent.text().substring(0, rent.text().length() - 3)
                    .replace(" ", "")
                    .replace(",", ".");
            return Double.parseDouble(rentText);
        } catch (NumberFormatException e) {
            logger.warn("Cannot parse rent: {}", rent);
            return null;
        }
    }

    private String getStateOfFinishing(Document doc) {
        Element stateOfFinishing = getFacility(doc, Facility.STATE_OF_FINISHING.getName());
        if (stateOfFinishing == null) {
            logger.warn("Cannot find state of finishing");
            return null;
        }
        return stateOfFinishing.text();
    }

    private String getMarket(Document doc) {
        Element market = getFacility(doc, Facility.MARKET.getName());
        if (market == null) {
            logger.warn("Cannot find market");
            return null;
        }
        return market.text();
    }

    private String getFormOfOwnership(Document doc) {
        Element formOfOwnership = getFacility(doc, Facility.FORM_OF_OWNERSHIP.getName());
        if (formOfOwnership == null) {
            logger.warn("Cannot find form of ownership");
            return null;
        }
        return formOfOwnership.text();
    }

    private LocalDate getAvailableFrom(Document doc) {
        Element availableFrom = getFacility(doc, Facility.AVAILABLE_FROM.getName());
        if (availableFrom == null) {
            logger.warn("Cannot find available from");
            return null;
        }
        try {
            return LocalDate.parse(availableFrom.text());
        } catch (DateTimeParseException e) {
            logger.warn("Cannot parse available from: {}", availableFrom);
            return null;
        }
    }

    private String getAdvertiserType(Document doc) {
        Element advertiserType = getFacility(doc, Facility.ADVERTISER_TYPE.getName());
        if (advertiserType == null) {
            logger.warn("Cannot find advertiser type");
            return null;
        }
        return advertiserType.text();
    }

    private List<String> getAdditionalInfo(Document doc) {
        Element additionalInfo = getFacility(doc, Facility.ADDITIONAL_INFO.getName());
        List<String> additionalInfoList = new ArrayList<>();
        if (additionalInfo == null || additionalInfo.text().contains("brak informacji")) {
            logger.warn("Cannot find additional info");
            return null;
        }
        if (!additionalInfo.select("span").isEmpty()) {
            for (Element span : additionalInfo.select("span")) {
                additionalInfoList.addAll(Arrays.stream(span.text().split("/")).toList());
            }
        } else {
            additionalInfoList.addAll(Arrays.stream(additionalInfo.text().split("/")).toList());
        }
        return additionalInfoList;
    }

    private Integer getYearOfConstruction(Document doc) {
        Element yearOfConstruction = getFacility(doc, Facility.YEAR_OF_CONSTRUCTION.getName());
        if (yearOfConstruction == null) {
            logger.warn("Cannot find year of construction");
            return null;
        }
        try {
            return Integer.parseInt(yearOfConstruction.text());
        } catch (NumberFormatException e) {
            logger.warn("Cannot parse year of construction: {}", yearOfConstruction);
            return null;
        }
    }

    private String getElevator(Document doc) {
        Element elevator = getFacility(doc, Facility.ELEVATOR.getName());
        if (elevator == null) {
            logger.warn("Cannot find elevator");
            return null;
        }
        return elevator.text();
    }

    private String getBuildingType(Document doc) {
        Element buildingType = getFacility(doc, Facility.BUILDING_TYPE.getName());
        if (buildingType == null) {
            logger.warn("Cannot find building type");
            return null;
        }
        return buildingType.text();
    }

    private List<String> getSafety(Document doc) {
        Element safety = getFacility(doc, Facility.SAFETY.getName());
        List<String> safetyList = new ArrayList<>();
        if (safety == null || safety.text().contains("brak informacji")) {
            logger.warn("Cannot find safety");
            return safetyList;
        }
        if (!safety.select("span").isEmpty()) {
            for (Element span : safety.select("span")) {
                safetyList.addAll(Arrays.stream(span.text().split(" / ")).toList());
            }
        } else {
            safetyList.addAll(Arrays.stream(safety.text().split(" / ")).toList());
        }
        return safetyList;
    }

    private String getBuildingMaterial(Document doc) {
        Element buildingMaterial = getFacility(doc, Facility.BUILDING_MATERIAL.getName());
        if (buildingMaterial == null) {
            logger.warn("Cannot find building material");
            return null;
        }
        return buildingMaterial.text();
    }

    private String getWindows(Document doc) {
        Element windows = getFacility(doc, Facility.WINDOWS.getName());
        if (windows == null) {
            logger.warn("Cannot find windows");
            return null;
        }
        return windows.text();
    }

    private String getEnergyCertificate(Document doc) {
        Element energyCertificate = getFacility(doc, Facility.ENERGY_CERTIFICATE.getName());
        if (energyCertificate == null) {
            logger.warn("Cannot find energy certificate");
            return null;
        }
        return energyCertificate.text();
    }

    private List<String> getEquipment(Document doc) {
        Element equipment = getFacility(doc, Facility.EQUIPMENT.getName());
        List<String> equipmentList = new ArrayList<>();
        if (equipment == null) {
            logger.warn("Cannot find equipment");
            return equipmentList;
        }
        Elements equipmentElements = equipment.select("span");
        if (!equipmentElements.isEmpty()) {
            for (Element equipmentElement : equipmentElements) {
                String txt = equipmentElement.text();
                equipmentList.addAll(Arrays.stream(txt.split(" / ")).toList());
            }
        } else {
            equipmentList.addAll(Arrays.stream(equipment.text().split("/")).toList());
        }
        return equipmentList;
    }

    private List<String> getSecurity(Document doc) {
        Element security = getFacility(doc, Facility.SECURITY.getName());
        List<String> securityList = new ArrayList<>();
        if (security == null) {
            logger.warn("Cannot find security");
            return securityList;
        }
        Elements securityElements = security.select("span");
        if (!securityElements.isEmpty()) {
            for (Element securityElement : securityElements) {
                String txt = securityElement.text();
                securityList.addAll(Arrays.stream(txt.split(" / ")).toList());
            }
        } else {
            securityList.addAll(Arrays.stream(security.text().split("/")).toList());
        }
        return securityList;
    }

    private String getDescription(Document doc) {
        Element description = doc.select("div[data-cy=adPageAdDescription]").first();
        if (description == null) {
            logger.warn("Cannot find description");
            return null;
        }
        return description.text();
    }
}
