package alg.np;

import alg.Recommender;

import java.util.List;

import java.util.Set;
import java.util.Random;

import profile.Profile;
import util.reader.DatasetReader;

public class RandomRecommender extends Recommender {
	/**
	 * constructor - creates a new RandomRecommender object
	 * @param seed - seed for the random number generator
	 * @param numGen - random number generator
	 */
	
	private long seed;
	private Random numGen ;
	
	public RandomRecommender(final DatasetReader reader) {
		super(reader);
		this.seed = System.nanoTime();
		this.numGen = new Random(seed);
		
	}
	
	public RandomRecommender(final DatasetReader reader, final long seed) {
		super(reader);
		this.seed = seed;
		this.numGen = new Random(seed);
		
	}
	
	public Profile getRecommendationScores(Integer userId) {
		Profile scores = new Profile(userId);
		Set<Integer> itemIds = reader.getItemIds();
		
		// Generate a set of random scores 
		for (Integer item : itemIds) {		
			scores.addValue(item, numGen.nextDouble());			
		}
		return scores;
	}

	/**
	 * @returns the recommendations based on random scores
	 * @param userId - the target user ID
	 */
	public List<Integer> getRecommendations(Integer userId)
	{	
		Profile scores = getRecommendationScores(userId);
		
		// Generate recommendations from the scores
		Profile userProfile = reader.getUserProfiles().get(userId);
		
		List<Integer> recs = getRecommendationsFromScores(userProfile,scores);
		return recs;
	}
}


