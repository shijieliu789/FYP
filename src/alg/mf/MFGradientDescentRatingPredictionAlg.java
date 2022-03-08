package alg.mf;
import java.util.Map;
import java.util.Random;
import profile.Profile;

import util.reader.DatasetReader;

public class MFGradientDescentRatingPredictionAlg 

		extends MatrixFactorisationRatingPrediction	{

	private Random numGen ;
	
	
	public MFGradientDescentRatingPredictionAlg(DatasetReader reader, int k)
	{
		super(reader,k);
		numGen = new Random();
	}
	
	protected void setDefaultHyperParams()
	{
		learningRate = 0.01;
		numberPasses = 200;
		regWeightP = 0.5;
		regWeightQ = 0.5;
		regWeightItemBias = 0.5;
		regWeightUserBias = 0.5;
		numReports = 100;
	}
	
	
	private void initialise(Double[][] Mat)
	{
		for (int i=0; i<Mat.length;i++)	
			for (int j=0; j<Mat[0].length;j++) {
				Mat[i][j] = numGen.nextDouble();	
			}
	}
	private void initialise(Double[] Vec)
	{
		for (int i=0; i<Vec.length;i++)	{
			Vec[i] =  numGen.nextDouble();
		}
	}
	
			
	
	
	public void fit()
	{   
		int nitems = Q.length;
		int nusers = P.length;
		
		// Variables to store updated values of parameters
		Double [][] Pnew = new Double[nusers][K];
		Double [][] Qnew = new Double[nitems][K];
		Double [] itemBiasnew = new Double[nitems];
		Double [] userBiasnew = new Double[nitems];
		Double globalBiasnew = 0.0;
		
		// degu[u] = size of user profile
		// degi[i] = size of item profile
		Integer [] degu = new Integer[nusers];
		Integer [] degi = new Integer[nitems];
		
		
		initialise(P);
		initialise(Q);
		initialise(itemBias);
		initialise(userBias);
		globalBias = numGen.nextDouble();
		
		int reportfreq = numReports>0 ? 
				(int)Math.ceil(numberPasses*1.0/numReports):0;
		Double ntrans = 0.0;
		for (Integer userId : reader.getUserIds() ) {
			int u = userRow.get(userId);
			Profile pu = reader.getUserProfiles().get(userId);
			degu[u] = pu.getIds().size();
			ntrans += pu.getIds().size();
		}
		

		for (Integer itemId : reader.getItemIds() ) {
			int i = itemRow.get(itemId);
			Profile iu = reader.getItemProfiles().get(itemId);
			degi[i] = iu.getIds().size();
		}
		
		// initialise Pnew, Qnew, itemBiasnew, userBiasnew, globalBiasnew to
		// P, Q, itemBias, userBias, globalBias
		for (int u=0;u<nusers;u++) {
			for (int k=0;k<K;k++) {
				Pnew[u][k] = P[u][k];
			}
			userBiasnew[u] = userBias[u];
		}
		for (int i=0;i<nitems;i++) {
			for (int k=0;k<K;k++) {
				Qnew[i][k] = Q[i][k];
			}
			itemBiasnew[i] = itemBias[i];
		}
		globalBiasnew = globalBias;
		
		
		for (int iter=0;iter<numberPasses;iter++) {
			
			double L = 0.0;
			
			
			for (Integer userId : reader.getUserIds() ) {
			
				Profile pu = reader.getUserProfiles().get(userId);
				
				for (Integer itemId : pu.getIds()) {
					
					// Get row of P and Q corresponding to userId, itemId
					// and rating corresponding to (userId,itemId)
					
					int u = userRow.get(userId);
					int i = itemRow.get(itemId);
					Double rui = pu.getValue(itemId);	
					
					// compute the predicted rating and update the
					// loss L as we proceed
					Double rhatui = getPrediction(userId,itemId);
					
					L = L+(rhatui-rui)*(rhatui-rui);

					// Compute Pnew, Qnew, itemBiasnew,userBiasnew, globalBiasnew
					// by applying full gradient to P,Q,itemBias,userBias,globalBias
					for (int k=0;k<K;k++) {
					
						Pnew[u][k] = Pnew[u][k] 
								- learningRate/degu[u] *
								( (rhatui - rui)*Q[i][k] + regWeightP*P[u][k]);

						
						Qnew[i][k] = Qnew[i][k] 
								- learningRate/degi[i] *
								( (rhatui - rui)*P[u][k] + regWeightQ*Q[i][k]);
						
					}
					itemBiasnew[i] = itemBiasnew[i]
						-learningRate/degi[i] *
						((rhatui - rui) + regWeightItemBias*itemBias[i]);
					
					userBiasnew[u] = userBiasnew[u]
							-learningRate/degu[u] *
							((rhatui - rui) + regWeightUserBias*userBias[u]);
					
					globalBiasnew = globalBiasnew
							- learningRate/(degi[i]*degu[u])*(rhatui-rui);
					
				}
				
			}
			if (reportfreq>0 && iter % reportfreq == 0)
				System.out.printf("Iter=%d \tRMSE=%f\n",iter,Math.sqrt(L/ntrans));
			
			// Copy Pnew, Qnew, itemBiasnew, userBiasnew, globalBiasnew to
			// P, Q, itemBias, userBias, globalBias
			
			for (int u=0;u<nusers;u++) {
				for (int k=0;k<K;k++) {				
					P[u][k] = Pnew[u][k];
				}
				userBias[u] = userBiasnew[u];
			}			
			for (int i=0;i<nitems;i++) {
				for (int k=0;k<K;k++) {
					Q[i][k] = Qnew[i][k];
				}
				itemBias[i] = itemBiasnew[i];
			}
			globalBias = globalBiasnew;
			
		}
		return;
	}

}
