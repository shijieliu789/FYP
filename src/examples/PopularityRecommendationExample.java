/**
 * A class to display the recommendations made by a non-personalised recommender algorithm
 */

package examples;

import java.io.File;
import alg.np.PopularityRecommender;
import java.util.Random;

import util.evaluator.Evaluator;
import util.reader.DatasetReader;

public class PopularityRecommendationExample {
	public static void main(String[] args)
	{		
		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml20m";
		// set the path and filename of the output file ...

		////////////////////////////////////////////////////////////////////////////
		// Read data into a reader object
		///////////////////////////////////////////////////////////////////////////
		DatasetReader reader = new DatasetReader(dataset);
		
		////////////////////////////////////////////////////////
		long seed = 0; // seed for random number generator
		int k = 10; // the number of recommendations to be made for each target user
		int nusers = 500; // number of users to evaluate
		double ratingThreshold = 3.0; // rating threshold above which item is considered liked
		double significanceLevel = 0.05; // for Wilson score
		
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
		
		////////////////////////////////////////////////////////

		
		Random numGen = new Random(seed);
		Integer[] userIds = 
				(Integer[])reader.getUserIds().
				toArray(new Integer[reader.getUserIds().size()]);
		
		
		
		PopularityRecommender alg = new PopularityRecommender(reader,ratingThreshold,significanceLevel);
		Evaluator eval = new Evaluator(alg,reader,k,nusers);
		
		
		// Print recommendation for nusers 
		
		for (int i=0;i<nusers;i++) {	
			int r = numGen.nextInt(userIds.length);		
			
			eval.printRecs(userIds[r]);
		}
	}
}
