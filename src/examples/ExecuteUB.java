/**
 * Executes the user-based CF algorithm.
 */

package examples;

import java.io.File;

import alg.ub.UBCFRatingPredictionAlg;
import neighbourhood.*;
import similarity.*;
import similarity.metric.profile.CosineMetric;
import similarity.metric.SimilarityMetric;
import alg.ub.predictor.*;
import alg.RatingPredictionRecommender;
import util.evaluator.*;
import util.reader.DatasetReader;
import profile.Profile;

public class ExecuteUB
{
	public static void main(String[] args)
	{
		
		
		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml1m";
		// set the path and filename of the output file ...
		String outputFile = "results" + File.separator + "predictions.txt";
		
		DatasetReader reader = new DatasetReader(dataset);
		
		/////////////////////////////////////////////////////////////////////////////////
		// configure the user-based CF algorithm - set the predictor, neighbourhood and similarity metric ...
		UBCFPredictor predictor = new SimpleAveragePredictor();
		Neighbourhood neighbourhood = new NearestNeighbourhood(10);
		SimilarityMetric<Profile> metric = new CosineMetric();
		SimilarityMap<Profile> simMap = 
				new SimilarityMap<Profile>(reader.getUserProfiles(),metric);
		
		int k = 10;
		int nusers = 100;
		int seed = 0;
		
		/////////////////////////////////////////////////////////////////////////////////
		// Create a User-based CF Rating Prediction Algorithm:
		
		UBCFRatingPredictionAlg ubcf = 
				new UBCFRatingPredictionAlg(predictor, neighbourhood, simMap, reader);
		
		// Evaluate the Rating Prediction Algorithm
		RatingPredictionEvaluator eval = 
				new RatingPredictionEvaluator(ubcf, reader.getTestData());
		eval.writeResults(outputFile); // write predictions to output file
		Double RMSE = eval.getRMSE(); // calculate RMSE over all predictions made
		double coverage = eval.getCoverage(); // calculate coverage over all predictions
		System.out.printf("RMSE: %.6f\nCoverage: %.2f%c\n", RMSE, coverage, '%');
		
		// Create a top-N recommendation algorithm using the Rating Prediction algorithm
		
		RatingPredictionRecommender ubcfrec = 
				new RatingPredictionRecommender(reader, ubcf);
		
		// Evaluate the top-N performance of the algorithm
		Evaluator evalrec = new Evaluator(ubcfrec,reader,k,nusers,seed);
		System.out.printf("k: %d, Prec: %.4f,Recall: %.4f\n",k,
				evalrec.aggregratePerformance(new Precision()),
				evalrec.aggregratePerformance(new Recall()));
		
		
	}
}
