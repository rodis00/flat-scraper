package com.example.flatscraper.otodomScraper;

public enum Facility {
    FLOOR("Piętro"),
    HEATING("Ogrzewanie"),
    RENT("Czynsz"),
    STATE_OF_FINISHING("Stan wykończenia"),
    MARKET("Rynek"),
    FORM_OF_OWNERSHIP("Forma własności"),
    AVAILABLE_FROM("Dostępne od"),
    ADVERTISER_TYPE("Typ ogłoszeniodawcy"),
    ADDITIONAL_INFO("Informacje dodatkowe"),
    YEAR_OF_CONSTRUCTION("Rok budowy"),
    ELEVATOR("Winda"),
    BUILDING_TYPE("Rodzaj zabudowy"),
    SAFETY("Bezpieczeństwo"),
    BUILDING_MATERIAL("Materiał budynku"),
    WINDOWS("Okna"),
    ENERGY_CERTIFICATE("Certyfikat energetyczny"),
    EQUIPMENT("Wyposażenie"),
    SECURITY("Zabezpieczenia");

    private String name;

    Facility(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
