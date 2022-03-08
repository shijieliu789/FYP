package alg.ub.predictor;

import alg.ub.UBCFRatingPredictionAlg;

public interface UBCFPredictor {
	/**
	 * @returns the target user's predicted rating for the target item or null if a prediction cannot be computed
	 * @param alg - the user-based CF rating prediction algorithm
	 * @param userId - the numeric ID of the target user
	 * @param itemId - the numeric ID of the target item
	 */
	public Double getPrediction(final UBCFRatingPredictionAlg alg, final Integer userId, final Integer itemId);
}
