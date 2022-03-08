/**
 * Compute the Pearson similarity between profiles.
 */

package similarity.metric.profile;

import java.util.Set;
import similarity.metric.SimilarityMetric;

import profile.Profile;

public class PearsonMetric implements SimilarityMetric<Profile>
{
	/**
	 * constructor - creates a new PearsonMetric object
	 */
	public PearsonMetric()
	{
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
