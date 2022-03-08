/**
 * Used to evaluate the user-based CF algorithm.
 */

package examples;

import java.io.File;

import alg.np.ItemMeanPredictionAlg;
import alg.ub.UBCFRatingPredictionAlg;
import alg.ub.predictor.*;
import neighbourhood.NearestNeighbourhood;
import neighbourhood.Neighbourhood;
import similarity.SimilarityMap;
import similarity.metric.profile.CosineMetric;
import similarity.metric.profile.PearsonMetric;
import similarity.metric.SimilarityMetric;
import util.evaluator.RatingPredictionEvaluator;
import util.reader.DatasetReader;
import profile.Profile;
public class ExecuteUB_Expt1
{
	public static void main(String[] args)
	{		
		
		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml20m";
		// set the path and filename of the output file ...
		DatasetReader reader = new DatasetReader(dataset);
		
		// create an array of predictors
		UBCFPredictor[] predictors = {
				new SimpleAveragePredictor(),
				new WeightedAveragePredictor(),
				new DeviationFromUserMeanPredictor()
		};
		SimilarityMetric<Profile> metric = new PearsonMetric();
		SimilarityMap<Profile> simMap = 
				new SimilarityMap<Profile>(reader.getUserProfiles(),metric);

		// calculate RMSE and coverage for each predictor at different neighbourhood sizes
		System.out.println("k,NP RMSE,NP Coverage,SAP RMSE,SAP Coverage,WAP RMSE,WAP Coverage,DFUM RMSE,DFUM Coverage");
		
		// firstly run the non-personalised predictor that predicts the
		// mean of the item ratings
		ItemMeanPredictionAlg np = new ItemMeanPredictionAlg(reader);
		RatingPredictionEvaluator eval = new RatingPredictionEvaluator(np, reader.getTestData());
		Double RMSE = eval.getRMSE();
		double coverage = eval.getCoverage();
		System.out.printf("np");
		System.out.printf(",%.6f,%.2f%c\n", RMSE, coverage, '%');
		
		for (int k = 10; k <= 250; k+=10) // iterate over neighbourhood sizes
		{
			System.out.printf("%d", k);
			for (int p = 0; p < predictors.length; p++) // iterate over predictors
			{
				// configure the user-based CF algorithm - set the predictor, neighbourhood and similarity 
				// metric and evaluate the algorithm
				Neighbourhood neighbourhood = new NearestNeighbourhood(k);
				
				UBCFRatingPredictionAlg ubcf = 
						new UBCFRatingPredictionAlg(predictors[p], neighbourhood, 
								simMap, reader);
				eval = new RatingPredictionEvaluator(ubcf, reader.getTestData());
				RMSE = eval.getRMSE();
				coverage = eval.getCoverage();			
				System.out.printf(",%.6f,%.2f%c", RMSE, coverage, '%');
			}
			System.out.println();
		}
	}
}
