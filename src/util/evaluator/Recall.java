package util.evaluator;

import java.util.List;

import profile.Profile;

public class Recall implements TestPerfInterface {
	private Double threshold;
	public Recall(Double threshold)
	{
		this.threshold = threshold;
	}
	public Recall()
	{
		this.threshold = 1.0;
	}
	
	public Double testperf(Integer userId, Profile testProfile, 
			List<Integer> recs, Integer k) {
		Double recall = 0.0;
		Integer numTestItems = 0;
		
		for (Integer testItem : testProfile.getIds()) {
			Double val = testProfile.getValue(testItem);
			if (val>=threshold)
				numTestItems++;
		}
		// Indicate that no test items were available by returning null
		if (numTestItems==0)
			return null;
		
		Integer numRecItems=0;
		for (Integer itemId : recs) {
			Double val = testProfile.getValue(itemId);
			if (val != null) {
				if (val>= threshold)
					recall = recall+1.0;	
					
			}
			
			numRecItems ++;
			if (numRecItems == k)
				break;
		}
		recall = recall/numTestItems;
		
		return recall;
	}
	
}
