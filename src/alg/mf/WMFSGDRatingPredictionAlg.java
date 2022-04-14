package alg.mf;

import java.util.Random;
import java.util.Map;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;

import profile.Profile;
import util.reader.DatasetReader;

public class WMFSGDRatingPredictionAlg 
	extends MatrixFactorisationRatingPrediction	{

	
	private Map<Integer,Integer> invUserRow;
	private Map<Integer,Integer> invItemRow;
	private int[] pool;
	
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
		
		ntrans=0;
		for (Integer userId : reader.getUserIds() ) {
			Profile pu = reader.getUserProfiles().get(userId);
			for (Integer itemId : pu.getIds()) {		
				trainingData[ntrans] = 
						new TrainingTriple(userId,itemId,
								(1 + alpha*pu.getValue(itemId)));
				ntrans = ntrans+1;
			}
		}
		invUserRow = new HashMap<Integer,Integer>();
		invItemRow = new HashMap<Integer,Integer>();
		for (int u : userRow.keySet()) {
			invUserRow.put(userRow.get(u),u);
		}
		for (int i : itemRow.keySet()) {
			invItemRow.put(itemRow.get(i),i);
		}
		
		pool = new int[Q.length];
		for (int j=0;j<Q.length;j++) {
			pool[j]=j;
		}
		
		
	}

	protected void setDefaultHyperParams()
	{
		h = 1;
		alpha = 2.0;
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

		int reportfreq = numReports>0 ? 
				(int)Math.ceil(numberPasses*1.0/numReports):0;

		for (int iter=0;iter<numberPasses;iter++) {
			
//			System.out.println(
//					" Original Size "+trainingData.length);
			
			TrainingTriple[] augmentedTrainingData = 
					addNegativeSamples(trainingData,Q.length);
//			System.out.println("Augmented Size="+augmentedTrainingData.length);
			
			int ntrans = augmentedTrainingData.length;

			double L = 0.0;
			for (int s=ntrans;s>0;s--) {
				
				// Draw the next sample from the augmented training data
				int draw = numGen.nextInt(s);
				TrainingTriple sample = augmentedTrainingData[draw];
				augmentedTrainingData[draw] = augmentedTrainingData[s-1];
				augmentedTrainingData[s-1] = sample;
				
				
				
				// Extract userId, itemId, rating and confidence
				Integer userId = sample.user;
				Integer itemId = sample.item;
				Double cui = sample.rating;
				Double rui = (cui>1.0) ? 1.0 : 0.0;
				

				int u = userRow.get(userId);
				int i = itemRow.get(itemId);
				Double rhatui = getPrediction(userId,itemId);
				//System.out.printf("(%d,%d,%f,%f)\n",u,i,rui,cui);
				if (rui==Double.NaN || cui==Double.NaN)
					System.out.printf("(%d,%d,%f,%f)\n",u,i,rui,cui);
				

				L = L+cui*(rhatui-rui)*(rhatui-rui);

				for (int k=0;k<K;k++) {

					P[u][k] = P[u][k] 
							- learningRate *
							( cui*(rhatui - rui)*Q[i][k] + regWeightP*P[u][k]);
					Q[i][k] = Q[i][k] 
							- learningRate *
							( cui*(rhatui - rui)*P[u][k] + regWeightQ*Q[i][k]);

				}
				itemBias[i] = itemBias[i]
						-learningRate *
						(cui*(rhatui - rui) + regWeightItemBias*itemBias[i]);

				userBias[u] = userBias[u]
						-learningRate *
						(cui*(rhatui - rui) + regWeightUserBias*userBias[u]);

				globalBias = globalBias - learningRate*cui*(rhatui-rui);
			}
			if (reportfreq>0 && iter % reportfreq == 0)
				System.out.printf("Iter=%d \tRMSE=%f\n",iter,Math.sqrt(L/ntrans));
		}
		return;	
	}
	
	TrainingTriple[] addNegativeSamples(TrainingTriple[] trainingSet, int nitems) {
		int nusers=reader.getUserIds().size();
		int[] degu = new int[nusers];
		for (int u=0;u<nusers;u++)
			degu[u]=0;
		Map<Entry<Integer,Integer>, Double> dataLookUp= new HashMap<>();
		for (int i=0;i<trainingSet.length;i++) {
			Integer u=userRow.get(trainingSet[i].user);
			degu[u]++;
		}
		int ntrans=0;
		for (Integer u=0;u<nusers;u++)
			if (degu[u]*(1+h)>=nitems) {
				for (Integer i=0;i<nitems;i++) {
					dataLookUp.put(new SimpleEntry<>(u,i),1.0);
				}
				degu[u]= nitems;
				ntrans = ntrans+nitems;
			}


		for (int i=0;i<trainingSet.length;i++) {
			dataLookUp.put(new SimpleEntry<>(
					userRow.get(trainingSet[i].user),
					itemRow.get(trainingSet[i].item)), trainingSet[i].rating);	
			if (degu[userRow.get(trainingSet[i].user)]!=nitems)
				ntrans++;
		} 



		for (int j=0;j<trainingSet.length;j++) {
			Integer u = userRow.get(trainingSet[j].user);
			if (degu[u] != nitems) {
				for (int t = 0; t<h; t++) {

					Integer poolsize = nitems;
					Integer negi;
					Entry<Integer,Integer> negEntry;
					do  {
						Integer n = numGen.nextInt(poolsize);
						negi = pool[n];
						pool[n] = pool[poolsize-1];
						pool[poolsize-1] = negi;
						poolsize--;
						negEntry = new SimpleEntry<>(u,negi);

					} while (poolsize>0 && dataLookUp.get(negEntry) != null);
					if (dataLookUp.get(negEntry)== null) {
						dataLookUp.put(negEntry,1.0);
						ntrans++;
					}
				}
			}
		}
		TrainingTriple[] newTrainingData = new TrainingTriple[ntrans];
		int t = 0;
		for (Entry<Integer, Integer> key : dataLookUp.keySet()) {
			newTrainingData[t++] = new TrainingTriple(
					invUserRow.get(key.getKey()),
					invItemRow.get(key.getValue()),
					dataLookUp.get(key));		
		}
		return newTrainingData;

	}
}

