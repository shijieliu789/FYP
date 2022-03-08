package examples;

import java.io.File;
import java.text.DecimalFormat;

import alg.ib.IBNNRecommender;
import alg.rerank.CalibrationReranker;
import util.reader.DatasetReader;
import neighbourhood.*;
import profile.Profile;
import similarity.SimilarityMap;
import similarity.metric.MaxMinDistanceMetric;
import similarity.metric.SimilarityMetric;
import similarity.metric.item.GenreMetric;
import alg.rerank.DiversityReranker;
import alg.RerankingRecommender;
import util.Item;
import util.evaluator.Diversity;
import util.evaluator.Evaluator;
import util.evaluator.Precision;
import util.evaluator.Recall;
import util.evaluator.TestPerfInterface;

public class RerankingExample {
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
		int N = 10; // the number of recommendations to be made for each target user
		int nusers = 100; // number of users to evaluate
		
		if (args.length>0)
			seed = Integer.parseInt(args[0]);
		if (args.length>1)
			N = Integer.parseInt(args[1]);
		if (args.length>2)
			nusers = Integer.parseInt(args[2]);
		
		DecimalFormat fmt = new DecimalFormat("#.#####");
		
		////////////////////////////////////////////////////////
		// SimilarityMetric metric = new GenreMetric(reader); 
		  
		SimilarityMetric<Item> metric = new GenreMetric();
		SimilarityMap<Item> simMap = 
				new SimilarityMap<Item>(reader.getItems(),metric);
		SimilarityMap<Item> distanceMap =
				new SimilarityMap<Item>(reader.getItems(),
						new MaxMinDistanceMetric<Item>(metric,simMap));

		
		
		
		
		Neighbourhood neighbourhood = new NearestNeighbourhood(100);
		IBNNRecommender alg =
				new IBNNRecommender(reader,neighbourhood,simMap);
		
		TestPerfInterface[] perfs = 
			{new Diversity(distanceMap), new Precision(), new Recall()};
		System.out.println("L, N, Div, Prec, Recall");
		for (double lambda=0.0;lambda<=1.0;lambda+=0.1) {
			
			
			RerankingRecommender rerankalg = 
					new RerankingRecommender(reader, alg,
							new DiversityReranker(distanceMap,lambda)); // replace with calibrationReranker
//			CalibrationReranker(lambda)
			
			// Evaluate Recommender
			Evaluator eval = new Evaluator(rerankalg,reader,N,nusers);
			double [] p = eval.aggregratePerformance(perfs);
					
			System.out.println(fmt.format(lambda)+", "+N + ", " +
					fmt.format(p[0])+", "+
					fmt.format(p[1])+", "+
					fmt.format(p[2]));
							
			
			
		}
		
		
		
	}


}
