/**
 * A class to implement the user-based collaborative filtering algorithm
 * 
 * Michael O'Mahony
 * 20/01/2011
 */

package alg.ub;

import alg.RatingPredictionAlg;
import alg.ub.predictor.UBCFPredictor;
import neighbourhood.Neighbourhood;
import similarity.*;
import util.reader.DatasetReader;
import profile.Profile;
import similarity.metric.SimilarityMetric;

public class UBCFRatingPredictionAlg implements RatingPredictionAlg
{
	private UBCFPredictor predictor; // the predictor technique  
	private Neighbourhood neighbourhood; // the neighbourhood technique
	private DatasetReader reader; // dataset reader
	private SimilarityMap<Profile> simMap; // similarity map - stores all user-user similarities
	
	/**
	 * constructor - creates a new UserBasedCF object
	 * @param predictor - the predictor technique
	 * @param neighbourhood - the neighbourhood technique
	 * @param metric - the user-user similarity metric
	 * @param reader - dataset reader
	 */
	public UBCFRatingPredictionAlg(final UBCFPredictor predictor, final Neighbourhood neighbourhood, 
				final SimilarityMetric<Profile> metric, final DatasetReader reader)
	{
		this.predictor = predictor;
		this.neighbourhood = neighbourhood;
		this.reader = reader;
		this.simMap = new SimilarityMap<Profile>(reader.getUserProfiles(), metric); // compute all user-user similarities
		this.neighbourhood.computeNeighbourhoods(simMap); // compute the neighbourhoods for all users
	}
	
	public UBCFRatingPredictionAlg(final UBCFPredictor predictor, final Neighbourhood neighbourhood, 
				final SimilarityMap<Profile> simMap, final DatasetReader reader)
	{
		this.predictor = predictor;
		this.neighbourhood = neighbourhood;
		this.reader = reader;
		this.simMap = simMap; // compute all user-user similarities
		this.neighbourhood.computeNeighbourhoods(simMap); // compute the neighbourhoods for all users
	}
	
	/**
	 * @returns the target user's predicted rating for the target item or null if a prediction cannot be computed
	 * @param userId - the target user ID
	 * @param itemId - the target item ID
	 */
	public Double getPrediction(final Integer userId, final Integer itemId)
	{	
		return predictor.getPrediction(this, userId, itemId);
	}

	
	public Neighbourhood getNeighbourhood() {
		return neighbourhood;
	}
	public SimilarityMap<Profile> getSimilarityMap() {
		return simMap;
	}
	public DatasetReader getReader() {
		return reader;
	}
	

}