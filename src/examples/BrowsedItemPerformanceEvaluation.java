package examples;

import java.io.File;

import java.text.DecimalFormat;


import alg.Recommender;
import util.reader.DatasetReader;
import util.Item;
import util.evaluator.Evaluator;
import util.evaluator.Precision;
import util.evaluator.Recall;
import profile.Profile;
import similarity.SimilarityMap;
import similarity.metric.profile.GenomeMetric;
import similarity.metric.profile.IncConfidenceMetric;
import similarity.metric.SimilarityMetric;
import similarity.metric.item.GenreMetric;
import similarity.metric.item.ItemProfileSimilarityMetric;
import alg.np.BrowsedItemRecommender;

public class BrowsedItemPerformanceEvaluation {
	public static void main(String[] args)
	{	

		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml20m";
		
		


		////////////////////////////////////////////////////////
		int seed = 0;
		int k = 20; // the number of recommendations to be made for each target item
		int nusers = 500; // number of users to evaluate
		int whichmetric=0; // which of the three similarity metrics to choose

		DecimalFormat fmt = new DecimalFormat("#.#####");

		if (args.length>0)
			seed = Integer.parseInt(args[0]);
		if (args.length>1)
			k = Integer.parseInt(args[1]);
		if (args.length>2)
			nusers = Integer.parseInt(args[2]);
		
		if (args.length>3)
			whichmetric = Integer.parseInt(args[3]);
		

		////////////////////////////////////////////////////////
		

		System.out.println("Random Seed\t: "+seed);
		System.out.println("Number Recommendations\t: "+k);
		System.out.println("Number Tested Users\t: "+nusers);
		
		////////////////////////////////////////////////////////////////////////////
		// Read the data ///////////////////////////////////////////////////////////
		DatasetReader reader = new DatasetReader(dataset);
		////////////////////////////////////////////////////////////////////////////
		
		
		// Choose similarity metric
		SimilarityMetric<Item> metric;
		
		switch (whichmetric) {
		case 1:
			metric = new ItemProfileSimilarityMetric(reader.getItemProfiles(),
						new IncConfidenceMetric());
			break;
		case 2:
			metric = new ItemProfileSimilarityMetric(reader.getItemGenomeScores(),
					new GenomeMetric());
			break;
		default:
			metric = new GenreMetric();

		}
		SimilarityMap<Item> simMap = new SimilarityMap<Item>(reader.getItems(),metric);
		
		// Evaluate by choosing browsed items in different ways. Once the browsed
		// item is chosen, the same item is used to make a recommendation for each
		// evaluated user.  
		
		///////////////////////////////////////////////////////////////////////
		// Choose the most popular item to browse   ///////////////////////////
		///////////////////////////////////////////////////////////////////////
		int s = -1;
		int popItem = 0;
		for (Integer itemId : reader.getItemIds()) {
			Profile p =reader.getItemProfiles().get(itemId);
			if (s<p.getSize()) {
				s = p.getSize();
				popItem = itemId;
			}
		}
		System.out.println("Browsing most popular item "+popItem+" rated by "+s+" users.");
		
		
		System.out.println("k,coverage,popularity,precision,recall");

		Recommender alg = new BrowsedItemRecommender(reader,popItem,simMap);
		//Temporarily added "M" as the param to make reranker work
		Evaluator eval = new Evaluator(alg, reader, k,nusers,seed, "M");
		// display results for the recommender algorithm
		System.out.println(k + "," +  
				fmt.format(eval.getRecommendationCoverage()) + "," +
				fmt.format(eval.getRecommendationPopularity()) + "," +
				fmt.format(eval.aggregratePerformance(new Precision())) + "," +
				fmt.format(eval.aggregratePerformance(new Recall())));
		
		
		///////////////////////////////////////////////////////////////////////
		// Choose the least popular item to browse  ///////////////////////////
		///////////////////////////////////////////////////////////////////////
		s = reader.getUserIds().size();
		int leastPopItem = 0;
		for (Integer itemId : reader.getItemIds()) {
			Profile p =reader.getItemProfiles().get(itemId);
			if (s>p.getSize()) {
				s = p.getSize();
				leastPopItem = itemId;
			}
		}
		System.out.println("Browsing least popular item "+leastPopItem+" rated by "+s+" users.");


		System.out.println("k,coverage,popularity,precision,recall");

		alg = new BrowsedItemRecommender(reader,leastPopItem,simMap);
		eval = new Evaluator(alg, reader, k,nusers,seed, "M");
		// display results for the recommender algorithm
		System.out.println(k + "," +  
				fmt.format(eval.getRecommendationCoverage()) + "," +
				fmt.format(eval.getRecommendationPopularity()) + "," +
				fmt.format(eval.aggregratePerformance(new Precision())) + "," +
				fmt.format(eval.aggregratePerformance(new Recall())));
		
		
		///////////////////////////////////////////////////////////////////////
		// Choose the item with highest average genome score to browse   //////
		///////////////////////////////////////////////////////////////////////
		Double avgs = -1.0;
		int mostGenomesItem = 0;
		for (Integer itemId : reader.getItemIds()) {
			Profile p =reader.getItemGenomeScores().get(itemId);
			Double score = 0.0;
			for (Integer tag : p.getIds()) {
				score += p.getValue(tag);
			}
			score = score/p.getIds().size();
			if (avgs<score) {
				avgs = score;
				mostGenomesItem  = itemId;
			}
		}
		System.out.println("Browsing item "+mostGenomesItem+" with greatest average genome score of "+avgs+".");


		System.out.println("k,coverage,popularity,precision,recall");

		alg = new BrowsedItemRecommender(reader,mostGenomesItem,simMap);
		eval = new Evaluator(alg, reader, k,nusers,seed, "M");
		// display results for the recommender algorithm
		System.out.println(k + "," +  
				fmt.format(eval.getRecommendationCoverage()) + "," +
				fmt.format(eval.getRecommendationPopularity()) + "," +
				fmt.format(eval.aggregratePerformance(new Precision())) + "," +
				fmt.format(eval.aggregratePerformance(new Recall())));
		
		
		///////////////////////////////////////////////////////////////////////
		// Choose the item with lowest average genome score to browse   //////
		///////////////////////////////////////////////////////////////////////
		avgs = 100.0;
		int leastGenomesItem = 0;
		for (Integer itemId : reader.getItemIds()) {
			Profile p =reader.getItemGenomeScores().get(itemId);
			Double score = 0.0;
			for (Integer tag : p.getIds()) {
				score += p.getValue(tag);
			}
			score = score/p.getIds().size();
			if (avgs>score) {
				avgs = score;
				leastGenomesItem  = itemId;
			}
		}
		System.out.println("Browsing item "+leastGenomesItem+" with least average genome score of "+avgs+".");
		
		
		System.out.println("k,coverage,popularity,precision,recall");

		alg = new BrowsedItemRecommender(reader,leastGenomesItem,simMap);
		eval = new Evaluator(alg, reader, k,nusers,seed, "M");
		// display results for the recommender algorithm
		System.out.println(k + "," +  
				fmt.format(eval.getRecommendationCoverage()) + "," +
				fmt.format(eval.getRecommendationPopularity()) + "," +
				fmt.format(eval.aggregratePerformance(new Precision())) + "," +
				fmt.format(eval.aggregratePerformance(new Recall())));
		
		
		///////////////////////////////////////////////////////////////////////
		// Choose an item with greatest number of genres to browse       //////
		///////////////////////////////////////////////////////////////////////
		s = -1;
		int mostGenresItem = 0;
		for (Integer itemId : reader.getItemIds()) {
			int numgenres = reader.getItem(itemId).getGenres().size();
			if (s<numgenres ) {
				s = numgenres;
				mostGenresItem  = itemId;
			}
		}
		System.out.println("Browsing item "+mostGenresItem+" with most number of genres, "+s+".");


		System.out.println("k,coverage,popularity,precision,recall");

		alg = new BrowsedItemRecommender(reader,mostGenresItem,simMap);
		eval = new Evaluator(alg, reader, k,nusers,seed, "M");
		// display results for the recommender algorithm
		System.out.println(k + "," +  
				fmt.format(eval.getRecommendationCoverage()) + "," +
				fmt.format(eval.getRecommendationPopularity()) + "," +
				fmt.format(eval.aggregratePerformance(new Precision())) + "," +
				fmt.format(eval.aggregratePerformance(new Recall())));
		
		
		///////////////////////////////////////////////////////////////////////
		// Choose an item with least number of genres to browse          //////
		///////////////////////////////////////////////////////////////////////
		s = reader.getItems().size();
		int leastGenresItem = 0;
		for (Integer itemId : reader.getItemIds()) {
			int numgenres = reader.getItem(itemId).getGenres().size();
			if (s>numgenres ) {
				s = numgenres;
				leastGenresItem  = itemId;
			}
		}
		System.out.println("Browsing item "+leastGenresItem+" with least number of genres, "+s+".");


		System.out.println("k,coverage,popularity,precision,recall");

		alg = new BrowsedItemRecommender(reader,leastGenresItem,simMap);
		eval = new Evaluator(alg, reader, k,nusers,seed, "M");
		// display results for the recommender algorithm
		System.out.println(k + "," +  
				fmt.format(eval.getRecommendationCoverage()) + "," +
				fmt.format(eval.getRecommendationPopularity()) + "," +
				fmt.format(eval.aggregratePerformance(new Precision())) + "," +
				fmt.format(eval.aggregratePerformance(new Recall())));
			
	
	}

}
