package alg;
import java.util.List;
import java.util.Set;



import java.util.Map;

import profile.Profile;
import util.reader.DatasetReader;

// class of top-N recommender that forms a recommendation
// by firstly calling a rating prediction algorithm to make
// predictions for a set of candidate items and then 
// recommends the items with highest predictions

public class RatingPredictionRecommender extends Recommender {
	
	private RatingPredictionAlg predictionAlg;
	private Set<Integer> candidates;
	private Map<Integer, Profile> userProfiles;
	
	public RatingPredictionRecommender(DatasetReader reader, 
			RatingPredictionAlg predictionAlg, Set<Integer> candidates) {
		super(reader);
		this.predictionAlg = predictionAlg;
		this.candidates = candidates;
		this.userProfiles = reader.getUserProfiles();
	}
	public RatingPredictionRecommender(DatasetReader reader, 
			RatingPredictionAlg predictionAlg) {
		super(reader);
		this.predictionAlg = predictionAlg;
		this.candidates = reader.getItemIds();
		this.userProfiles = reader.getUserProfiles();
	}
	
	public Profile getRecommendationScores(Integer userId)
	{
		Profile scores = new Profile(0);
		
		// get a predicted rating for all candidate items
		for (Integer id : candidates) {
			Double r = predictionAlg.getPrediction(userId, id);
			if (r != null)
				scores.addValue(id, r);
		}
		
		return scores;
		
	}
	
	public List<Integer>  getRecommendations(Integer userId)
	{
		Profile scores = getRecommendationScores(userId);
		Profile userProfile = userProfiles.get(userId);
		
		
		// return recommendations in sorted order of predicted rating
		return getRecommendationsFromScores(userProfile,scores);
		
	}
	

}
