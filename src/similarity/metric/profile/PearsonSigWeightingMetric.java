/**
 * Compute the Pearson similarity between profiles using Significant Weighting.
 */

package similarity.metric.profile;


import similarity.metric.SimilarityMetric;

import profile.Profile;

public class PearsonSigWeightingMetric implements SimilarityMetric<Profile>
{
	private int sizeThreshold;
	/**
	 * constructor - creates a new PearsonSignificantWeightingMetric object
	 */
	public PearsonSigWeightingMetric(int sizeThreshold)
	{
		this.sizeThreshold = sizeThreshold;
	}
		
	/**
	 * computes the similarity between profiles
	 * @param profile 1 
	 * @param profile 2
	 */
	@Override
	public double getSimilarity(final Profile p1, final Profile p2)
	{
       return 0.0;
	}
}

