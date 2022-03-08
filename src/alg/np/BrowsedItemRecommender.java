package alg.np;


import java.util.List;
import java.util.ArrayList;


import alg.Recommender;
import profile.Profile;
import similarity.SimilarityMap;
import similarity.metric.SimilarityMetric;
import util.reader.DatasetReader;
import util.Item;


public class BrowsedItemRecommender  extends Recommender {
	private SimilarityMap<Item> simMap; // similarity map - stores all item-item similarities
	private Integer itemId; // the item on which recommendation is based
	private Profile scores;

	/**
	 * constructor - creates a new BrowsedItemRecommender object
	 */
	public BrowsedItemRecommender(DatasetReader reader, final Integer itemId,final SimilarityMetric<Item> metric) {
		super(reader);
		this.simMap = new SimilarityMap<Item>(reader.getItems(), metric);
		if (this.simMap==null) {
			System.out.println("Null similarity map");
		}
		setBrowsedItem(itemId);
		
	}
	
	public BrowsedItemRecommender(final DatasetReader reader, final Integer itemId,final SimilarityMap<Item> simMap) {
		super(reader);
		this.simMap = simMap;
		setBrowsedItem(itemId);
		
	}
	
	
	
	public void setBrowsedItem(Integer id)
	{
		this.itemId = id;
		setScores();
	}
	
	private void setScores()
	{	
		// get the item similarity profile
		scores = simMap.getSimilarities(itemId); 	
	}
	
	public Profile getRecommendationScores(final Integer userId)
	{	

		return scores;
		
	}

	/**
	 * @returns the recommendations based on the target item
	 * @param itemId - the target item ID
	 */
	public List<Integer> getRecommendations(final Integer userId)
	{	

		// User profile
		Profile userProfile = reader.getUserProfiles().get(userId);
		if (scores==null || scores.getIds().size()==0) {
			return new ArrayList<Integer>();
		}
		return getRecommendationsFromScores(userProfile,scores);
		
	}
}
