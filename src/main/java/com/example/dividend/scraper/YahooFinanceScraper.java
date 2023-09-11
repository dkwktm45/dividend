package com.example.dividend.scraper;

import com.example.dividend.exception.impl.YahooScraperException;
import com.example.dividend.model.Company;
import com.example.dividend.model.Dividend;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class YahooFinanceScraper implements Scraper{
  private static final String URL = "https://finance.yahoo.com/quote/%s/history?period1=%d&period2=%d&interval=1mo";
  private static final long START_TIME = 86400;
  private static final String SUMMARY_URL ="https://finance.yahoo.com/quote/%s?p=%s";


  @Override
  public ScrapedResult scrap(Company company) {
    var scrapReuslt = new ScrapedResult();
    scrapReuslt.setCompany(company);

    try {

      long end = System.currentTimeMillis() / 1000;
      String url = String.format(URL, company.getTicker(), START_TIME, end);
      Connection connection = Jsoup.connect(url);

      Document document = connection.get();
      Elements parsingDivs = document.getElementsByAttributeValue("data-test",
          "historical-prices");
      Element tableElse = parsingDivs.get(0);
      Element tbody = tableElse.children().get(1);

      List<Dividend> dividendList = new ArrayList<>();
      for (Element e : tbody.children()) {
        String txt = e.text();
        if (!txt.endsWith("Dividend")) {
          continue;
        }
        String[] splits = txt.split(" ");
        int mounth = Month.strToNumber(splits[0]);
        int day = Integer.valueOf(splits[1].replace(",", ""));
        int year = Integer.valueOf(splits[2]);
        String dividend = splits[3];
        if (mounth < 0) {
          throw new YahooScraperException();
        }
        dividendList.add(new Dividend(LocalDateTime.of(year, mounth, day, 0,
            0),dividend));
      }
      scrapReuslt.setDividends(dividendList);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    return scrapReuslt;
  }

  @Override
  public Company scarapCompanyByTicker(String ticker) {
    String url = String.format(SUMMARY_URL, ticker, ticker);
    try {
      Document document = Jsoup.connect(url).get();
      Element titleEle = document.getElementsByTag("h1").get(0);
      String title = titleEle.text().split(" - ")[1].trim();

      return new Company(ticker, title);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
