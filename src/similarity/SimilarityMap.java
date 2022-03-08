/**
 * This class computes and stores the pairwise similarities between all items
 */

package similarity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import profile.Profile;
import similarity.metric.SimilarityMetric;
import util.reader.DatasetReader;
import util.Item;

public class SimilarityMap<T> 
{
	private Map<Integer,Profile> simMap; // stores item-item similarities

	/**
	 * constructor - creates a new SimilarityMap object
	 */
	public SimilarityMap()
	{
		simMap = new HashMap<Integer,Profile>();
	}

	/**
	 * constructor - creates a new SimilarityMap object
	 * @param reader - dataset reader
	 * @param metric - similarity metric
	 */
	public SimilarityMap(final Map<Integer,T> map, final SimilarityMetric<T> metric)
	{		
		simMap = new HashMap<Integer,Profile>();

		// get the set of map ids
		Set<Integer> mapIds = map.keySet();
		
		
		// compute pairwise similarities between objects 
		for(Integer id1: mapIds)
			for(Integer id2: mapIds) {
				if (id1 != id2) {
					double sim = 
							metric.getSimilarity(map.get(id1), map.get(id2));
					if (sim > 0) {
						setSimilarity(id1, id2, sim);
					}
				}
			}
		return;
	}

	/**
	 * @returns the numeric IDs of the profiles
	 */
	public Set<Integer> getIds()
	{
		return simMap.keySet();
	}

	/**
	 * @returns the similarity profile
	 * @param the numeric ID of the profile
	 */
	public Profile getSimilarities(Integer id)
	{
		return simMap.get(id);
	}

	/**
	 * @returns the similarity between two profiles
	 * @param the numeric ID of the first profile
	 * @param the numeric ID of the second profile
	 */
	public double getSimilarity(final Integer id1, final Integer id2)
	{
		if(simMap.containsKey(id1))
			return (simMap.get(id1).contains(id2) ? simMap.get(id1).getValue(id2).doubleValue() : 0);
		else 
			return 0;
	}

	/**
	 * adds the similarity between two profiles to the map
	 * @param the numeric ID of the first profile
	 * @param the numeric ID of the second profile
	 */
	public void setSimilarity(final Integer id1, final Integer id2, final double sim)
	{
		Profile profile = simMap.containsKey(id1) ? simMap.get(id1) : new Profile(id1);
		profile.addValue(id2, Double.valueOf(sim));
		simMap.put(id1, profile);
	}

	/**
	 * @returns a string representation of all similarity values
	 */
	@Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer();

		for(Integer id: simMap.keySet())
			buf.append(simMap.get(id).toString());

		return buf.toString();
	}
}