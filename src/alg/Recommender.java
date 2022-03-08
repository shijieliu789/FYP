package alg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import profile.Profile;
import util.ScoredThingDsc;
import util.reader.DatasetReader;

public abstract class Recommender implements RecAlg {
	
	protected DatasetReader reader;
	
	
	public Recommender(DatasetReader reader) {
		this.reader = reader;
	}
	
	
	public abstract Profile getRecommendationScores(Integer userId);
	
	
	/* Given a user profile and profile of scores, recommend
	 * a list of items, such that the items are ordered in decreasing
	 * order of score, but items already in the user's profile are
	 * not recommended.d
	 */
	
	public List<Integer> getRecommendationsFromScores(Profile userProfile, 
			Profile scores)
	{
		// create a list to store recommendations
		List<Integer> recs = new ArrayList<Integer>();
		
		if (scores==null)
			return recs;

		// store all scores in descending order in a sorted set
		SortedSet<ScoredThingDsc> ss = new TreeSet<ScoredThingDsc>(); 
		for(Integer id: scores.getIds()) {
			double s = scores.getValue(id);
			
			if (s>0)
				ss.add(new ScoredThingDsc(s, id));
		}
		
		// save all recommended items in descending order of similarity in the list
		// but leaving out items that are already in the user's profile
		for(Iterator<ScoredThingDsc> iter = ss.iterator(); iter.hasNext();)
		{
			ScoredThingDsc st = iter.next();
			Integer id = (Integer)st.thing;
			if (st.score > 0.0 && !userProfile.contains(id))
			{
				recs.add(id);
			}
				
		}
		
		return recs;

	}
}