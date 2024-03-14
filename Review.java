import java.util.Scanner;
import java.io.File;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class that contains helper methods for the Review Lab
 **/
public class Review {
  
  private static HashMap<String, Double> sentiment = new HashMap<String, Double>();
  private static ArrayList<String> posAdjectives = new ArrayList<String>();
  private static ArrayList<String> negAdjectives = new ArrayList<String>();
  
  static{
    try {
      Scanner input = new Scanner(new File("cleanSentiment.csv"));
      while(input.hasNextLine()){
        String[] temp = input.nextLine().split(",");
        sentiment.put(temp[0],Double.parseDouble(temp[1]));
        // System.out.println("added "+ temp[0]+", "+temp[1]);
      }
      input.close();
    }
    catch(Exception e){
      System.out.println("Error reading or parsing cleanSentiment.csv");
    }
  
  
  //read in the positive adjectives in postiveAdjectives.txt
  //    try {
  //     Scanner input = new Scanner(new File("ConsumerLab_Code/positiveAdjectives.txt"));
  //     while(input.hasNextLine()){
  //       posAdjectives.add(input.nextLine().trim());
  //     }
  //     input.close();
  //   }
  //   catch(Exception e){
  //     System.out.println("Error reading or parsing postitiveAdjectives.txt\n" + e);
  //   }   
 
  // //read in the negative adjectives in negativeAdjectives.txt
  //    try {
  //     Scanner input = new Scanner(new File("ConsumerLab_Code/negativeAdjectives.txt"));
  //     while(input.hasNextLine()){
  //       negAdjectives.add(input.nextLine().trim());
  //     }
  //     input.close();
  //   }
  //   catch(Exception e){
  //     System.out.println("Error reading or parsing negativeAdjectives.txt");
  //   }   
  }
  
  /** 
   * returns a string containing all of the text in fileName (including punctuation), 
   * with words separated by a single space 
   */
  public static String textToString( String fileName )
  {  
    String temp = "";
    try {
      Scanner input = new Scanner(new File(fileName));
      
      //add 'words' in the file to the string, separated by a single space
      while(input.hasNext()){
        temp = temp + input.next() + " ";
      }
      input.close();
      
    }
    catch(Exception e){
      System.out.println("Unable to locate " + fileName);
    }
    // remove any additional space that may have been added at the end of the string
    return temp.trim();
  }
  
  /**
   * @returns the sentiment value of word as a number between -1 (very negative) to 1 (very positive sentiment) 
   */
  public static double sentimentVal( String word )
  {
    try
    {
      return sentiment.get(word.toLowerCase());
    }
    catch(Exception e)
    {
      return 0;
    }
  }
  
  /**
   * Returns the ending punctuation of a string, or the empty string if there is none 
   */
  public static String getPunctuation( String word )
  { 
    String punc = "";
    for(int i=word.length()-1; i >= 0; i--){
      if(!Character.isLetterOrDigit(word.charAt(i))){
        punc = punc + word.charAt(i);
      } else {
        return punc;
      }
    }
    return punc;
  }

      /**
   * Returns the word after removing any beginning or ending punctuation
   */
  public static String removePunctuation( String word )
  {
    while(word.length() > 0 && !Character.isAlphabetic(word.charAt(0)))
    {
      word = word.substring(1);
    }
    while(word.length() > 0 && !Character.isAlphabetic(word.charAt(word.length()-1)))
    {
      word = word.substring(0, word.length()-1);
    }
    
    return word;
  }
 
  /** 
   * Randomly picks a positive adjective from the positiveAdjectives.txt file and returns it.
   */
  public static String randomPositiveAdj()
  {
    int index = (int)(Math.random() * posAdjectives.size());
    return posAdjectives.get(index);
  }
  
  /** 
   * Randomly picks a negative adjective from the negativeAdjectives.txt file and returns it.
   */
  public static String randomNegativeAdj()
  {
    int index = (int)(Math.random() * negAdjectives.size());
    return negAdjectives.get(index);
    
  }


  /** 
   * Randomly picks a positive or negative adjective and returns it.
   */
  public static String randomAdjective()
  {
    boolean positive = Math.random() < .5;
    if(positive){
      return randomPositiveAdj();
    } else {
      return randomNegativeAdj();
    }
  }

  public static double totalSentiment(String fileName) {
    String review = textToString(fileName);
    String[] words = review.split(" ");
    ArrayList<String> sentimentWords = new ArrayList<String>(Arrays.asList(words));
    ArrayList<Double> sentimentalValues = new ArrayList<Double>();
    double total = 0;

    for (String word : sentimentWords) {
      if (Review.sentimentVal(word) != 0) {
        sentimentalValues.add(Review.sentimentVal(word));
      }
    }
    
    for (int i = 0; i < sentimentalValues.size(); i++) {
      total += sentimentalValues.get(i);
    }
    return total/(sentimentalValues.size()+1);
  }

  public static int starRating(String review) {
    if (totalSentiment(review) > 0.4) {
      return 5;
    } else if (totalSentiment(review) > 0.2875) {
      return 4;
    } else if (totalSentiment(review) > 0.175) {
      return 3;
    } else if (totalSentiment(review) > 0.0625) {
      return 2;
    } else {
      return 1;
    }
  }

  public static String fakeReview(String fileName) {
    String temp;
    int increment = 0;
    String output = "";
    String review = textToString(fileName);
    String[] words = review.split(" ");
    ArrayList<String> splitPhrase = new ArrayList<String>(Arrays.asList(words));
    ArrayList<String> adjectives = new ArrayList<String>();

    for (int i = 0; i < splitPhrase.size(); i++) {
      if (splitPhrase.get(i).indexOf("*") != -1) {
        temp = splitPhrase.get(i).replace("*", "");
        adjectives.add(temp);
      }
    }

    for (int i = 0; i < adjectives.size(); i++) {
      if (Review.sentimentVal(adjectives.get(i)) > 0) {
        String positiveadj = Review.randomPositiveAdj();
        positiveadj = positiveadj.substring(0, positiveadj.indexOf(","));
        int index = 0;
        while (Review.sentimentVal(positiveadj) < Review.sentimentVal(adjectives.get(i))) {
          positiveadj = posAdjectives.get(index);
          positiveadj = positiveadj.substring(0, positiveadj.indexOf(","));
          index++;
        }
        adjectives.set(i, positiveadj);
      } else if (Review.sentimentVal(adjectives.get(i)) < 0) {
        String negativeadj = Review.randomNegativeAdj();
        negativeadj = negativeadj.substring(0, negativeadj.indexOf(","));
        int index = 0;
        while (Review.sentimentVal(negativeadj) > Review.sentimentVal(adjectives.get(i))) {
          negativeadj = negAdjectives.get(index);
          negativeadj = negativeadj.substring(0, negativeadj.indexOf(","));
          index++;
        }
        adjectives.set(i, negativeadj);
      }
    }

    
    for (int i = 0; i < splitPhrase.size(); i++) {
      if (splitPhrase.get(i).indexOf("*") != -1) {
        splitPhrase.set(i, adjectives.get(increment));
        increment++;
      }
      output += (splitPhrase.get(i) + " ");
    }
    return output;
  }

  public static double totalSentimentWithoutRepeatWords(String fileName) {
    String review = textToString(fileName);
    String[] words = review.split(" ");
    ArrayList<String> sentimentWords = new ArrayList<String>(Arrays.asList(words));
    ArrayList<Double> sentimentalValues = new ArrayList<Double>();
    ArrayList<Integer> removeIndex = new ArrayList<Integer>();
    double total = 0;
    
    for (int i = 0; i < sentimentWords.size(); i++) {
      sentimentWords.set(i, (sentimentWords.get(i)).replace(",", ""));
      sentimentWords.set(i, (sentimentWords.get(i)).replace(".", ""));
    }  

    for (int i = 0; i < sentimentWords.size(); i++) {
      for (int j = 0; j < sentimentWords.size(); j++) {
        if (i != j) {
          if (sentimentWords.get(i).equals(sentimentWords.get(j))) {
            removeIndex.add(i);
            removeIndex.add(j);
          }
        }
      }
    }

    for (int i = 0; i < (removeIndex.size()/2)+1; i++) {
      removeIndex.remove(i);
    }

    for (int i = 0; i < removeIndex.size()/2; i++) {
      int temp = removeIndex.get(i);
      sentimentWords.remove(temp);
    }

    for (String word : sentimentWords) {
      if (Review.sentimentVal(word) != 0) {
        sentimentalValues.add(Review.sentimentVal(word));
      }
    }
    
    for (int i = 0; i < sentimentalValues.size(); i++) {
      total += sentimentalValues.get(i);
    }
    return (total/sentimentalValues.size());
  }
}