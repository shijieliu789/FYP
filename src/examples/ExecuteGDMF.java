package examples;

import java.io.File;

import alg.mf.MatrixFactorisationRatingPrediction;
import alg.mf.MFGradientDescentRatingPredictionAlg;
import alg.mf.MFSGDRatingPredictionAlg;
import alg.mf.WMFSGDRatingPredictionAlg;

import alg.RatingPredictionRecommender;
import util.evaluator.*;
import util.reader.DatasetReader;

public class ExecuteGDMF {

	public static void main(String[] args)
	{


		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml20m";
		// set the path and filename of the output file ...
		String outputFile = "results" + File.separator + "predictions.txt";

		DatasetReader reader = new DatasetReader(dataset);

		int nusers = 200;
		int seed = 0;
		int k = 10;
		int which = 2;
		
		int latentSpaceDim = 20;
		
		MatrixFactorisationRatingPrediction alg;
		
		switch (which) {
		
		case 0:
			System.out.println("Gradient Descent Matrix Factorisation");
			alg = 
			new MFGradientDescentRatingPredictionAlg(reader, latentSpaceDim);
			break;
			
		case 1:
			System.out.println("SGD Matrix Factorisation");
			alg = new MFSGDRatingPredictionAlg(reader, latentSpaceDim);
			break;
			
		default:
			System.out.println("Weighted Matrix Factorisation");
			alg = new WMFSGDRatingPredictionAlg(reader, latentSpaceDim);
			break;
		}
		
		alg.fit();
		

		// Evaluate the Rating Prediction Algorithm
		RatingPredictionEvaluator eval = 
				new RatingPredictionEvaluator(alg, reader.getTestData());
		eval.writeResults(outputFile); // write predictions to output file
		Double RMSE = eval.getRMSE(); // calculate RMSE over all predictions made
		System.out.printf("RMSE: %.6f\n", RMSE);

		// Create a top-N recommendation algorithm using the Rating Prediction algorithm

		RatingPredictionRecommender recalg = 
				new RatingPredictionRecommender(reader, alg);

		// Evaluate the top-N performance of the algorithm
		Evaluator evalrec = new Evaluator(recalg,reader,k,nusers,seed);
		System.out.printf("k: %d, Prec: %.4f,Recall: %.4f\n",k,
				evalrec.aggregratePerformance(new Precision()),
				evalrec.aggregratePerformance(new Recall()));


	}
}

