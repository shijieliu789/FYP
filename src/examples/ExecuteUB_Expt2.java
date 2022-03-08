/**
 * Used to evaluate the user-based CF algorithm.
 */

package examples;

import java.io.File;

import alg.ub.UBCFRatingPredictionAlg;
import alg.ub.predictor.*;
import neighbourhood.Neighbourhood;
import neighbourhood.ThresholdNeighbourhood;
import similarity.*;
import similarity.metric.profile.CosineMetric;
import similarity.metric.SimilarityMetric;
import util.evaluator.RatingPredictionEvaluator;
import util.reader.DatasetReader;

import profile.Profile;

public class ExecuteUB_Expt2
{
	public static void main(String[] args)
	{		
		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml20m";
		// set the path and filename of the output file ...
		DatasetReader reader = new DatasetReader(dataset);
		
		
		
		SimilarityMetric<Profile> metric = new CosineMetric();
		SimilarityMap<Profile> simMap = 
				new SimilarityMap<Profile>(reader.getUserProfiles(),metric);
		
		// calculate RMSE and coverage for different neighbourhood thresholds
		System.out.println("t,RMSE,Coverage");
		for (int t = 0; t <= 80; t+=5)  // iterate over neighbourhood thresholds
		{
			// configure the user-based CF algorithm - set the predictor, neighbourhood and similarity 
			// metric and evaluate the algorithm
			UBCFPredictor predictor = new SimpleAveragePredictor();
			double threshold = t / 100.0;
			Neighbourhood neighbourhood = new ThresholdNeighbourhood(threshold);
			
			UBCFRatingPredictionAlg ubcf = 
					new UBCFRatingPredictionAlg(predictor, neighbourhood, 
							simMap, reader);
			RatingPredictionEvaluator eval = 
					new RatingPredictionEvaluator(ubcf, reader.getTestData());
			Double RMSE = eval.getRMSE();
			double coverage = eval.getCoverage();			
			System.out.printf("%f,%.6f,%.2f%c\n", threshold, RMSE, coverage, '%');
		}
		for (int t = 0; t <= 80; t+=5)  // iterate over neighbourhood thresholds
		{
			// configure the user-based CF algorithm - set the predictor, neighbourhood and similarity 
			// metric and evaluate the algorithm
			UBCFPredictor predictor = new DeviationFromUserMeanPredictor();
			double threshold = t / 100.0;
			Neighbourhood neighbourhood = new ThresholdNeighbourhood(threshold);
			
			UBCFRatingPredictionAlg ubcf = 
					new UBCFRatingPredictionAlg(predictor, neighbourhood, 
							simMap, reader);
			RatingPredictionEvaluator eval = 
					new RatingPredictionEvaluator(ubcf, reader.getTestData());
			Double RMSE = eval.getRMSE();
			double coverage = eval.getCoverage();			
			System.out.printf("%f,%.6f,%.2f%c\n", threshold, RMSE, coverage, '%');
		}
	}
}
