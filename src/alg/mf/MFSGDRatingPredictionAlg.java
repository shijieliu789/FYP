package alg.mf;

import java.util.Random;

import profile.Profile;
import util.reader.DatasetReader;

public class MFSGDRatingPredictionAlg 
	extends MatrixFactorisationRatingPrediction	{

	
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



	public MFSGDRatingPredictionAlg(DatasetReader reader, int k)
	{
		super(reader,k);
		numGen = new Random();
		setDefaultHyperParams();
		int ntrans = 0;
		for (Integer userId : reader.getUserIds() ) {
			Profile pu = reader.getUserProfiles().get(userId);
			ntrans += pu.getSize();
		}
		// Construct the training data array
		
		trainingData = new TrainingTriple[ntrans];
		
		ntrans=0;
		for (Integer userId : reader.getUserIds() ) {

			Profile pu = reader.getUserProfiles().get(userId);

			for (Integer itemId : pu.getIds()) {
				
				trainingData[ntrans] = 
						new TrainingTriple(userId,itemId, pu.getValue(itemId));
				ntrans = ntrans+1;
			}
			
		}	
	}

	protected void setDefaultHyperParams()
	{
		learningRate = 0.01;
		numberPasses = 200;
		regWeightP = 0.5;
		regWeightQ = 0.5;
		regWeightItemBias = 0.5;
		regWeightUserBias = 0.5;
		numReports = 10;
	}


	// used to initialise the parameter matrices P and Q
	private void initialise(Double[][] Mat)
	{
		for (int i=0; i<Mat.length;i++)	
			for (int j=0; j<Mat[0].length;j++) {
				Mat[i][j] = numGen.nextDouble()/Math.sqrt(K);	
			}
	}
	// used to initialise the parameter vectors userBias and itemBias
	private void initialise(Double[] Vec)
	{
		for (int i=0; i<Vec.length;i++)	{
			Vec[i] =  numGen.nextDouble()/Math.sqrt(K);
		}
	}

	public void fit()
	{   
		// ONLY INITIALISED HERE - FIT ALGORITHM NEEDS TO BE COMPLETED
		initialise(P);
		initialise(Q);
		initialise(itemBias);
		initialise(userBias);
		globalBias = numGen.nextDouble();

	}
}

