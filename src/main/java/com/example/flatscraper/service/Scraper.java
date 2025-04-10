package com.example.flatscraper.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Scraper {
    public static void main(String[] args) throws IOException {

//        String url = "https://www.otodom.pl/pl/oferta/nad-zatoka-apartament-gotowy-do-zamieszkania-ID4wjxb";
//        String url = "https://www.otodom.pl/pl/oferta/mieszkanie-inwestycyjne-ul-polna-podgrodzie-ID4wtOj";
//        String url = "https://www.otodom.pl/pl/oferta/wyjatkowe-mieszkanie-augustowska-3pokoje-2pietro-ID4wOFa";
        String url = "https://www.otodom.pl/pl/wyniki/sprzedaz/mieszkanie/warminsko--mazurskie/olsztyn/olsztyn/olsztyn?viewType=listing";

        Document doc = Jsoup.connect(url).get();

        Elements divs = doc.select("li");
        for (Element div : divs) {
            System.out.println(div.text());
        }
    }
}
