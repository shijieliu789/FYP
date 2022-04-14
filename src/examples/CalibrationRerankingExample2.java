package examples;

import alg.RatingPredictionRecommender;
import alg.RerankingRecommender;
import alg.rerank.Reranker;
import alg.ib.IBNNRecommender;
import alg.mf.WMFSGDRatingPredictionAlg;
import alg.rerank.CalibrationReranker;
import alg.rerank.DiversityReranker;
import neighbourhood.NearestNeighbourhood;
import neighbourhood.Neighbourhood;
import similarity.SimilarityMap;
import similarity.metric.MaxMinDistanceMetric;
import similarity.metric.SimilarityMetric;
import similarity.metric.item.GenreMetric;
import util.Item;
import util.evaluator.*;
import util.reader.DatasetReader;
import java.util.Map;
import java.io.File;
import java.text.DecimalFormat;

public class CalibrationRerankingExample2 {
	public static void main(String[] args)
	{
		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml100k";
		// set the path and filename of the output file ...


		////////////////////////////////////////////////////////////////////////////
		// Read data into a reader object
		///////////////////////////////////////////////////////////////////////////
		DatasetReader reader = new DatasetReader(dataset);


		////////////////////////////////////////////////////////
		long seed = 0; // seed for random number generator
		int N = 5; // the number of recommendations to be made for each target user
		int nusers = 100; // number of users to evaluate

		if (args.length>0)
			seed = Integer.parseInt(args[0]);
		if (args.length>1)
			N = Integer.parseInt(args[1]);
		if (args.length>2)
			nusers = Integer.parseInt(args[2]);

		DecimalFormat fmt = new DecimalFormat("#.########");

		////////////////////////////////////////////////////////
		
		// Use the Weighted Matrix Factorisation Algorithm to
		// do the recommendation

		System.out.println("Running baseline recommender WMF");
		int latentSpaceDim = 20;
		WMFSGDRatingPredictionAlg alg = 
				new WMFSGDRatingPredictionAlg(reader, latentSpaceDim);
		alg.setAlpha(1.0);
		alg.setNegativeSamplingRate(1);
		alg.setNumReports(0);
		alg.fit();
		RatingPredictionRecommender recalg = 
				new RatingPredictionRecommender(reader, alg);
		System.out.println("Finished running baseline recommender WMF");

		TestPerfInterface[] perfs =
			{new Precision(), new Recall()};
		System.out.println("L,   N,   Prec,   Recall");
		for (double lambda=0.0;lambda<=1.0;lambda+=0.5) {

			CalibrationReranker reranker = new CalibrationReranker(reader,lambda);

			RerankingRecommender rerankalg =
					new RerankingRecommender(reader, recalg,
							reranker); // replace with calibrationReranker

			// Evaluate Recommender
			Evaluator eval = new Evaluator(rerankalg,reader,N,nusers,"M");
			double [] p = eval.aggregratePerformance(perfs);

			System.out.println(fmt.format(lambda)+", "+N + ", " +
					fmt.format(p[0])+", "+
					fmt.format(p[1])+", ");
			
			Map<Integer, Double> cklvals = reranker.getCKLvals();
			for (Integer userId : cklvals.keySet() ) {
				System.out.println(userId+" "+cklvals.get(userId));
			}
		}

	}
}
