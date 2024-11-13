package com.selenium.pricecollector.helper;

import com.selenium.pricecollector.helper.ticker.XMLimport;
import com.selenium.pricecollector.sql.EntryData;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component
public class GlobalFunctions {


    public EntryData getEntryData(String companyName, String articleName, String articleNr, String category, Double articleBuyPrice, Double articleSellPrice, String articleWeight) {
        EntryData entryData = new EntryData();

        entryData.setCompany(companyName);
        entryData.setArticleName(articleName);
        entryData.setArticleNr(articleNr);
        entryData.setCategory(category);
        entryData.setBuyValue(articleBuyPrice);
        entryData.setSellValue(articleSellPrice);
        entryData.setWeight(articleWeight);

        if (category.toLowerCase().contains("gold")) {
            entryData.setTickerValue(XMLimport.goldTickerValue);
            if (articleBuyPrice != 00.00 && articleBuyPrice != null) {
                switch (articleWeight) {
                    case "1 oz" ->
                            entryData.setAufGeldEUR((100 / (XMLimport.goldTickerValue) * articleBuyPrice - 100) / 100);
                    case "100 g" ->
                            entryData.setAufGeldEUR((100 / (XMLimport.goldTickerValue / 31.1035 * 100) * articleBuyPrice - 100) / 100);
                    case "1 kg" ->
                            entryData.setAufGeldEUR((100 / (XMLimport.goldTickerValue / 31.1035 * 1000) * articleBuyPrice - 100) / 100);
                    default -> entryData.setAufGeldEUR(null);
                }
            }

            if (articleSellPrice != 00.00 && articleSellPrice != null) {
                switch (articleWeight) {
                    case "1 oz" ->
                            entryData.setAbSchlagEUR((100 / (XMLimport.goldTickerValue) * articleSellPrice - 100) / 100);
                    case "100 g" ->
                            entryData.setAbSchlagEUR((100 / (XMLimport.goldTickerValue / 31.1035 * 100) * articleSellPrice - 100) / 100);
                    case "1 kg" ->
                            entryData.setAbSchlagEUR((100 / (XMLimport.goldTickerValue / 31.1035 * 1000) * articleSellPrice - 100) / 100);
                    default -> entryData.setAbSchlagEUR(null);
                }
            }


        } else if (category.toLowerCase().contains("silber")) {
            entryData.setTickerValue(XMLimport.silverTickerValue);
            if (articleBuyPrice != 00.00 && articleSellPrice != null) {

                switch (articleWeight) {
                    case "1 oz" ->
                            entryData.setAufGeldEUR((100 / (XMLimport.silverTickerValue) * articleBuyPrice - 100) / 100);
                    case "1 kg" ->
                            entryData.setAufGeldEUR((100 / (XMLimport.silverTickerValue / 31.1035 * 1000) * articleBuyPrice - 100) / 100);
                    case "5 kg" ->
                            entryData.setAufGeldEUR((100 / (XMLimport.silverTickerValue / 31.1035 * 1000 * 5) * articleBuyPrice - 100) / 100);
                    case "15 kg" ->
                            entryData.setAufGeldEUR((100 / (XMLimport.silverTickerValue / 31.1035 * 1000 * 15) * articleBuyPrice - 100) / 100);
                    default -> entryData.setAufGeldEUR(null);
                }
            }

            if (articleSellPrice != 00.00 && articleSellPrice != null) {

                switch (articleWeight) {
                    case "1 oz" ->
                            entryData.setAbSchlagEUR((100 / (XMLimport.silverTickerValue) * articleSellPrice - 100) / 100);
                    case "1 kg" ->
                            entryData.setAbSchlagEUR((100 / (XMLimport.silverTickerValue / 31.1035 * 1000) * articleSellPrice - 100) / 100);
                    case "5 kg" ->
                            entryData.setAbSchlagEUR((100 / (XMLimport.silverTickerValue / 31.1035 * 1000 * 5) * articleSellPrice - 100) / 100);
                    case "15 kg" ->
                            entryData.setAbSchlagEUR((100 / (XMLimport.silverTickerValue / 31.1035 * 1000 * 15) * articleSellPrice - 100) / 100);
                    default -> entryData.setAbSchlagEUR(null);
                }
            }

        } else if (category.toLowerCase().contains("pall")) {

            entryData.setTickerValue(XMLimport.palladiumTickerValue);

        } else if (category.toLowerCase().contains("plat")) {

            entryData.setTickerValue(XMLimport.platinumTickerValue);

        }

        return entryData;
    }

    public List<EntryData> getEntryData(String companyName, List<String> articleName, List<String> articleNr, List<String> category, List<Double> articleBuyPrice, List<Double> articleSellPrice, List<String> articleWeight) {

        List<String> company = new LinkedList<>();
        List<Double> ticker = new LinkedList<>();
        List<Double> aufGeld = new LinkedList<>();
        List<Double> abSchlag = new LinkedList<>();
        List<EntryData> entryData = new LinkedList<>();

        for (int i = 0; i < articleName.size(); i++) {

            ticker.add(null);
            aufGeld.add(null);
            abSchlag.add(null);
            company.add(companyName);

            if (category.get(i).toLowerCase().contains("gold")) {
                if (articleBuyPrice.get(i) != 00.00 && articleBuyPrice.get(i) != null) {
                    switch (articleWeight.get(i)) {
                        case "1 oz":
                            aufGeld.set(i, (100 / (XMLimport.goldTickerValue) * articleBuyPrice.get(i) - 100) / 100);
                            break;
                        case "100 g":
                            aufGeld.set(i, (100 / (XMLimport.goldTickerValue / 31.1035 * 100) * articleBuyPrice.get(i) - 100) / 100);
                            break;
                        case "1 kg":
                            aufGeld.set(i, (100 / (XMLimport.goldTickerValue / 31.1035 * 1000) * articleBuyPrice.get(i) - 100) / 100);
                            break;
                        default:
                            aufGeld.set(i, null);
                            break;
                    }
                }

                if (articleSellPrice.get(i) != 00.00 && articleSellPrice.get(i) != null) {
                    switch (articleWeight.get(i)) {
                        case "1 oz" ->
                                abSchlag.set(i, (100 / (XMLimport.goldTickerValue) * articleSellPrice.get(i) - 100) / 100);
                        case "100 g" ->
                                abSchlag.set(i, (100 / (XMLimport.goldTickerValue / 31.1035 * 100) * articleSellPrice.get(i) - 100) / 100);
                        case "1 kg" ->
                                abSchlag.set(i, (100 / (XMLimport.goldTickerValue / 31.1035 * 1000) * articleSellPrice.get(i) - 100) / 100);
                        default -> abSchlag.set(i, null);
                    }
                }

                ticker.set(i, XMLimport.goldTickerValue);

            } else if (category.get(i).toLowerCase().contains("silber")) {
                if (articleBuyPrice.get(i) != 00.00 && articleSellPrice.get(i) != null) {

                    switch (articleWeight.get(i)) {
                        case "1 oz" ->
                                aufGeld.set(i, (100 / (XMLimport.silverTickerValue) * articleBuyPrice.get(i) - 100) / 100);
                        case "1 kg" ->
                                aufGeld.set(i, (100 / (XMLimport.silverTickerValue / 31.1035 * 1000) * articleBuyPrice.get(i) - 100) / 100);
                        case "5 kg" ->
                                aufGeld.set(i, (100 / (XMLimport.silverTickerValue / 31.1035 * 1000 * 5) * articleBuyPrice.get(i) - 100) / 100);
                        case "15 kg" ->
                                aufGeld.set(i, (100 / (XMLimport.silverTickerValue / 31.1035 * 1000 * 15) * articleBuyPrice.get(i) - 100) / 100);
                        default -> aufGeld.set(i, null);
                    }
                }

                if (articleSellPrice.get(i) != 00.00 && articleSellPrice.get(i) != null) {

                    switch (articleWeight.get(i)) {
                        case "1 oz" ->
                                abSchlag.set(i, (100 / (XMLimport.silverTickerValue) * articleSellPrice.get(i) - 100) / 100);
                        case "1 kg" ->
                                abSchlag.set(i, (100 / (XMLimport.silverTickerValue / 31.1035 * 1000) * articleSellPrice.get(i) - 100) / 100);
                        case "5 kg" ->
                                abSchlag.set(i, (100 / (XMLimport.silverTickerValue / 31.1035 * 1000 * 5) * articleSellPrice.get(i) - 100) / 100);
                        case "15 kg" ->
                                abSchlag.set(i, (100 / (XMLimport.silverTickerValue / 31.1035 * 1000 * 15) * articleSellPrice.get(i) - 100) / 100);
                        default -> abSchlag.set(i, null);
                    }
                }

                ticker.set(i, XMLimport.silverTickerValue);

            } else if (category.get(i).toLowerCase().contains("pall")) {

                ticker.set(i, XMLimport.palladiumTickerValue);

            } else if (category.get(i).toLowerCase().contains("plat")) {

                ticker.set(i, XMLimport.platinumTickerValue);

            }
            if (articleSellPrice.get(i).equals(00.00) || articleSellPrice.get(i) == null) {
                entryData.add(new EntryData(
                        company.get(i),
                        articleNr.get(i),
                        category.get(i),
                        articleName.get(i),
                        articleWeight.get(i),
                        articleBuyPrice.get(i),
                        ticker.get(i),
                        aufGeld.get(i)
                ));
            } else {
                entryData.add(new EntryData(
                        company.get(i),
                        articleNr.get(i),
                        category.get(i),
                        articleName.get(i),
                        articleWeight.get(i),
                        articleBuyPrice.get(i),
                        articleSellPrice.get(i),
                        ticker.get(i),
                        aufGeld.get(i),
                        abSchlag.get(i))
                );
            }
        }

        return entryData;
    }
}
