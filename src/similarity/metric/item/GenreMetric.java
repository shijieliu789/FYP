/**
 * Compute the similarity between two items based on genres
 */

package similarity.metric.item;

import java.util.Set;

import similarity.metric.SimilarityMetric;
import util.Item;


public class GenreMetric implements SimilarityMetric<Item>
{	
	
	/**
	 * constructor - creates a new GenreMetric object
	 */
	public GenreMetric()
	{
	}
	
	/**
	 * computes the similarity between items
	 * @param X - the id of the first item 
	 * @param Y - the id of the second item
	 */
	public double getSimilarity(final Item X, final Item Y)
	{
		// calculate similarity using overlap coefficient
		
		// get the genres sets for items X and Y
		Set<String> genresX = X.getGenres();
		Set<String> genresY = Y.getGenres();
		
		// get the number of common genres between items X and Y
		int count = 0;
		for (String s: genresX)
			if (genresY.contains(s))
				count++;
		
		// calculate the return the overlap coefficient - if division by zero occurs, return zero 
		int denom = Math.min(genresX.size(), genresY.size());
		return (denom > 0 ) ? count * 1.0 / denom : 0;
	}
}
