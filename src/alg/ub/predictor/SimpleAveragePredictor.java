/**
 * An class to compute the target user's predicted rating for the target item (user-based CF) using the simple average technique.
 */

package alg.ub.predictor;

import java.util.Set;

import alg.ub.UBCFRatingPredictionAlg;


public class SimpleAveragePredictor implements UBCFPredictor 
{
	/**
	 * constructor - creates a new SimpleAveragePredictor object
	 */
	
	
	public SimpleAveragePredictor()
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
		double above = 0;
		int counter = 0;

		// get the neighbours for the user
		
		Set<Integer> neighbours = 
				alg.getNeighbourhood().getNeighbours(userId);

		// return null if the user has no neighbours
		if (neighbours == null)
			return null;
		
		for(Integer neighbour: neighbours) // iterate over each neighbour
		{
			Double rating = 
					alg.getReader().getUserProfiles().get(neighbour).getValue(itemId); // get the neighbour's rating for the target item
			if(rating != null) {
				above += rating.doubleValue();
				counter++;
			}
		}

		if(counter > 0)
			return above / counter;
		else
			return null;
	}
}
