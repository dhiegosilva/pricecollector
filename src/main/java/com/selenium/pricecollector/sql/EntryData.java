package com.selenium.pricecollector.sql;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
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
}
