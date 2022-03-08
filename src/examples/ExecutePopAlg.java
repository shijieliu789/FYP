/**
 * A class to display the recommendations made by a non-personalised recommender algorithm
 */

package examples;

import alg.RecAlg;
import alg.np.PopularityRecommender;

import java.io.File;
import java.util.Random;

import util.evaluator.Evaluator;
import util.reader.DatasetReader;


public class ExecutePopAlg {
	public static void main(String[] args)
	{		
		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml20m";
		// set the path and filename of the output file ...

		////////////////////////////////////////////////////////////////////////////
		// configure the non-personalised recommender algorithms and run experiments
		DatasetReader reader = new DatasetReader(dataset);
		RecAlg alg = (RecAlg) new PopularityRecommender(reader,3.0,0.01);
		// display the top-k recommendations for five items
		int k = 3; // the number of recommendations to be made for each target item

		Integer[] userIds = (Integer[])reader.getUserIds().toArray(new Integer[reader.getUserIds().size()]);

		// Print recommendation for 10 random users
		Evaluator eval = new Evaluator(alg, reader,k,500);
		
		Random numGen = new Random();
		
		for (int i=0;i<10;i++) {	
			Integer r = numGen.nextInt(userIds.length);		
			eval.printRecs(userIds[r]);
		}
	}
}
