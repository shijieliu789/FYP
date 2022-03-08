/**
 * Compute the Increasing Confidence Similarity Metric between (item) profiles.
 */

package similarity.metric.profile;

import profile.Profile;
import similarity.metric.SimilarityMetric;


public class GenomeMetric implements SimilarityMetric<Profile>
{		
	/**
	 * constructor - creates a new CosineMetric object
	 */
	public GenomeMetric()
	{
	}
	
	/**
	 * computes the similarity between profiles
	 * @param profile 1 
	 * @param profile 2
	 */
	@Override
	public double getSimilarity(final Profile genomeX, final Profile genomeY)
	{		
		// calculate similarity using weighted Jaccard
		return 0.0;

	}
	


}
