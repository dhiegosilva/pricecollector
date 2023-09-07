package com.selenium.pricecollector.helper.ticker;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
@Component
public class XMLimport {
    public static Double goldTickerValue, silverTickerValue, platinumTickerValue, palladiumTickerValue;
    public void xmlReader() throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        // parse XML file
        DocumentBuilder db = dbf.newDocumentBuilder();

        URL url = new URL("http://mm.market-maker.de/posgmbh/precious-metals.xml");
        URLConnection uc = url.openConnection();
        String userpass = "posgmbh" + ":" + "PpreciOS";
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
        uc.setRequestProperty("Authorization", basicAuth);
        InputStream in = uc.getInputStream();

        Document doc = db.parse(in);
        doc.getDocumentElement().normalize();

        // get <staff>
        NodeList list = doc.getElementsByTagName("quote");

        for (int temp = 0; temp < list.getLength(); temp++) {

            Node node = list.item(temp);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) node;
                String metal = element.getElementsByTagName("symbol").item(0).getTextContent();

                switch (metal) {
                    case "XAUEUR.FXVWD":
                        goldTickerValue = Double.valueOf(element.getElementsByTagName("last").item(0).getTextContent());
                        break;
                    case "XAGEUR.FXVWD":
                        silverTickerValue = Double.valueOf(element.getElementsByTagName("last").item(0).getTextContent());
                        break;
                    case "XPTEUR.FXVWD":
                        platinumTickerValue = Double.valueOf(element.getElementsByTagName("last").item(0).getTextContent());
                        break;
                    case "XPDEUR.FXVWD":
                        palladiumTickerValue = Double.valueOf(element.getElementsByTagName("last").item(0).getTextContent());
                        break;
                }
            }
        }
    }
}
