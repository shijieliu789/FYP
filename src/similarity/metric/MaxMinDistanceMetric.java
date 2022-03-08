package similarity.metric;

import profile.Profile;
import similarity.SimilarityMap;

public class MaxMinDistanceMetric<T> implements SimilarityMetric<T> {
	private SimilarityMetric<T> simMetric;
	private Double minSim;
	private Double maxSim;
	
	// By default assume the maximum and minimum of the original
	// similarity function are 1 and 0
	public MaxMinDistanceMetric(SimilarityMetric<T> simMetric) {
		this.simMetric = simMetric;
		this.minSim = 0.0;
		this.maxSim = 1.0;
	}
	
	// Receive max and min values as input parameters to 
	// constructor
	public MaxMinDistanceMetric(SimilarityMetric<T> simMetric, double max, double min) {
		this.simMetric = simMetric;
		this.minSim = min;
		this.maxSim = max;
	}
	
	// Compute max and min from similarity data computed over some
	// set of profiles
	public MaxMinDistanceMetric(SimilarityMetric<T> simMetric, SimilarityMap<T> simMap) {
		this.simMetric = simMetric;
		computeMaxMin(simMap);
	}
	
	// max-min normalisation to convert similarity values into
	// a distance value in range [0,1]
	@Override
	public double getSimilarity(final T X, final T Y)
	{
		if (maxSim==minSim)
			return 0;
		double s = simMetric.getSimilarity(X, Y);
		
		s = (maxSim -s )/(maxSim-minSim);

		return s;
	}


	// Compute the maximum and minimum directly from simMap
	private void computeMaxMin(SimilarityMap<T> simMap) {

		minSim = Double.POSITIVE_INFINITY;
		maxSim = Double.NEGATIVE_INFINITY;
		int numSims=0;
		for (Integer id: simMap.getIds()) {
			Profile p = simMap.getSimilarities(id);
			numSims++;
			for (Integer id2 : p.getIds()) {
				Double s = p.getValue(id2);
				if (s>maxSim)
					maxSim = s;
				if (s<minSim)
					minSim = s;
			}
		}
		int n = simMap.getIds().size();
		if (numSims< n*(n-1))
			minSim=0.0;
	}

	
}
