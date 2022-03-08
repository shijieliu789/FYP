/**
 * Used to evaluate the user-based CF algorithm
 */

package examples;

import java.io.File;
import java.util.ArrayList;

import alg.ub.UBCFRatingPredictionAlg;
import alg.ub.predictor.*;
import neighbourhood.NearestNeighbourhood;
import neighbourhood.Neighbourhood;
import similarity.metric.SimilarityMetric;
import similarity.metric.profile.CosineMetric;
import similarity.metric.profile.MeanSquaredDifferenceMetric;
import similarity.metric.profile.PearsonMetric;
import similarity.metric.profile.PearsonSigWeightingMetric;
import util.evaluator.RatingPredictionEvaluator;
import util.reader.DatasetReader;

import profile.Profile;

public class ExecuteUB_Expt3
{
	public static void main(String[] args)
	{
	

		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml20m";
		// set the path and filename of the output file ...
		DatasetReader reader = new DatasetReader(dataset);

		// create a list of similarity metrics
		ArrayList< SimilarityMetric<Profile> > metricList = new ArrayList<>();
		
		metricList.add(new CosineMetric());
		metricList.add(new PearsonMetric());
		metricList.add(new PearsonSigWeightingMetric(50));
		metricList.add(new MeanSquaredDifferenceMetric(1, 5));
		

		// calculate RMSE and coverage for each predictor using different similarity metrics
		System.out.println("Metric,RMSE,Coverage");
		String[] labels = {"Cosine", "Pearson", "Pearson Sig", "MSD"};
		for (int s = 0; s < metricList.size();s++)
		{
			// configure the user-based CF algorithm - set the predictor, neighbourhood and similarity 
			// metric and evaluate the algorithm
			UBCFPredictor predictor = new DeviationFromUserMeanPredictor();
			Neighbourhood neighbourhood = new NearestNeighbourhood(200);
			SimilarityMetric<Profile> metric = metricList.get(s);
			UBCFRatingPredictionAlg ubcf = 
					new UBCFRatingPredictionAlg(predictor, neighbourhood, 
							metric, reader);
			RatingPredictionEvaluator eval = 
					new RatingPredictionEvaluator(ubcf, reader.getTestData());
			Double RMSE = eval.getRMSE();
			double coverage = eval.getCoverage();	
			System.out.printf("%s,%.6f,%.2f%c\n", labels[s], RMSE, coverage, '%');
		}
	}
}
