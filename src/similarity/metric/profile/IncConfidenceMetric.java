/**
 * Compute the Increasing Confidence Similarity Metric between (item) profiles.
 */

package similarity.metric.profile;


import java.util.Set;
import similarity.metric.SimilarityMetric;
import profile.Profile;


public class IncConfidenceMetric implements SimilarityMetric<Profile>
{	
	
	private static double RATING_THRESHOLD = 4.0; // the threshold rating for liked items 

	
	
	/**
	 * constructor - creates a new CosineMetric object
	 */
	public IncConfidenceMetric()
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
		// calculate similarity using conf(X => Y) / conf(!X => Y)
		return 0.0;


	}
	


}
