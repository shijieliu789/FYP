/**
 * Compute the Increasing Confidence Similarity Metric between (item) profiles.
 */

package similarity.metric.profile;

import profile.Profile;
import similarity.metric.SimilarityMetric;

public class RatingMetric implements SimilarityMetric<Profile>
{		
	/**
	 * constructor - creates a new CosineMetric object
	 */
	private double alpha;
	
	
	public RatingMetric()
	{
		this.alpha = 1.0;
	}
	public RatingMetric(double alpha)
	{
		this.alpha = 1.0;
	}
	
	/**
	 * computes the similarity between profiles
	 * @param profile 1 
	 * @param profile 2
	 */
	@Override
	public double getSimilarity(final Profile px, final Profile py)
	{	
		/* cosine similarity between two (item) profiles */
		return 0.0;

	}
	


}
