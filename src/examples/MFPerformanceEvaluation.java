package examples;

import java.io.File;

import alg.mf.MatrixFactorisationRatingPrediction;
import alg.mf.MFGradientDescentRatingPredictionAlg;
import alg.mf.MFSGDRatingPredictionAlg;
import alg.mf.WMFSGDRatingPredictionAlg;

import alg.RatingPredictionRecommender;
import util.evaluator.*;
import util.reader.DatasetReader;

public class MFPerformanceEvaluation {

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

		int nusers = 200;
		int seed = 0;
		int N = 10;	
		int latentSpaceDim = 20;
		
		WMFSGDRatingPredictionAlg alg = new WMFSGDRatingPredictionAlg(reader, latentSpaceDim);
		
		double[] alphas = {1.0,2.0,3.0};
		int[] hs = {1,2,3};
		int[] dims = {5, 10, 15, 20, 25, 30};

		// Set number of times fit() writes to the console as optimisation proceeds
		alg.setNumReports(0);

		RatingPredictionRecommender recalg = 
				new RatingPredictionRecommender(reader, alg);

		// Evaluate the top-N performance of the algorithm
		Evaluator evalrec = new Evaluator(recalg,reader,N,nusers,seed);


		for (double alpha :alphas) {

			alg.setAlpha(alpha);
			for (int h : hs) {
				alg.setNegativeSamplingRate(h);

				for (int dim : dims) {
					alg.setLatentSpaceDim(dim);
					alg.fit();

					double prec = evalrec.aggregratePerformance(new Precision());
					double recall = evalrec.aggregratePerformance(new Recall());

					System.out.printf("N: %d, alpha: %.2f, h: %d, dim: %d, "
							+ "Prec: %.4f,Recall: %.4f\n",
							N,alpha,h,dim,prec,recall);
				}
			}

		}
	}
}

