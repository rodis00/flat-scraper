package com.example.flatscraper.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "flats")
public class FlatEntity {
    @Id
    @GeneratedValue
    private Integer id;

    private String title;

    private String url;

    private String imageUrl;

    private Double price;

    private Double pricePerMeter;

    private String address;

    private Double area;

    private Integer rooms;

    private String heating;

    private Integer floor;

    private Double rent;

    private String stateOfFinishing;

    private String market;

    private String formOfOwnership;

    private String availableFrom;

    private String advertiserType;

    private String additionalInfo;

    private Integer yearOfConstruction;

    private String elevator;

    private String buildingType;

    private String buildingMaterial;

    private String security;

    private String media;

    private String windows;

    private String energyCertificate;

    private String safety;

    public FlatEntity() {
    }

    public String getWindows() {
        return windows;
    }

    public void setWindows(String windows) {
        this.windows = windows;
    }

    public String getEnergyCertificate() {
        return energyCertificate;
    }

    public void setEnergyCertificate(String energyCertificate) {
        this.energyCertificate = energyCertificate;
    }

    public String getSafety() {
        return safety;
    }

    public void setSafety(String safety) {
        this.safety = safety;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPricePerMeter() {
        return pricePerMeter;
    }

    public void setPricePerMeter(Double pricePerMeter) {
        this.pricePerMeter = pricePerMeter;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }

    public String getHeating() {
        return heating;
    }

    public void setHeating(String heating) {
        this.heating = heating;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Double getRent() {
        return rent;
    }

    public void setRent(Double rent) {
        this.rent = rent;
    }

    public String getStateOfFinishing() {
        return stateOfFinishing;
    }

    public void setStateOfFinishing(String stateOfFinishing) {
        this.stateOfFinishing = stateOfFinishing;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getFormOfOwnership() {
        return formOfOwnership;
    }

    public void setFormOfOwnership(String formOfOwnership) {
        this.formOfOwnership = formOfOwnership;
    }

    public String getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(String availableFrom) {
        this.availableFrom = availableFrom;
    }

    public String getAdvertiserType() {
        return advertiserType;
    }

    public void setAdvertiserType(String advertiserType) {
        this.advertiserType = advertiserType;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Integer getYearOfConstruction() {
        return yearOfConstruction;
    }

    public void setYearOfConstruction(Integer yearOfConstruction) {
        this.yearOfConstruction = yearOfConstruction;
    }

    public String getElevator() {
        return elevator;
    }

    public void setElevator(String elevator) {
        this.elevator = elevator;
    }

    public String getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(String buildingType) {
        this.buildingType = buildingType;
    }

    public String getBuildingMaterial() {
        return buildingMaterial;
    }

    public void setBuildingMaterial(String buildingMaterial) {
        this.buildingMaterial = buildingMaterial;
    }

    public String getSecurity() {
        return security;
    }

    public void setSecurity(String security) {
        this.security = security;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
