package alg.mf;

import java.util.Map;
import java.util.Random;
import java.util.HashMap;

import alg.RatingPredictionAlg;
import util.reader.DatasetReader;

public abstract class MatrixFactorisationRatingPrediction implements RatingPredictionAlg, ModelBasedAlg {
	protected Double[][] P;
	protected Double[][] Q;
	protected Double[] itemBias;
	protected Double[] userBias;
	protected Map<Integer,Integer> userRow;
	protected Map<Integer,Integer> itemRow;
	protected Double globalBias;
	protected int K;
	protected DatasetReader reader;
	
	// Hyperparameters of MF algorithm
	protected Double learningRate;
	protected Integer numberPasses;
	protected Double regWeightP;
	protected Double regWeightQ;
	protected Double regWeightItemBias;
	protected Double regWeightUserBias;
	protected Integer numReports;
	
	
	
	
	
	MatrixFactorisationRatingPrediction(DatasetReader reader, Integer k)
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
		globalBias = 0.0;
		
		
		setLatentSpaceDim(k);
		setDefaultHyperParams();
		
	}
	// Set the Hyperparameters of the algorithm
	
	public void setLatentSpaceDim(int dim) {
		this.K = dim;
		int nitems = reader.getItemIds().size();
		int nusers = reader.getUserIds().size();
		
		P = new Double[nusers][dim];
		Q = new Double[nitems][dim];
		itemBias = new Double[nitems];
		userBias = new Double[nusers];
	}
	public void setLearningRate(Double learningRate) {
		this.learningRate = learningRate;
	}
	public void setNumberPasses(int numberPasses) {
		this.numberPasses = numberPasses;
	}
	public void setRegularisationWeights(Double weight) {
		this.regWeightP = weight;
		this.regWeightQ = weight;
		this.regWeightItemBias = weight;
		this.regWeightUserBias = weight;
	}
	public void setRegWeightP(Double weight) {
		this.regWeightP = weight;
	}
	public void setRegWeightQ(Double weight) {
		this.regWeightQ = weight;
	}
	public void setRegWeightItemBias(Double weight) {
		this.regWeightItemBias = weight;
	}
	public void setRegWeightUserBias(Double weight) {
		this.regWeightUserBias = weight;
	}
	public void setNumReports(int numReports)
	{
		this.numReports = numReports;
	}

	abstract public void fit();
	abstract protected void setDefaultHyperParams();
	
	
	public Double getPrediction(final Integer userId, final Integer itemId)
	{
		Integer user = userRow.get(userId);
		Integer item = itemRow.get(itemId);

		
		Double rhat =itemBias[item] + userBias[user] + globalBias;
		for (int j=0;j<K;j++) {
			rhat += P[user][j]*Q[item][j];
		}
		
		return rhat;
	}
}
