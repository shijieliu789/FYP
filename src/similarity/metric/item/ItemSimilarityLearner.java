package similarity.metric.item;

import java.util.Map;

import java.util.HashMap;


import profile.Profile;
import util.reader.DatasetReader;

import similarity.metric.SimilarityMetric;

import util.Item;


import org.apache.commons.math3.linear.*;


public abstract class ItemSimilarityLearner implements SimilarityMetric<Item> {
	protected SparseRealMatrix R;
	
	protected double[][] B;
	
	protected Map<Integer,Integer> userRow;
	protected Map<Integer,Integer> itemRow;
	
	protected DatasetReader reader;
	
	protected ItemSimilarityLearner(DatasetReader reader)
	{
		this.reader = reader;
		
		userRow = new HashMap<Integer, Integer>();
		itemRow = new HashMap<Integer, Integer>();
		
		int nitems = 0;
		for (Integer itemId : reader.getItemIds()) {
			itemRow.put(itemId, nitems);
			nitems++;
		}
		int nusers = 0;
		for (Integer userId : reader.getUserIds()) {
			userRow.put(userId, nusers);
			nusers++;
		}
		
		this.R = new OpenMapRealMatrix(nusers,nitems);
		
		for (Integer userId : reader.getUserIds() ) {
			
			Profile pu = reader.getUserProfiles().get(userId);
			
			for (Integer itemId : pu.getIds()) {
				
				// Get row of P and Q corresponding to userId, itemId
				// and rating corresponding to (userId,itemId)
				
				int u = userRow.get(userId);
				int i = itemRow.get(itemId);
				Double rui = pu.getValue(itemId);
				
				R.addToEntry(u, i, rui);
			}
		}		
	}
	abstract public void learnItemSimilarity();
	@Override
	public double getSimilarity(final Item X, final Item Y)
	{
		int i = itemRow.get(X.getId());
		int j = itemRow.get(Y.getId());
		return B[i][j];
	}

}
