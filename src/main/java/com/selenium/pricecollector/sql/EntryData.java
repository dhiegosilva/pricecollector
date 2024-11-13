package com.selenium.pricecollector.sql;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "selenium_marktanalyse_wettbewerb")
public class EntryData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="uuid" ,nullable = false)
    private UUID id;
    @Column
    private String company;
    @Column
    private String articleNr;
    @Column
    private String category;
    @Column
    private String articleName;
    @Column
    private String weight;
    @Column
    private Double buyValue;
    @Column
    private Double sellValue;
    @Column
    private Double tickerValue;
    @Column
    private Double aufGeldEUR;
    @Column
    private Double abSchlagEUR;
    @CreationTimestamp
    @Column
    private Timestamp dataCollectionDatetime;

    public EntryData() {}

    public EntryData(String company, String articleNr, String category, String articleName, String weight, Double buyValue, Double sellValue, Double tickerValue, Double aufGeldEUR, Double abSchlagEUR) {
        this.company = company;
        this.articleNr = articleNr;
        this.category = category;
        this.articleName = articleName;
        this.weight = weight;
        this.buyValue = buyValue;
        this.sellValue = sellValue;
        this.tickerValue = tickerValue;
        this.aufGeldEUR = aufGeldEUR;
        this.abSchlagEUR = abSchlagEUR;
    }

    public EntryData(String company, String articleNr, String category, String articleName, String weight, Double buyValue, Double tickerValue, Double aufGeldEUR) {
        this.company = company;
        this.articleNr = articleNr;
        this.category = category;
        this.articleName = articleName;
        this.weight = weight;
        this.buyValue = buyValue;
        this.tickerValue = tickerValue;
        this.aufGeldEUR = aufGeldEUR;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getArticleNr() {
        return articleNr;
    }

    public void setArticleNr(String articleNr) {
        this.articleNr = articleNr;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public Double getBuyValue() {
        return buyValue;
    }

    public void setBuyValue(Double buyValue) {
        this.buyValue = buyValue;
    }

    public Double getSellValue() {
        return sellValue;
    }

    public void setSellValue(Double sellValue) {
        this.sellValue = sellValue;
    }

    public Double getTickerValue() {
        return tickerValue;
    }

    public void setTickerValue(Double tickerValue) {
        this.tickerValue = tickerValue;
    }

    public Double getAufGeldEUR() {
        return aufGeldEUR;
    }

    public void setAufGeldEUR(Double aufGeldEUR) {
        this.aufGeldEUR = aufGeldEUR;
    }

    public Double getAbSchlagEUR() {
        return abSchlagEUR;
    }

    public void setAbSchlagEUR(Double abSchlagEUR) {
        this.abSchlagEUR = abSchlagEUR;
    }

    public Timestamp getDataCollectionDatetime() {
        return dataCollectionDatetime;
    }

    public void setDataCollectionDatetime(Timestamp dataCollectionDatetime) {
        this.dataCollectionDatetime = dataCollectionDatetime;
    }
}
