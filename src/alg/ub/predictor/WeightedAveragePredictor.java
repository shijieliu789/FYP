package alg.ub.predictor;

import alg.ub.UBCFRatingPredictionAlg;


public class WeightedAveragePredictor implements UBCFPredictor 
{
	/**
	 * constructor - creates a new WeightedAveragePredictor object
	 */
	
	
	public WeightedAveragePredictor()
	{
	}

	/**
	 * @returns the target user's predicted rating for the target item or null if a prediction cannot be computed
	 * @param alg - UserBasedCF algorithm
	 * @param userId - the numeric ID of the target user
	 * @param itemId - the numerid ID of the target item
	 */
	public Double getPrediction(final UBCFRatingPredictionAlg alg,
			final Integer userId, final Integer itemId)
	{
		return 0.0;
	}
}
