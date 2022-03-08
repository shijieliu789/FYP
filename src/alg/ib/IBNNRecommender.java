package alg.ib;

import java.util.List;
import java.util.Set;
import alg.Recommender;
import profile.Profile;
import similarity.SimilarityMap;
import util.reader.DatasetReader;

import neighbourhood.Neighbourhood;

public class IBNNRecommender extends Recommender {
	private Neighbourhood neighbourhood; 
	private SimilarityMap simMap;

	public IBNNRecommender(final DatasetReader reader,
			final Neighbourhood neighbourhood,
			final SimilarityMap simMap) {
		super(reader);
		this.neighbourhood = neighbourhood;
		this.simMap = simMap;
		this.neighbourhood.computeNeighbourhoods(simMap);
			
	}
	
	public Profile getRecommendationScores(final Integer userId)
	{
		// User profile
		Set<Integer> itemSet = reader.getItemIds();
		Profile scores = new Profile(userId);
		Profile userProfile = reader.getUserProfiles().get(userId);
		
		for (Integer simId : itemSet) {
			double s = 0.0;

			for (Integer profId: userProfile.getIds())
			{
				if (neighbourhood.isNeighbour(simId,profId)) {
					Double sim = simMap.getSimilarity(simId, profId);
					if (sim!=null) {
						s += sim;
					}
				}
			}
			scores.addValue(simId,s);
		}
		return scores;
		
	}
	

	public List<Integer> getRecommendations(final Integer userId)
	{	
		Profile userProfile = reader.getUserProfiles().get(userId);
		Profile scores = getRecommendationScores(userId);
		
		return getRecommendationsFromScores(userProfile,scores);	
	}

}
