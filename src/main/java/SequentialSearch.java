import model.DocumentData;
import search.TFIDF;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SequentialSearch {
    public static final String BOOKS_DIRECTORY = "./resources/books";
    public static final String SEARCH_QUERY_1 = "The best detective that catches using deductive methods";
    public static final String SEARCH_QUERY_2 = "The girl that falls through the rabbit hole into a fantasy wonderland";
    public static final String SEARCH_QUERY_3 = "A war between Russia and France in the cold winter";

    public static void main(String [] args) throws FileNotFoundException {
        File documentDirectory=new File(BOOKS_DIRECTORY);
        List<String> documents = Arrays.asList(documentDirectory.list()).stream()
                .map(documentName -> BOOKS_DIRECTORY + "/" +documentName)
                .collect(Collectors.toList());

        searchBy(SEARCH_QUERY_1, documents);
        searchBy(SEARCH_QUERY_2, documents);
        searchBy(SEARCH_QUERY_3, documents);
    }

    private static void searchBy(String searchQuery, List<String> documents) throws FileNotFoundException {
        List<String> terms= TFIDF.getWordsFromLine(searchQuery);
        System.out.println("============================================");
        System.out.println(String.format("Search phrase : %s",searchQuery));
        findMostRelevantDocuments(documents,terms);
    }

    private static void findMostRelevantDocuments(List<String> documents, List<String> terms)
            throws FileNotFoundException {
        Map<String, DocumentData> documentResults = new HashMap<>();

        for(String document:documents){
            BufferedReader bufferedReader = new BufferedReader(new FileReader(document));
            List<String> lines = bufferedReader.lines().collect(Collectors.toList());
            List<String> words = TFIDF.getWordsFromDocument(lines);
            DocumentData documentData = TFIDF.createDocumentData(words, terms);
            documentResults.put(document,documentData);
        }

        Map<Double,List<String>> documentsByScore = TFIDF.getDocumentsScores(terms, documentResults);
        printResults(documentsByScore);
    }

    private static void printResults(Map<Double, List<String>> documentsByScore) {
        for(Map.Entry<Double,List<String>> docScorePair:documentsByScore.entrySet()){
            double score = docScorePair.getKey();
            for(String document:docScorePair.getValue()){
                System.out.println(String.format("Book : %s - score : %f",document.split("/")[3],score));
            }
        }
    }
}
