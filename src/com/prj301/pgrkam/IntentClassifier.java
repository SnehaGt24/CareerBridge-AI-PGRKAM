package com.prj301.pgrkam;

import java.util.*;
import java.util.regex.Pattern;

public class IntentClassifier {
    public record Prediction(String intent, double confidence, List<Result> alternatives) {}
    public record Result(String intent, double score) {}

    private static final Set<String> STOP_WORDS = Set.of(
        "i","me","my","the","a","an","is","are","am","to","for","of","in","on","at","and","or","can",
        "could","would","should","do","does","did","please","want","need","show","tell","give","find","about",
        "any","some","after","how","what","where","which","there","be","with","from"
    );

    private final List<ServiceInfo> services;
    private final Map<String, Double> idf = new HashMap<>();
    private final Map<String, List<Map<String, Double>>> intentExampleVectors = new HashMap<>();
    private final Map<String, Set<String>> intentKeywords = new HashMap<>();
    private final double threshold;

    public IntentClassifier(List<ServiceInfo> services, double threshold) {
        this.services = services;
        this.threshold = threshold;
        train();
    }

    private void train() {
        List<List<String>> docs = new ArrayList<>();
        Map<String, List<List<String>>> tokensByIntent = new LinkedHashMap<>();

        for (ServiceInfo service : services) {
            List<List<String>> intentDocs = new ArrayList<>();
            for (String example : service.examples()) {
                List<String> tokens = tokenize(example);
                docs.add(tokens);
                intentDocs.add(tokens);
            }
            tokensByIntent.put(service.intent(), intentDocs);
        }

        int n = docs.size();
        Map<String, Integer> df = new HashMap<>();
        for (List<String> doc : docs) {
            for (String term : new HashSet<>(doc)) df.merge(term, 1, Integer::sum);
        }
        for (var e : df.entrySet()) {
            idf.put(e.getKey(), Math.log((n + 1.0) / (e.getValue() + 1.0)) + 1.0);
        }

        for (var e : tokensByIntent.entrySet()) {
            List<Map<String, Double>> vectors = new ArrayList<>();
            Map<String,Integer> aggregate = new HashMap<>();
            for (List<String> tokens : e.getValue()) {
                vectors.add(vectorize(tokens));
                for (String t : tokens) aggregate.merge(t, 1, Integer::sum);
            }
            intentExampleVectors.put(e.getKey(), vectors);
            Set<String> keywords = new HashSet<>();
            aggregate.entrySet().stream()
                .sorted((a,b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(14)
                .forEach(x -> keywords.add(x.getKey()));
            intentKeywords.put(e.getKey(), keywords);
        }
    }

    public Prediction predict(String input) {
        List<String> tokens = tokenize(input);
        if (tokens.isEmpty()) return new Prediction("OUT_OF_SCOPE", 0.0, List.of());
        String normalizedInput = input.toLowerCase(Locale.ROOT);
        if (normalizedInput.matches(".*\\b(movie|movies|cricket|football|weather|song|songs|restaurant|joke|capital|time)\\b.*") || normalizedInput.matches(".*\\b(calculate|calculator|math)\\b.*"))
            return new Prediction("OUT_OF_SCOPE", 0.95, List.of(new Result("OUT_OF_SCOPE", 0.95)));
        Map<String,Double> q = vectorize(tokens);
        Set<String> qSet = new HashSet<>(tokens);
        List<Result> results = new ArrayList<>();

        for (ServiceInfo service : services) {
            double maxCos = 0.0;
            for (Map<String,Double> ex : intentExampleVectors.get(service.intent())) {
                maxCos = Math.max(maxCos, cosine(q, ex));
            }
            Set<String> keys = intentKeywords.get(service.intent());
            long overlap = qSet.stream().filter(keys::contains).count();
            double keywordScore = qSet.isEmpty() ? 0 : (double) overlap / Math.min(4.0, qSet.size());
            double score = Math.min(1.0, 0.82 * maxCos + 0.18 * keywordScore);
            results.add(new Result(service.intent(), score));
        }

        results.sort((a,b) -> Double.compare(b.score(), a.score()));
        Result best = results.get(0);
        double margin = results.size() > 1 ? best.score() - results.get(1).score() : best.score();
        double adjusted = Math.min(1.0, best.score() * 0.90 + Math.max(0, margin) * 0.35);

        String intent = best.intent();
        if (best.score() < threshold && !"OUT_OF_SCOPE".equals(intent)) intent = "OUT_OF_SCOPE";
        if (!"OUT_OF_SCOPE".equals(intent) && margin < 0.025 && best.score() < 0.42) intent = "OUT_OF_SCOPE";

        return new Prediction(intent, adjusted, results.subList(0, Math.min(3, results.size())));
    }

    private static List<String> tokenize(String text) {
        String cleaned = text.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ").trim();
        if (cleaned.isBlank()) return List.of();
        List<String> out = new ArrayList<>();
        for (String raw : cleaned.split(" ")) {
            if (raw.length() < 2 || STOP_WORDS.contains(raw)) continue;
            out.add(stem(raw));
        }
        return out;
    }

    private static String stem(String s) {
        if (s.endsWith("ies") && s.length() > 4) return s.substring(0, s.length()-3) + "y";
        if (s.endsWith("ing") && s.length() > 5) return s.substring(0, s.length()-3);
        if (s.endsWith("ed") && s.length() > 4) return s.substring(0, s.length()-2);
        if (s.endsWith("s") && s.length() > 4 && !s.endsWith("ss")) return s.substring(0, s.length()-1);
        return s;
    }

    private Map<String,Double> vectorize(List<String> tokens) {
        Map<String,Integer> counts = new HashMap<>();
        for (String t : tokens) counts.merge(t, 1, Integer::sum);
        Map<String,Double> v = new HashMap<>();
        int total = Math.max(1, tokens.size());
        for (var e : counts.entrySet()) {
            double tf = (double)e.getValue() / total;
            double idfValue = idf.getOrDefault(e.getKey(), Math.log(idf.size()+2.0));
            v.put(e.getKey(), tf * idfValue);
        }
        return v;
    }

    private static double cosine(Map<String,Double> a, Map<String,Double> b) {
        double dot=0, na=0, nb=0;
        for (double x : a.values()) na += x*x;
        for (double x : b.values()) nb += x*x;
        if (na==0 || nb==0) return 0;
        Map<String,Double> smaller = a.size() < b.size() ? a : b;
        Map<String,Double> larger = a.size() < b.size() ? b : a;
        for (var e : smaller.entrySet()) dot += e.getValue() * larger.getOrDefault(e.getKey(),0.0);
        return dot / (Math.sqrt(na)*Math.sqrt(nb));
    }
}
