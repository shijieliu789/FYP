/**
 * This class computes and stores the pairwise similarities between user (or item) profiles.
 */

package similarity;

import java.util.Map;
import similarity.metric.SimilarityMetric;

import profile.Profile;

public class ProfileSimilarityMap extends SimilarityMap
{
	/**
	 * constructor - creates a new SimilarityMap object
	 */
	public ProfileSimilarityMap()
	{
		super();
	}
	
	/**
	 * constructor - creates a new SimilarityMap object
	 * @param profileMap
	 * @param metric
	 */
	public ProfileSimilarityMap(final Map<Integer,Profile> profileMap, final SimilarityMetric<Profile> metric)
	{
		super();
		
		// compute pairwise similarities between profiles
		for(Integer id1: profileMap.keySet())
			for(Integer id2: profileMap.keySet())
				if(id2 < id1)
				{
					double sim = metric.getSimilarity(profileMap.get(id1), profileMap.get(id2));
					if(sim > 0)
					{
						setSimilarity(id1, id2, sim);
						setSimilarity(id2, id1, sim);
					}
				}
	}
}
