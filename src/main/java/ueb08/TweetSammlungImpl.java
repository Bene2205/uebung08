package ueb08;

import org.apache.commons.lang3.tuple.Pair;
import sun.awt.datatransfer.DataTransferer;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.cert.CollectionCertStoreParameters;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TweetSammlungImpl implements TweetSammlung{

    List<String> tweetOrig = new LinkedList<>();
    Map<String, Integer> tweetCounter = new TreeMap<>();
    List<String> stopwords = new ArrayList<>();

    @Override
    public void setStopwords(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            stopwords.add(sc.nextLine());
        }
    }

    @Override
    public void ingest(String tweet) {
        List<String> tweets = TweetSammlung.tokenize(tweet);
        tweetOrig.add(tweet);
        for (String s:
             tweets) {
            String[] tweetSplit = s.split(" ");
            for (String word:
                 tweetSplit) {
                if (tweetCounter.get(word) == null) {
                    if (stopwords.contains(word)) {
                        continue;
                    }
                    tweetCounter.put(word, 1);
                } else {
                    if (stopwords.contains(word)) {
                        continue;
                    }
                    tweetCounter.replace(word, tweetCounter.get(word)+1);
                }
            }
        }

    }

    @Override
    public Iterator<String> vocabIterator() {
        return tweetCounter.keySet().iterator();
    }

    @Override
    public Iterator<String> topHashTags() {
        List<Pair<String, Integer>> topHashtags = new LinkedList<>();
        for (String word:
             tweetCounter.keySet()) {
            if (word.contains("#")) {
                topHashtags.add(Pair.of(word, tweetCounter.get(word)));
            }
        }

        topHashtags.sort(new Comparator<Pair<String, Integer>>() {
            @Override
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                return o2.getRight().compareTo(o1.getRight());
            }
        });

        List<String> tpHash = new LinkedList<>();
        for(int i = 0; i < topHashtags.size(); i++) {
            tpHash.add(topHashtags.get(i).getLeft());
        }

        return tweetCounter.entrySet().stream()
                .filter(e -> e.getKey().startsWith("#"))
                .sorted((a, b) -> -Integer.compare(a.getValue(), b.getValue()))
                .sorted(Comparator.comparingInt(Map.Entry<String, Integer>::getValue).reversed())
                .map(e -> e.getKey())
                .collect(Collectors.toList()).iterator();

        //return tpHash.iterator();
    }

    @Override
    public Iterator<String> topWords() {

        return tweetCounter.entrySet().stream()
                .filter(e -> Character.isAlphabetic(e.getKey().charAt(0)))
                .sorted(Comparator.comparingInt(Map.Entry<String, Integer>::getValue).reversed())
                .map(Map.Entry::getKey)
                //.reduce(new LinkedList<String>(),
                //        (u, s) -> { u.add(s); return u; },
                //        (a, b) -> { b.addAll(a); return b; }).iterator();
                .collect(Collectors.toList()).iterator();

//        List<Pair<String, Integer>> topWords = new LinkedList<>();
//
//        for (String s : tweetCounter.keySet()) {
//            char[] wordSplit = s.toCharArray();
//            if (Character.isAlphabetic(wordSplit[0])){
//                topWords.add(Pair.of(s, tweetCounter.get(s)));
//            }
//        }
//
//        topWords.sort(new Comparator<Pair<String, Integer>>() {
//            @Override
//            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
//                return o2.getRight().compareTo(o1.getRight());
//            }
//        });
//
//        List<String> tpWords = new LinkedList<>();
//
//        for (int i = 0; i < topWords.size()-1; i++) {
//            tpWords.add(topWords.get(i).getLeft());
//        }
//
//        return tpWords.iterator();
    }

    @Override
    public Iterator<Pair<String, Integer>> topTweets() {
        List<Pair<String, Integer>> count = new LinkedList<>();

        return tweetOrig.stream()
                .map(s -> Pair.of(s, TweetSammlung.tokenize(s)))
                .map(p -> Pair.of(p.getLeft(), p.getRight().stream()
                        .mapToInt(s -> stopwords.contains(s)?0:tweetCounter.get(s)).sum()))
                .sorted(Comparator.comparingInt(Pair<String, Integer>::getRight).reversed())
                .collect(Collectors.toList()).iterator();




//        for (String s : tweetOrig) {
//            String[] tweetSplitted = s.split(" ");
//            int buzzAnz = 0;
//            for (String word: tweetSplitted) {
//                if (tweetCounter.containsKey(word)){
//                    if (stopwords.contains(word)) {
//                        continue;
//                    }
//                    buzzAnz += tweetCounter.get(word);
//                }
//            }
//            count.add(Pair.of(s, buzzAnz));
//        }
//
//        count.sort(new Comparator<Pair<String, Integer>>() {
//            @Override
//            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
//                return Integer.compare(o2.getRight(), o1.getRight());
//            }
//        });
//
//        return count.iterator();
    }
}
