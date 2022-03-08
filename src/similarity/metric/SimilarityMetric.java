/**
 * An interface to compute the similarity between two items
 */

package similarity.metric;


public interface SimilarityMetric<T> 
{
	/**
	 * computes the similarity between two Objects
	 * @param X - the first object of type T 
	 * @param Y - the second object of type T
	 */
	public double getSimilarity(final T X, final T Y);

}
