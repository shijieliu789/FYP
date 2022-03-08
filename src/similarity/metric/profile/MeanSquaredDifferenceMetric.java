/**
 * Compute the Mean Squared Difference similarity between profiles.
 */

package similarity.metric.profile;

import java.util.Set;
import similarity.metric.SimilarityMetric;

import profile.Profile;

public class MeanSquaredDifferenceMetric implements SimilarityMetric<Profile>
{
	private double rmax;
	private double rmin;
	/**
	 * constructor - creates a new PearsonMetric object
	 */
	public MeanSquaredDifferenceMetric(double a,double b)
	{
		this.rmin = a;
		this.rmax = b;
		
		/* enforce a difference of 1 if the two bounds are equal */
		if (a==b)
			this.rmax = b+1;
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
