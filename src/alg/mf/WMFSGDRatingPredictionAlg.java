package alg.mf;

import java.util.Random;


import profile.Profile;
import util.reader.DatasetReader;

public class WMFSGDRatingPredictionAlg 
	extends MatrixFactorisationRatingPrediction	{

	
	// parameter for confidence value
	private Double alpha;
	// negative sampling rate parameter
	private Integer h;
	
	private Random numGen ;
	
	// The trainingData array of private class TrainingTriple allows for access to
	// the training data in a convenient way. For SGD, it is necessary to access
	// the training data in a random order. Hence the training data is placed in 
	// an array, which can then be accessed at random
	private TrainingTriple[] trainingData;
	private class TrainingTriple {

	    public final Integer user;
	    public final Integer item;
	    public final Double rating;
	    

	    public TrainingTriple(Integer user, Integer item, Double rating) {
	        this.user = user;
	        this.item = item;
	        this.rating = rating;
	    }

	}



	public WMFSGDRatingPredictionAlg(DatasetReader reader, int k)
	{
		super(reader,k);
		numGen = new Random();
		setDefaultHyperParams();
		int ntrans = 0;
		for (Integer userId : reader.getUserIds() ) {

			Profile pu = reader.getUserProfiles().get(userId);
			ntrans += pu.getSize();
		}
		trainingData = new TrainingTriple[ntrans];
		
		// For convenience, put the confidence value into the rating
		// field of the TrainingTriple. 
		ntrans=0;
		for (Integer userId : reader.getUserIds() ) {
			Profile pu = reader.getUserProfiles().get(userId);
			for (Integer itemId : pu.getIds()) {
				// confidence = 1 + alpha*rui, where rui is the rating
				trainingData[ntrans] = 
						new TrainingTriple(userId,itemId,
								(1 + alpha*pu.getValue(itemId)));
				ntrans = ntrans+1;
			}
		}
		
		
	}

	protected void setDefaultHyperParams()
	{
		h = 1;
		alpha = 2.0;
		// The learning rate needs to be small for WMF - around the value shown here
		learningRate = 0.0001;
		numberPasses = 100;
		regWeightP = 0.5;
		regWeightQ = 0.5;
		regWeightItemBias = 0.5;
		regWeightUserBias = 0.5;
		numReports = 100;
	}
	// functions to set hyperparameters associated with WMF
	public void setNegativeSamplingRate(Integer h) {
		this.h = h;
	}
	public void setAlpha(Double alpha) {
		this.alpha = alpha;
	}	

	private void initialise(Double[][] Mat)
	{
		for (int i=0; i<Mat.length;i++)	
			for (int j=0; j<Mat[0].length;j++) {
				Mat[i][j] = numGen.nextDouble()/Math.sqrt(K);	
			}
	}
	private void initialise(Double[] Vec)
	{
		for (int i=0; i<Vec.length;i++)	{
			Vec[i] =  numGen.nextDouble()/Math.sqrt(K);
		}
	}


	public void fit()
	{   
		initialise(P);
		initialise(Q);
		initialise(itemBias);
		initialise(userBias);
		globalBias = numGen.nextDouble();

		// How often to report the RMSE on the console as the optimisation
		// proceeds
		int reportfreq = (numReports>0)? (int)Math.ceil(numberPasses*1.0/numReports):0;

		for (int iter=0;iter<numberPasses;iter++) {
			
			TrainingTriple[] augmentedTrainingData = 
					addNegativeSamples(trainingData,Q.length);
			//System.out.println("Augmented Size="+augmentedTrainingData.length+
			//		" Original Size "+trainingData.length);
			
			int ntrans = augmentedTrainingData.length;

			double L = 0.0;
			for (int s=ntrans;s>0;s--) {
				
				// Draw the next sample from the augmented training data
				int draw = numGen.nextInt(s);
				TrainingTriple sample = augmentedTrainingData[draw];
				augmentedTrainingData[draw] = augmentedTrainingData[s-1];
				augmentedTrainingData[s-1] = sample;
				
				
				
				// Extract userId, itemId
				Integer userId = sample.user;
				Integer itemId = sample.item;
				
				
				// The code below  NEEDS TO CHANGE
				// Currently it is written for the standard SGD MF algorithm
				// For WMF, we need to
				// Change the updates to take the confidence term into account.
				// Note, from the constructor, we have put the confidence cui in 
				// sample.rating, rather than rui. 
				
				
				Double rui = sample.rating;
				

				int u = userRow.get(userId);
				int i = itemRow.get(itemId);
				Double rhatui = getPrediction(userId,itemId);

				L = L+(rhatui-rui)*(rhatui-rui);

				for (int k=0;k<K;k++) {

					P[u][k] = P[u][k] 
							- learningRate *
							( (rhatui - rui)*Q[i][k] + regWeightP*P[u][k]);
					Q[i][k] = Q[i][k] 
							- learningRate *
							( (rhatui - rui)*P[u][k] + regWeightQ*Q[i][k]);

				}
				itemBias[i] = itemBias[i]
						-learningRate *
						((rhatui - rui) + regWeightItemBias*itemBias[i]);

				userBias[u] = userBias[u]
						-learningRate *
						((rhatui - rui) + regWeightUserBias*userBias[u]);

				globalBias = globalBias - learningRate*(rhatui-rui);
			}
			if (reportfreq>0 && iter % reportfreq == 0)
				System.out.printf("Iter=%d \tRMSE=%f\n",iter,Math.sqrt(L/ntrans));
		}
		return;	
	}
	
	TrainingTriple[] addNegativeSamples(TrainingTriple[] trainingSet, int nitems) {
		// COMPLETE THIS FUNCTION - 
		// It should return the training set augmented so that for every rating
		// in the original training set, h negative samples are added
		
		// That is, for each (userId,itemId) pair for which a positive rating
		// exists in the trainingSet, select, at random, h new (userId,negId) pairs, and set 
		// their rating value to 0.  The pair (userId,negId) should not be 
		// already in the training set.
		
		// Note, that for some users, their profiles will be too full to add h extra
		// negative items per positive item.  Hence the new training set will not be 
		// exactly (h+1) times larger than the old one
		
		// return the augmented training set
		return trainingSet;
	}
	
}

