package examples;

import java.io.File;
import java.util.Random;

import alg.np.BrowsedItemRecommender;
import util.reader.DatasetReader;
import profile.Profile;
import similarity.SimilarityMap;
import similarity.metric.SimilarityMetric;
import similarity.metric.item.GenreMetric;
import similarity.metric.item.ItemProfileSimilarityMetric;
import similarity.metric.profile.IncConfidenceMetric;
import similarity.metric.profile.GenomeMetric;
import similarity.metric.profile.RatingMetric;
import util.Item;
import util.evaluator.Evaluator;

public class BrowsedItemRecommenderExample {
	public static void main(String[] args)
	{		
		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml1m";
		// set the path and filename of the output file ...
		
		DatasetReader reader = new DatasetReader(dataset);
	
		
		////////////////////////////////////////////////////////
		long seed = 0; // seed for random number generator
		int k = 10; // the number of recommendations to be made for each target user
		int nusers = 10; // number of users to evaluate
		int whichmetric = 0; // select the metric to use
	

		if (args.length>0)
			seed = Integer.parseInt(args[0]);
		if (args.length>1)
			k = Integer.parseInt(args[1]);
		if (args.length>2)
			nusers = Integer.parseInt(args[2]);
		if (args.length>3)
			whichmetric = Integer.parseInt(args[3]);
		////////////////////////////////////////////////////////
		Random numGen = new Random(seed);
		Integer[] userIds = 
				(Integer[])reader.getUserIds().
				toArray(new Integer[reader.getUserIds().size()]);
		

		////////////////////////////////////////////////////////
		// Choose similarity metric
		System.out.println("whichmetric="+whichmetric);
		SimilarityMetric<Item> metric = null;
		SimilarityMap<Item> simMap = null;
		switch (whichmetric) {
		case 0:
			System.out.println("IncConfidence");
			metric = new ItemProfileSimilarityMetric(reader.getItemProfiles(),
					new IncConfidenceMetric());
			simMap = new SimilarityMap<Item>(reader.getItems(),metric);
	
			break;
		case 1:
			System.out.println("Genome");
			metric = new ItemProfileSimilarityMetric(reader.getItemGenomeScores(),	
					new GenomeMetric());
			simMap = new SimilarityMap<Item>(reader.getItems(),metric);
	
			break;
			
		case 2:
			System.out.println("Rating");
			metric = new ItemProfileSimilarityMetric(reader.getItemProfiles(),
					new RatingMetric());
			simMap = new SimilarityMap<Item>(reader.getItems(),metric);
	
			break;
		case 3:
			System.out.println("Genre");
			metric = new GenreMetric();
			simMap = new SimilarityMap<Item>(reader.getItems(),metric);
	
			break;
			
			
		}
		// Print recommendation for nusers users
			
		for (int i=0;i<nusers;i++) {	
			int r = numGen.nextInt(userIds.length);	
			
			// Get the profile of user userId[r]
			Profile p = reader.getUserProfiles().get(userIds[r]);
			
			// Choose an item from the user's profile and use it
			// to generate a recommendation
			for (Integer itemId : p.getIds()) {
				Integer browsedItemId = itemId;
				System.out.println("Browsed item :"+browsedItemId
						+" "+reader.getItems().get(browsedItemId).getName());
				BrowsedItemRecommender alg = new 
						BrowsedItemRecommender(reader,browsedItemId,simMap);
				//temporarily added "M" to make reranker work
				Evaluator eval = new Evaluator(alg,reader,k,nusers, "M");
				eval.printRecs(userIds[r]);
				
				// only use one item from the user's profile
				break;
			}
			
		}
	}

}

