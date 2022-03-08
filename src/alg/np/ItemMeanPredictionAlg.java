package alg.np;

import alg.RatingPredictionAlg;
import util.reader.DatasetReader;
import profile.Profile;
public class ItemMeanPredictionAlg implements RatingPredictionAlg {
	
	private DatasetReader reader;
	private Profile meanItemScores;
	
	public ItemMeanPredictionAlg(final DatasetReader reader)
	{
		this.reader = reader;
		this.meanItemScores = new Profile(0);
				
		for (Integer itemId : reader.getItemIds()) {
			Profile p = reader.getItemProfiles().get(itemId);
			meanItemScores.addValue(itemId, p.getMeanValue());			
		}
	}
	
	
	
	public Double getPrediction(final Integer userId, final Integer itemId)
	{
		return meanItemScores.getValue(itemId);
	}
}
