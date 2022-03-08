package alg.np;


import java.util.List;
import java.util.Set;

import alg.Recommender;
import profile.Profile;
import util.reader.DatasetReader;

import org.apache.commons.math3.distribution.NormalDistribution;




public class PopularityRecommender extends Recommender {
	// threshold above which a rating is considered to be an upvote
	private Double ratingThreshold; 
	// significance level for Wilson score
	private Double significanceLevel;
	
	// a profile of scores used to sort the items for recommendation
	private Profile scores;

	/**
	 * constructor - creates a new PopularityRecommender object
	 * @param reader - dataset reader
	 * @param ratingThreshold 
	 * @param significanceLevel
	 */
	public PopularityRecommender(final DatasetReader reader, double ratingThreshold,
			double significanceLevel) {
		super(reader);
		this.ratingThreshold = ratingThreshold;
		this.significanceLevel = significanceLevel;	
		setScores();
	}
	/**
	 * constructor - creates a new PopularityRecommender object
	 * @param reader - dataset reader
	 * @param ratingThreshold 
	 * 
	 */
	public PopularityRecommender(final DatasetReader reader, double ratingThreshold) {
		super(reader);
		this.ratingThreshold = ratingThreshold;
		this.significanceLevel = 1.0;
		setScores();	
	}
	
	public void setSignificanceLevel(double level)
	{
		// scores are  recomputed whenever the significance level is
		// changed
		this.significanceLevel = level;
		setScores();
	}
	public void setRatingThreshold(double threshold)
	{
		// scores are  recomputed whenever the rating threshold is
		// changed
		this.ratingThreshold = threshold;
		setScores();
	}
	
	private void setScores() {
		
		scores = new Profile(0);
		
		// Get all the items in the dataset
		
		Set<Integer> itemIds = reader.getItems().keySet();
		
		// Set the score of each item and add it into scores
		
		// COMPLETE THIS FUNCTION TO INSTEAD CALCULATE THE SCORE
		// AS PHAT FROM Lecture 2 - the proportion of up-votes
		// obtained by each item.  
		// Count every time an item has 
		// appeared in a user profile.  This is its total number of 
		// votes.  
		// Count every time the item has received a rating greater than
		// or equal to the ratingThreshold - this is its number of up-votes
		
		// phat is the ratio of up-votes to total number of votes
		
		// apply the Wilson formula to correct phat, taking into account
		// the total number of votes
		
		for (Integer item : itemIds) {
			scores.addValue(item, 0.0);			
		}	
		
	}
	
	public Profile getRecommendationScores(Integer userId)
	{
		return scores;
	}

	/**
	 * @returns the recommendations based on the target item
	 * @param itemId - the target item ID
	 */
	public List<Integer> getRecommendations(Integer userId)
	{		
		Profile userProfile = reader.getUserProfiles().get(userId);
		return getRecommendationsFromScores(userProfile,scores);
	}
	
}


