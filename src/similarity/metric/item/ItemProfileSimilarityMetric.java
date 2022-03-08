/**
 * Compute the similarity between two items based on genres
 */

package similarity.metric.item;

import util.Item;
import profile.Profile;
import java.util.Map;

import similarity.metric.SimilarityMetric;
import similarity.metric.profile.RatingMetric;

public class ItemProfileSimilarityMetric implements SimilarityMetric<Item>
{	
	private SimilarityMetric<Profile> profileMetric;
	private Map<Integer, Profile> profileMap;
	/**
	 * constructor - creates a new GenreMetric object
	 */
	public ItemProfileSimilarityMetric(Map<Integer,Profile> profileMap)
	{
		this.profileMap = profileMap;
		this.profileMetric = new RatingMetric();
	}
	
	public ItemProfileSimilarityMetric(Map<Integer,Profile> profileMap,
			SimilarityMetric<Profile> profileMetric)
	{
		this.profileMap = profileMap;
		this.profileMetric = profileMetric;
	   
	}
	
	/**
	 * computes the similarity between items
	 * @param X - the id of the first item 
	 * @param Y - the id of the second item
	 */
	public double getSimilarity(final Item X, final Item Y)
	{
		return profileMetric.
				getSimilarity(profileMap.get(X.getId()),profileMap.get(Y.getId()));
		
	}
}
