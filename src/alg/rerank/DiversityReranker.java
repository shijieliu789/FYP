package alg.rerank;

import profile.Profile;
import similarity.SimilarityMap;
import util.ScoredThingDsc;

import java.util.List;

import java.util.ArrayList;
import java.util.Iterator;

import java.util.SortedSet;
import java.util.TreeSet;

import util.Item;

// formula 6

public class DiversityReranker implements Reranker
{
	SimilarityMap<Item> distanceMap;
	
	double lambda;
	// dont need distance map
	public DiversityReranker(SimilarityMap<Item> distanceMap, double lambda)
	{
		this.distanceMap = distanceMap;
		this.lambda = lambda;
	}
	
	// !!!
	public List<Integer> rerank(Profile userProfile,Profile scores)
	{
		
		// IMPLEMENT THE DIVERSIFICATION METHOD THAT 
		// re-ranks a set of items to maximise a tradeoff 
		// between accuracy and diversity as given by the parameter
		// lambda
		
		// BELOW IS THE CODE USED IN RECOMMENDER.JAVA to 
		// return a list of items according to their. This code is
		// added here just to give you a working starting point.
		
		// You should modify/replace this code with a code to 
		// that chooses the list according to the accuracy/diversity
		// tradeoff

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
