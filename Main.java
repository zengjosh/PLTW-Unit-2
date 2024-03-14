import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.util.Arrays;
import java.lang.Math;

import com.opencsv.CSVWriter;

import java.io.FileReader;

import core.data.*;

public class Main {

  public static void main(String[] args) throws IOException, URISyntaxException {
    String[] htmlFiles = {"AmazonReviews/Review1.html", "AmazonReviews/Review2.html", "AmazonReviews/Review3.html", "AmazonReviews/Review4.html", "AmazonReviews/Review5.html", "AmazonReviews/Review6.html", "AmazonReviews/Review7.html", "AmazonReviews/Review8.html", "AmazonReviews/Review9.html", "AmazonReviews/Review10.html"};

    FileWriter reviewoutputfile = new FileWriter("reviews.csv");
    CSVWriter reviewwriter = new CSVWriter(reviewoutputfile);
    String[] header = {"Name", "Title", "Review", "Stars", "Date", "Color", "Verified Purchase", "Sentiment Score"};
    reviewwriter.writeNext(header);
    reviewwriter.close();  

    for (String htmlFile : htmlFiles) {
        htmltoCSV(htmlFile);
    }

    DataSource ds = DataSource.connect("reviews.csv").load();

    ArrayList<String> names = ds.fetchStringList("Name");
    ArrayList<String> colors = ds.fetchStringList("Color");
    ArrayList<String> sentimentScoresS = ds.fetchStringList("Sentiment Score");
    ArrayList<Double> sentimentScoresD = new ArrayList<Double>();

    for (String score : sentimentScoresS) {
        try {
            sentimentScoresD.add(Double.valueOf(score));
        } catch (NumberFormatException e) {
            sentimentScoresD.add(0.0); // or handle the error appropriately
        }
    }

    FileWriter top10outputfile = new FileWriter("top10reviews.csv");
    CSVWriter top10writer = new CSVWriter(top10outputfile);
    String[] top10header = {"Name", "Color", "Sentiment Score", "Advertisement"};
    top10writer.writeNext(top10header);

    for (int i = 0; i < 10 && !sentimentScoresD.isEmpty(); i++) {
        double greatest = Double.MIN_VALUE;
        int greatestIndex = -1;
        for (int j = 0; j < sentimentScoresD.size(); j++) {
            if (greatest < sentimentScoresD.get(j)) {
                greatest = sentimentScoresD.get(j);
                greatestIndex = j;
            }
        }

        if (greatestIndex != -1) {
            String[] record = {
              names.get(greatestIndex),
              colors.get(greatestIndex),
              String.valueOf(sentimentScoresD.get(greatestIndex)), 
              ("Hi " + names.get(greatestIndex) + ", we hope you are happy with your product! Here's a " + Math.round(sentimentScoresD.get(greatestIndex)*2) + "% discount for the next time you decide to buy another " + colors.get(greatestIndex) + " Portable Charger.")
            };
            top10writer.writeNext(record);

            System.out.println("Hi " + names.get(greatestIndex) + ", we hope you are happy with your product! Here's a " + Math.round(sentimentScoresD.get(greatestIndex)*2) + "% discount for the next time you decide to buy another " + colors.get(greatestIndex) + " Portable Charger.");

            names.remove(greatestIndex);
            colors.remove(greatestIndex);
            sentimentScoresD.remove(greatestIndex);
        }
    }

    top10writer.close();

  }

  public static void htmltoCSV(String htmlFile) throws URISyntaxException, IOException {
    URL path = ClassLoader.getSystemResource(htmlFile);
    File input = new File(path.toURI());
    Document document = Jsoup.parse(input, "UTF-8");

    Elements name = document.select(".a-profile-name");
    Elements title = document.select("[data-hook=review-title]");
    Elements review = document.select(".review-text-content");
    Elements stars = document.select(".review-rating");
    Elements date = document.select("[data-hook=review-date]");
    Elements color = document.select("[data-hook=format-strip]");
    Elements verified = document.select("[data-hook=avp-badge]");

    name.remove(0);
    name.remove(0);
    title.remove(0);
    title.remove(0);
    stars.remove(0);
    stars.remove(0);

    deduplicate(name);
    deduplicate(title);

    FileWriter outputfileAppend = new FileWriter("reviews.csv", true); 
    CSVWriter writerAppend = new CSVWriter(outputfileAppend);

    for (int i = 0; i < name.size(); i++) {
      try (FileWriter reviewAnalysisWriter = new FileWriter("ReviewAnalysis.txt");
         FileWriter titleAnalysisWriter = new FileWriter("TitleAnalysis.txt")) {

        reviewAnalysisWriter.write(review.get(i).text() + System.lineSeparator());
        titleAnalysisWriter.write(title.get(i).text().substring(19) + System.lineSeparator());
        } catch (IOException e) {
        e.printStackTrace();
        }
        
      String[] record = {
        name.get(i).text(),
        title.get(i).text().substring(19),
        review.get(i).text(),
        stars.get(i).text().substring(0, 3),
        date.get(i).text().substring(33),
        color.get(i).text().substring(7),
        verified.size() > i ? verified.get(i).text() : "Not Available", 
Double.toString(sentimentalValCalc(Double.valueOf(stars.get(i).text().substring(0, 3)), "TitleAnalysis.txt", "ReviewAnalysis.txt"))
      };
      writerAppend.writeNext(record);
      }

      writerAppend.close();
  }

  private static void deduplicate(Elements elements) {
    for (int i = 0; i < elements.size(); i++) {
      for (int j = i + 1; j < elements.size(); j++) {
        if (elements.get(i).text().equals(elements.get(j).text())) {
          elements.remove(j);
          j--;
        }
      }
    }
  }

  private static double sentimentalValCalc (double stars, String titlefile, String reviewfile) { 
    //Score = w1 * stars + w2 * titleSentimentValue + w3 * reviewSentimentValue
    double score = 0;
    double w1 = 2;
    double w2 = 1;
    double w3 = 0.5;

    score += w1*stars;
    score += w2*Review.totalSentiment(titlefile);
    score += w3*Review.totalSentiment(reviewfile);

    return(score);
  }
}
