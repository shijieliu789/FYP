/**
 * A class to execute various non-personalised recommender algorithms.
 * Please do not edit this class.
 */

package examples;

import java.io.File;
import java.util.Random;

import alg.Recommender;
import alg.np.BrowsedItemRecommender;
import alg.np.PopularityRecommender;
import alg.np.RandomRecommender;
import similarity.metric.profile.GenomeMetric;
import similarity.metric.profile.IncConfidenceMetric;
import similarity.metric.profile.RatingMetric;
import similarity.metric.item.GenreMetric;
import similarity.metric.item.ItemProfileSimilarityMetric;
import util.evaluator.Evaluator;
import util.evaluator.Precision;
import util.evaluator.Recall;
import util.reader.DatasetReader;

import java.text.DecimalFormat;


public class NP_PerformanceEvaluation {
	public static void main(String[] args)
	{	
		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml20m";
		// set the path and filename of the output file ...
	
		
		////////////////////////////////////////////////////////
		int seed = 0;
		int k = 10; // the number of recommendations to be made for each target item
		int nusers = 500; // number of users to evaluate
		double ratingThreshold = 3.0;
		double significanceLevel = 0.05;
		
		DecimalFormat fmt = new DecimalFormat("#.#####");
		
		if (args.length>0)
			seed = Integer.parseInt(args[0]);
		if (args.length>1)
			k = Integer.parseInt(args[1]);
		if (args.length>2)
			nusers = Integer.parseInt(args[2]);
		if (args.length>3)
			ratingThreshold = Double.parseDouble(args[3]);
		if (args.length>4)
			significanceLevel = Double.parseDouble(args[4]);
		
			
		
		System.out.println("Random Seed\t: "+seed);
		System.out.println("Number Recommendations\t: "+k);
		System.out.println("Number Tested Users\t: "+nusers);
		System.out.println("Relevance Rating Threshold\t: "+ratingThreshold);
		System.out.println("Significance Level\t: "+significanceLevel);
		
		Random numGen = new Random(seed);
		

		////////////////////////////////////////////////////////////////////////////
		// Read data into a reader object
		///////////////////////////////////////////////////////////////////////////
		DatasetReader reader = new DatasetReader(dataset);

		
	////////////////////////////////////////////////////////////////////////////
		
		System.out.println("k,algorithm,mean rating,rec. coverage,item space coverage,rec. popularity,precision,recall");

		
		Evaluator eval;
		
		Double[] significanceLevels = {0.01,0.05,0.1,0.5,1.0};
		Integer[] ratingThresholds = {1,2,3,4,5};
		
		for (int s=0;s<significanceLevels.length;s++) {
			Double level = significanceLevels[s];
			//		Integer threshold = ratingThresholds[s];
			Recommender popalg = new PopularityRecommender(reader,ratingThreshold,
					level);
			System.out.println("Significance Level="+level);	
			// create an Evaluator object 
			eval = new Evaluator(popalg, reader, k,nusers,seed);
			// display results for the recommender algorithm
			System.out.println(k + "," + "PopularityRecommender" + "," + 
					fmt.format(eval.getRecommendationAverageRating()) + "," + 
					fmt.format(eval.getRecommendationCoverage()) + "," +
					fmt.format(eval.getRecommendationPopularity()) + "," +
					fmt.format(eval.aggregratePerformance(new Precision())) + "," +
					fmt.format(eval.aggregratePerformance(new Recall())));
		}
	}
}
