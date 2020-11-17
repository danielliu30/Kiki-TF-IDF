package dataset.recommendation;

import java.io.FileWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import org.springframework.stereotype.Service;

import it.unimi.dsi.fastutil.Hash;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

@Service
public class CustomerOrder {
    private static DynamoDbClient client = DynamoDbClient.builder().region(Region.US_EAST_1).build();

    private static MongoClient mongoClient = MongoClients
            .create("mongodb+srv://root:8lfAXCyJfHCQojjf@cluster0.asinm.mongodb.net/Customers?retryWrites=true&w=majority");
    private static MongoDatabase database = mongoClient.getDatabase("Customers");

    public CustomerOrder() {

    }

    void retreiveCategoryType(List<ArrayList<String>> type, List<ArrayList<String>> flavor) {
        ScanResponse response;
        try {
            ScanRequest request = ScanRequest.builder().tableName("Customers").build();

            response = client.scan(request);

            for (Map<String, AttributeValue> item : response.items()) {

                ArrayList<String> subType = new ArrayList<>();
                ArrayList<String> subFlavor = new ArrayList<>();
                List<AttributeValue> orderHistory = item.get("orders").l();
                for (AttributeValue pair : orderHistory) {

                    subType.add(pair.m().get("category").s());
                    subFlavor.add(pair.m().get("flavor").s());
                }

                type.add(subType);
                flavor.add(subFlavor);
            }
        } catch (Exception e) {

        }

    }

    Map<String, Double> invTermFreq(List<ArrayList<String>> type) {
        Map<String, Double> freqs = new HashMap<>();

        int docSize = 0;
        for (List<String> doc : type) {
            for (String word : doc) {
                freqs.putIfAbsent(word, 0.0);
                freqs.put(word, freqs.get(word).intValue() + 1.0);
            }
            docSize += doc.size();
        }
        for (Map.Entry<String, Double> pair : freqs.entrySet()) {

            freqs.put(pair.getKey(), Math.log(docSize / pair.getValue().doubleValue()));
        }
        return freqs;
    }

    HashMap<Integer, Map<String, Double>> termFreq(List<ArrayList<String>> type) {
        var individual = new HashMap<Integer, Map<String, Double>>();
        int user = 0;

        for (List<String> doc : type) {
            individual.putIfAbsent(user, new HashMap<String, Double>());
            for (String word : doc) {
                individual.get(user).putIfAbsent(word, 0.0);
                individual.get(user).put(word, individual.get(user).get(word) + (1.0 / doc.size()));
            }
            user++;

        }

        return individual;
    }

    Map<Integer, Map<String, Double>> tfIdf(HashMap<Integer, Map<String, Double>> vec1, Map<String, Double> vec2) {
        Map<Integer, Map<String, Double>> res = new HashMap<Integer, Map<String, Double>>();

        for (int person : vec1.keySet()) {
            Map<String, Double> user = new HashMap<>();
            for (String word : vec2.keySet()) {
                // place normalized value
                double num1 = vec1.get(person).getOrDefault(word, 0.0);
                double num2 = vec2.get(word);
                user.put(word, num1 * num2);
            }
            res.put(person, user);
        }

        return res;
    }

    PriorityQueue<Double> cosineSimilarity(int user, Map<Integer, Map<String, Double>> vectors) {
        PriorityQueue<Double> queue = new PriorityQueue<>((val1, val2) -> (val1 == val2) ? 0 : (int) (val1 - val2));
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int ind : vectors.keySet()) {
            if (ind != user) {
                for (String word : vectors.get(ind).keySet()) {
                    double v1 = vectors.get(ind).get(word);
                    double v2 = vectors.get(user).get(word);
                    dotProduct += v1 * v2;
                    normA += Math.pow(v1, 2);
                    normB += Math.pow(v2, 2);

                }
                // need to assocate user with the value added to the queue
                queue.add(dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)));

            }
        }

        return queue;
    }
    
    // private void placeRecommendation(){
    //     database.getCollection("Orders").
    // }

}