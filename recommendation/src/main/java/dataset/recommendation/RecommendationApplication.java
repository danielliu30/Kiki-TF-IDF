package dataset.recommendation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.springframework.beans.factory.annotation.Autowired;

public class RecommendationApplication {


	public static void main(String[] args) throws IOException, TasteException {
		CustomerOrder customerOrder = new CustomerOrder();
		
		List<ArrayList<String>> type = new ArrayList<ArrayList<String>>();
		List<ArrayList<String>> flavor = new ArrayList<ArrayList<String>>();
		customerOrder.retreiveCategoryType(type, flavor);
		var IDF = customerOrder.invTermFreq(type);
		var TF = customerOrder.termFreq(type);
		var tfIdf1 = IDF.get("cake") * TF.get(0).get("cake");
		var tfIdf2 = IDF.get("cake") * TF.get(1).get("cake");
		var result = customerOrder.tfIdf(TF, IDF);
		var similarity = customerOrder.cosineSimilarity(0, result);
		// DataModel dataModel = new FileDataModel(new File("src\\main\\resources\\typeData.csv"));
		// // UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
		// EuclideanDistanceSimilarity similarity = new EuclideanDistanceSimilarity(dataModel);
		

		// // UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.5,
		// // similarity, dataModel);
		// UserNeighborhood neighborhood = new NearestNUserNeighborhood(10, similarity, dataModel);
		// long[] neighbors = neighborhood.getUserNeighborhood(2);

		// for (long user : neighbors) {
		// 	System.out.print(user + " ");
		// }
		// UserBasedRecommender recommender = new GenericUserBasedRecommender(dataModel, neighborhood, similarity);

		// //This model needs enough variation to make a recommendation otherwise the items user behavior is too distinct
		// List<RecommendedItem> recommendations = recommender.recommend(2, 5);
		
		// for (RecommendedItem item : recommendations) {
		// 	System.out.println(item);
		// }
		// FileWriter csv = new FileWriter("src\\main\\resources\\typeData.csv");
		// for(int i =0; i < 50;i++){
		// 	int user = ThreadLocalRandom.current().nextInt(1,20+1);
		// 	int type = ThreadLocalRandom.current().nextInt(1,10+1);
		// 	int rating = ThreadLocalRandom.current().nextInt(1,5+1);
		// 	csv.append(user+","+type+","+rating);
		// 	csv.append("\n");
		// }
		
		// csv.flush();
		// csv.close();
	}

}
