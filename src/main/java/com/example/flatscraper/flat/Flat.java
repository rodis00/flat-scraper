package com.example.flatscraper.flat;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Flat {
    @Id
    @GeneratedValue
    private Integer id;

    private String url;

    private Integer price;

    private Integer pricePerMeter;

    private String address;

    private Double area;

    private Integer rooms;

    private String heating;

    private Integer floor;

    private Double rent;

    private String stateOfFinishing;

    private String market;

    private String formOfOwnership;

    private LocalDate availableFrom;

    private String advertiserType;

    @ElementCollection
    private List<String> additionalInfo = new ArrayList<>();

    private Integer yearOfConstruction;

    private String elevator;

    private String buildingType;

    @ElementCollection
    private List<String> safety = new ArrayList<>();

    private String buildingMaterial;

    private String windows;

    private String energyCertificate;

    @ElementCollection
    private List<String> equipment = new ArrayList<>();

    @ElementCollection
    private List<String> security = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String description;

    public Flat() {
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPricePerMeter() {
        return pricePerMeter;
    }

    public void setPricePerMeter(Integer pricePerMeter) {
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

    public LocalDate getAvailableFrom() {
        return availableFrom;
    }

    public void setAvailableFrom(LocalDate availableFrom) {
        this.availableFrom = availableFrom;
    }

    public String getAdvertiserType() {
        return advertiserType;
    }

    public void setAdvertiserType(String advertiserType) {
        this.advertiserType = advertiserType;
    }

    public List<String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(List<String> additionalInfo) {
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

    public List<String> getSafety() {
        return safety;
    }

    public void setSafety(List<String> safety) {
        this.safety = safety;
    }

    public String getBuildingMaterial() {
        return buildingMaterial;
    }

    public void setBuildingMaterial(String buildingMaterial) {
        this.buildingMaterial = buildingMaterial;
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

    public List<String> getEquipment() {
        return equipment;
    }

    public void setEquipment(List<String> equipment) {
        this.equipment = equipment;
    }

    public List<String> getSecurity() {
        return security;
    }

    public void setSecurity(List<String> security) {
        this.security = security;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Flat{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", price=" + price +
                ", pricePerMeter=" + pricePerMeter +
                ", address='" + address + '\'' +
                ", area=" + area +
                ", rooms=" + rooms +
                ", heating='" + heating + '\'' +
                ", floor=" + floor +
                ", rent=" + rent +
                ", stateOfFinishing='" + stateOfFinishing + '\'' +
                ", market='" + market + '\'' +
                ", formOfOwnership='" + formOfOwnership + '\'' +
                ", availableFrom=" + availableFrom +
                ", advertiserType='" + advertiserType + '\'' +
                ", additionalInfo=" + additionalInfo +
                ", yearOfConstruction=" + yearOfConstruction +
                ", elevator='" + elevator + '\'' +
                ", buildingType='" + buildingType + '\'' +
                ", safety=" + safety +
                ", buildingMaterial='" + buildingMaterial + '\'' +
                ", windows='" + windows + '\'' +
                ", energyCertificate='" + energyCertificate + '\'' +
                ", equipment=" + equipment +
                ", security=" + security +
                ", description='" + description + '\'' +
                '}';
    }
}
