

package examples;

import java.io.File;

import alg.ub.UBCFRatingPredictionAlg;
import neighbourhood.*;
import similarity.*;
import similarity.metric.profile.CosineMetric;
import similarity.metric.SimilarityMetric;
import alg.ub.predictor.*;
import alg.RatingPredictionRecommender;
import util.reader.DatasetReader;
import alg.mf.MFGradientDescentRatingPredictionAlg;
import alg.mf.MFSGDRatingPredictionAlg;
import util.UserItemPair;
import util.evaluator.*;

import java.util.Map;
import java.util.HashMap;

import profile.Profile;

public class UBMFComparison
{
	public static void main(String[] args)
	{


		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml20m";
		// set the path and filename of the output file ...

		// set the path and filename of the output file ...
		String outputFile = "results" + File.separator + "predictions.txt";

		DatasetReader reader = new DatasetReader(dataset);
		/////////////////////////////////////////////////////////////////////////////////
		// configure the user-based CF algorithm - set the predictor, neighbourhood and 
		// similarity metric. 
		
		UBCFPredictor predictor = new SimpleAveragePredictor();
		Neighbourhood neighbourhood = new NearestNeighbourhood(10);
		SimilarityMetric<Profile> metric = new CosineMetric();
		SimilarityMap<Profile> simMap = 
				new SimilarityMap<Profile>(reader.getUserProfiles(),metric);

		/////////////////////////////////////////////////////////////////////////////////
		// Create a User-based CF Rating Prediction Algorithm:

		UBCFRatingPredictionAlg ubcf = 
				new UBCFRatingPredictionAlg(predictor, neighbourhood, simMap, reader);

	
		/////////////////////////////////////////////////////////////////////////////////
		// Create an MF Rating Prediction Algorithm:
		// Again, choose the version of MF (MFGradientDescentRatingPredicationAlg or
		// MFSGDRatingPredictionAlg that works best for you and set the 
		// parameters to those that you have observed works bes
		
		
		
		
		int latentSpaceDim = 30;	
		MFGradientDescentRatingPredictionAlg mfalg = 
				new MFGradientDescentRatingPredictionAlg(reader,latentSpaceDim);
		
		// Setting other Parameters, changing these can change performance:
		// mfalg.setRegularisationWeights(0.5);
		// mfalg.setlearningRate(0.01);
		
		// MUST call fit() on the MF alg before it can be used to generate
		// predictions
		
		mfalg.fit();
		
		
		// EVALUATION: This is a COLD-START EVALUATION
		// We are interested to know how the algorithms perform for users that
		// have few ratings in the training set. So, we run an analysis where we
		// only evaluate over those users whose profile size is less than a given
		// size threshold. Varying the threshold allows us to observe the performance
		// over different levels of "cold start"
		
		
		Map<UserItemPair, Double> testData = reader.getTestData();
		
		// sizeThreshold = max number of interactions in  a user's profile
		
		for (int sizeThreshold=15;sizeThreshold<500;sizeThreshold+=5) {
			Map<UserItemPair, Double> filteredTestData = new HashMap<UserItemPair, Double>();

			// 1. Filter from test set those (user,item)p pairs where
			// the user's profile has size less than the threshold
			for (UserItemPair pair: testData.keySet()) {
				Integer userId = pair.getUserId();
				if (reader.getUserProfiles().get(userId).getSize()<sizeThreshold)
					filteredTestData.put(pair, testData.get(pair));
			}
			// 2. Create a rating prediction evaluator to evaluate the RMSE of 
			// the UBCF algorithm on the filtered test data 
			RatingPredictionEvaluator eval = 
					new RatingPredictionEvaluator(ubcf, filteredTestData);
			eval.writeResults(outputFile); // write predictions to output file
			Double cfRMSE = eval.getRMSE(); // calculate RMSE over all predictions made


			// 3. Create a rating prediction evaluator to evaluate the RMSE of 
			// the MF algorithm on the filtered test data 
			RatingPredictionEvaluator mfeval = 
					new RatingPredictionEvaluator(mfalg, filteredTestData);
			mfeval.writeResults(outputFile); // write predictions to output file
			Double mfRMSE = mfeval.getRMSE(); // calculate RMSE over all predictions made

			// 4. Write out the result
			System.out.printf("Threshold: %d\tUBCF: %.6f\tMF: %.6f\n", 
					sizeThreshold, cfRMSE, mfRMSE);


		}
	}
}
