package hotelapp;

import java.util.HashMap;
import java.util.Map;

public class WordCounter {

    /**
     * Counts the frequency of words in an array of strings.
     *
     * @param reviewStr An array of strings containing the words to be counted.
     * @return A map where keys are words, and values are their respective frequencies.
     */
    public Map<String, Integer> countWordFrequency(String[] reviewStr) {
        Map<String, Integer> wordFrequencyMap = new HashMap<>();
        for (String word : reviewStr) {
            if (word.isEmpty()) {
                continue;
            }
            if (wordFrequencyMap.containsKey(word)) {
                int frequency = wordFrequencyMap.get(word);
                wordFrequencyMap.put(word, frequency + 1);
            } else {
                wordFrequencyMap.put(word, 1);
            }
        }
        return wordFrequencyMap;
    }

}
