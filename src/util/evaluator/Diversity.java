package util.evaluator;

import java.util.List;

import profile.Profile;
import similarity.SimilarityMap;

public class Diversity implements TestPerfInterface {
	private SimilarityMap distanceMap;
	
	public Diversity(SimilarityMap distanceMap)
	{
		this.distanceMap = distanceMap;
		
	}
	
	
	public Double testperf(Integer userId, Profile testProfile, 
			List<Integer> recs, Integer k) {
		
		Integer numRecItems = 0;
		Double diversity = 0.0;
		int i = 0;
		for (Integer itemId1 : recs) {
			
			int nrecs = 0;
			for (Integer itemId2 : recs) {
				if (itemId2 != itemId1) {
					double d = distanceMap.getSimilarity(itemId1, itemId2);
					diversity += d ;
					i++;
				}
				nrecs++;
				if (nrecs==k)
					break;
			}
				
			numRecItems ++;
			if (numRecItems == k)
				break;
		}
		diversity = diversity/(k*(k-1));
		
		
		return diversity;
	}
	
	
}
