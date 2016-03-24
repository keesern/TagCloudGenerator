/*
 *  Created by Kee Sern on 3/24/16.
 *  Copyright © 2016 Kee Sern. All rights reserved.
*/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Creates a html file that generates a Tag Cloud containing given amount of
 * words
 *
 * @author Chua Kee Sern
 * @author Nie Chen Feng
 * @author Zou Yu Peng
 *
 */

public final class TagCloudGenerator {

    /**
     * Return -1 is less than or 1 if a value of a map greater than than the
     * second. If the two arguments are equal, compare the two keys.
     *
     */

    private static class ValueComparator implements Comparator<String> {

        Map<String, Integer> origin;

        ValueComparator(Map<String, Integer> origin) {
            this.origin = origin;
        }

        @Override
        public int compare(String first, String second) {
            int firstInt = this.origin.get(first);
            int secondInt = this.origin.get(second);
            if (secondInt > firstInt) {
                return 1;
            } else if (firstInt > secondInt) {
                return -1;
            } else {
                return second.compareTo(first);
            }
        }
    }

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloudGenerator() {
    }

    /**
     * String containing all valid separators.
     */
    private static final String SEPARATORSTRING = " \t\n\r =+-_)(*&^%$#@!/'\","
            + ".:;{}[]<>?|~`";

    /**
     * Generates beginning header HTML for Tag Cloud.
     *
     * @param inputLocation
     *            the location of the file to be read
     *
     * @param outputFileName
     *            the name of the outgoing html file
     *
     * @param outputHTML
     *            the file output stream
     *
     * @param numberOfWords
     *            number of words to be included in cloud
     *
     * @requires <pre>
     * {@code outputHTML.is_open}
     * </pre>
     *
     * @ensures<pre> {@code [html file is created containing appropriate code]}
     *               </pre>
     */
    private static void generateHeaderHTML(PrintWriter outputHTML,
            String inputLocation, String outputFileName, int numberOfWords) {

        outputHTML.println("<html>");
        outputHTML.println("\t<head>");
        outputHTML.print("\t\t<title>");
        outputHTML.print("\t\t\tTop " + numberOfWords + " words in "
                + inputLocation);
        outputHTML.println("\t\t</title>");
        outputHTML.println("\t\t<link href=\"doc/tagcloud.css\""
                + " rel=\"stylesheet\" type=\"text/css\">");
        outputHTML.println("\t</head>");
        outputHTML.println("<body>");
        outputHTML.println("\t<h2>Top " + numberOfWords + " words in "
                + inputLocation + "</h2>");
        outputHTML.println("<hr>");
        outputHTML.println("<div class=\"cdiv\">");
        outputHTML.println("\t<p class=\"cbox\">");

    }

    /**
     * Generates the body of the HTML file containing the words of the Tag
     * Cloud.
     *
     * @param fontDeque
     *            Deque containing font sizes in sequence of alphabetized words
     *
     * @param countWords
     *            Alphabetized map containing words and their counts
     *
     * @param outputHTML
     *            Output stream to created file
     *
     * @requires <pre>
     * {@code |fontDeque| != 0 && |wordsWithCounts| != 0 && outputHTML.is_open}
     * </pre>
     * @clears fontDeque
     * @ensures <pre>
     * {@code [html file contains appropriate code for tag cloud]}
     * </pre>
     *
     */
    private static void generateHTMLBody(Deque<Integer> fontDeque,
            Map<String, Integer> countWords, PrintWriter outputHTML) {

        for (Map.Entry<String, Integer> entry : countWords.entrySet()) {
            outputHTML.println("\t\t<span style=\"cursor:default\" class=\"f"
                    + fontDeque.removeFirst() + "\" title=\"count: "
                    + entry.getValue() + "\">" + entry.getKey() + "</span>");
        }

    }

    /**
     * Generates the ending HTML code tags.
     *
     * @param outputHTML
     *            the file output stream
     * @requires <pre>
     * outputSite.is_open
     * </pre>
     * @ensures <pre>
     * html file contains ending tags
     * </pre>
     */
    private static void generateHTMLFooter(PrintWriter outputHTML) {
        outputHTML.println("\t</p>");
        outputHTML.println("</div>");
        outputHTML.println("</body>");
        outputHTML.println("</html>");
    }

    /**
     * Processes each word and the amount of times it appears in a text document
     * into a TreeMap.
     *
     * @param in
     *            the input stream to the file to be read
     *
     * @return map containing words and the amount they are displayed in read
     *         file.
     *
     * @requires <pre>
     * {@code in.is_open}
     * </pre>
     * @ensures <pre>
     * {@code Returned map contains words and their counts provided
     * by input file}
     * </pre>
     */
    private static Map<String, Integer> createMapOfWords(BufferedReader in) {

        Map<String, Integer> wordCountInText = new TreeMap<String, Integer>();
        String inputLines = "";
        try {
            inputLines = in.readLine();
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e);
            return wordCountInText;
        }

        int position = 0, counter;
        try {
            while (inputLines != null) {
                while (position < inputLines.length()) {
                    String word = nextWordOrSeparator(inputLines, position,
                            SEPARATORSTRING);

                    if (!SEPARATORSTRING.contains(Character.toString(word
                            .charAt(0)))) {
                        if (!wordCountInText.containsKey(word)) {
                            wordCountInText.put(word, 1);
                        } else {
                            counter = wordCountInText.get(word);
                            counter++;
                            wordCountInText.remove(word);
                            wordCountInText.put(word, counter);

                        }
                    }
                    position += word.length();
                }
                position = 0;
                inputLines = in.readLine();
            }
        } catch (IOException e) {
            System.err.println("Error reading line from file input stream: "
                    + e);
            return wordCountInText;
        }

        return wordCountInText;
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (string of character with
     * length of one in {@code separators}) in the given {@code text} starting
     * at the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires <pre>
     * {@code 0 <= position < |text|}
     * </pre>
     * @ensures <pre>
     * {@code nextWordOrSeparator =
     *   text[ position .. position + |nextWordOrSeparator| )  and
     * if elements(text[ position .. position + 1 )) intersection separators = {}
     * then
     *   elements(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    elements(text[ position .. position + |nextWordOrSeparator| + 1 ))
     *      intersection separators /= {})
     * else
     *   elements(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    elements(text[ position .. position + |nextWordOrSeparator| + 1 ))
     *      is not subset of separators)}
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position,
            String separators) {
        assert text != null : "Violation of: text is not null";
        assert separators != null : "Violation of: separators is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        String result = "";
        int i = position;

        if (!separators.contains(Character.toString(text.charAt(position)))) {

            while (i < text.length()
                    && !separators.contains(Character.toString(text.charAt(i)))) {

                i++;

            }
            result = text.substring(position, i);
        } else {
            while (i < text.length()
                    && separators.contains(Character.toString(text.charAt(i)))) {
                i++;
            }
            result = text.substring(position, i);
        }

        return result.toLowerCase();
    }

    /**
     * Creates a Deque that contains the calculated font sizes and sorts the
     * first n words in the map by highest value, then alphabetizes the map. (n
     * is the user defined number of words desired in tag cloud)
     *
     * @param wordsWithCounts
     *            Map containing the words and their counts
     *
     * @param numberOfWords
     *            User defined number of words to be counted in tag cloud
     * @updates wordsWithCounts
     * @return Deque containing calculated font sizes
     * @requires <pre>
     * {@code |wordsWithCounts| > 0}
     * </pre>
     * @ensures <pre>
     * {@code wordsWithCounts contains the first n words that have been sorted
     * by count and alphabetically as well as returning a deque containing
     * corresponding font sizes.}
     * </pre>
     */
    private static Deque<Integer> getFontDeque(
            Map<String, Integer> wordsWithCounts, int numberOfWords) {

        TreeMap<String, Integer> comparatorMap = new TreeMap<String, Integer>(
                wordsWithCounts);

        ValueComparator maxValueCompare = new ValueComparator(comparatorMap);

        Deque<Integer> wordFontDeque = new LinkedList<Integer>();

        TreeMap<String, Integer> sortedMapByValue = new TreeMap<String, Integer>(
                maxValueCompare);

        sortedMapByValue.putAll(wordsWithCounts);

        wordsWithCounts.clear();

        double minimumCount = sortedMapByValue.lastEntry().getValue();
        double maximumCount = sortedMapByValue.firstEntry().getValue();

        for (int i = 0; i < numberOfWords && !sortedMapByValue.isEmpty(); i++) {
            wordsWithCounts.put(sortedMapByValue.firstEntry().getKey(),
                    sortedMapByValue.firstEntry().getValue());
            sortedMapByValue.remove(sortedMapByValue.firstKey());
        }

        double maxFontSize = 37;

        for (Map.Entry<String, Integer> entry : wordsWithCounts.entrySet()) {

            int sizeOfFont = 0;
            int countOfWord = entry.getValue();
            if (countOfWord > minimumCount) {
                double fontSizeDouble = maxFontSize
                        * (countOfWord - minimumCount)
                        / (maximumCount - minimumCount);
                sizeOfFont = (int) Math.ceil(fontSizeDouble);
                sizeOfFont += 10;
            } else {
                sizeOfFont = 11;
            }

            wordFontDeque.add(sizeOfFont);
        }

        return wordFontDeque;
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {

        BufferedReader inputFile = null;
        PrintWriter outputHTML = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        /*
         * Get input file location and output file name from user. Input file
         * name may not be the empty string. Create input file stream.
         */
        System.out.print("Please enter a valid file location: ");
        String inputLocation = "";
        try {
            inputLocation = in.readLine();
        } catch (IOException e) {
            System.err.println("Error input stream from System: " + e);
            return;
        }

        try {
            inputFile = new BufferedReader(new FileReader(inputLocation));
        } catch (IOException e) {
            System.err.println("Error opening input stream from file: " + e);
            return;
        }
        System.out.println();
        /*
         * Get name of output file and create stream
         */
        String outputFileName = "";
        System.out.print("Please enter a name for the output HTML file: ");
        try {
            outputFileName = in.readLine();
        } catch (IOException e) {
            System.err.println("Error input stream from System: " + e);
            return;
        }

        try {
            outputHTML = new PrintWriter(new BufferedWriter(new FileWriter(
                    outputFileName)));
        } catch (IOException e) {
            System.err.println("Error opening output stream to file: " + e);
            return;
        }

        System.out.println();
        /*
         * Get number of words desired in tag cloud
         */
        int numberOfWords = 0;
        System.out.print("Number of words to be included"
                + " in Tag Cloud (only positive integers): ");
        try {
            numberOfWords = Integer.parseInt(in.readLine());
        } catch (NumberFormatException e) {
            System.err.println("Error concerning integer value " + e);
            return;
        } catch (IOException e) {
            System.err.println("Error in System input stream: " + e);
            return;
        }

        // Generate HTML header
        generateHeaderHTML(outputHTML, inputLocation, outputFileName,
                numberOfWords);

        // Create map of all words in the text document and their counts to use
        // in getFontMap
        Map<String, Integer> wordCountInText = createMapOfWords(inputFile);

        //Error if number of words desired is more then available text
        //Throw exception if the file is empty
        if (wordCountInText.size() < numberOfWords
                && wordCountInText.size() > 0) {
            System.err.println("Error number of words too little.");
        }

        // Create the deque holding all of the fonts
        Deque<Integer> fontDeque = getFontDeque(wordCountInText, numberOfWords);

        generateHTMLBody(fontDeque, wordCountInText, outputHTML);

        // Finish generating html file
        generateHTMLFooter(outputHTML);

        /*
         * Close input and output streams
         */
        try {
            in.close();
            inputFile.close();
            System.out.close();
            outputHTML.close();
        } catch (IOException e) {
            System.err.println("Error closing streams: " + e);
            return;
        }
    }

}
