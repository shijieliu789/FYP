/**
 * A class that implements the "nearest neighbourhood" technique (user-based CF).
 */

package neighbourhood;

import util.ScoredThingDsc;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import profile.Profile;
import similarity.*;
import util.Item;

public class NearestNeighbourhood extends Neighbourhood 
{
	private final int k; // the number of neighbours in the neighbourhood
	
	/**
	 * constructor - creates a new NearestNeighbourhood object
	 * @param k - the number of neighbours in the neighbourhood
	 */
	public NearestNeighbourhood(final int k)
	{
		super();
		this.k = k;
	}
	
	/**
	 * stores the neighbourhoods for all users in member Neighbour.neighbourhoodMap
	 * @param simMap - a map containing user-user similarities
	 */
	public void computeNeighbourhoods(final SimilarityMap<Profile> simMap)
	{
		for (Integer simId: simMap.getIds()) { // iterate over all ids
			SortedSet<ScoredThingDsc> ss = new TreeSet<ScoredThingDsc>(); // for the current id, store all similarities in order of descending similarity in a sorted set

			Profile profile = simMap.getSimilarities(simId); // get the similarity profile
			if (profile != null) {
				for (Integer id: profile.getIds()) { // iterate over each id in the profile
					double sim = profile.getValue(id);
					if (sim > 0)
						ss.add(new ScoredThingDsc(sim, id));
				}
			}
			
			// get the k most similar users (neighbours)
			int counter = 0;
			for (Iterator<ScoredThingDsc> iter = ss.iterator(); iter.hasNext() && counter < k; ) {
				ScoredThingDsc st = iter.next();
				Integer id = (Integer)st.thing;
				this.add(simId, id);
				counter++;
			}
		}	
	}
}
