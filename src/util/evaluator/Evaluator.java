/**
 * A class to evaluate a non-personalised recommender algorithm
 */

package util.evaluator;
import alg.RecAlg;
import profile.Profile;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import util.Item;
import util.reader.DatasetReader;
import java.util.Random;

public class Evaluator 
{		
	private RecAlg alg; //  recommender algorithm
	private DatasetReader reader; // dataset reader
	private int k; // the number of recommendations to be made
	private Integer[] userGroup; // users to be evaluated
	private int seed;  // random number seed for userGroup selection

	/**
	 * constructor - creates a new Evaluator object
	 * @param alg - the non-personalised recommender algorithm
	 * @param reader - dataset reader
	 * @param k - the number of recommendations to be made
	 */
	public Evaluator(final RecAlg alg, final DatasetReader reader, final int k,
			final Integer numUsers)
	{
		this.alg = alg;
		this.reader = reader;
		this.k = k;
		this.seed = 1234;
		selectUserGroup(numUsers);
		
		
	}
	public Evaluator(final RecAlg alg, final DatasetReader reader, final int k,
			final Integer numUsers, final int seed)
	{
		this.alg = alg;
		this.reader = reader;
		this.k = k;
		this.seed = seed;
		selectUserGroup(numUsers);
		
		
	}
	
	private void selectUserGroup(Integer numUsers) {
		
		List<Integer> userIds
			= new ArrayList<Integer>(reader.getUserIds());
		
		java.util.Collections.shuffle(userIds, new Random(seed));
		
		numUsers = Math.min(numUsers,userIds.size());
		
		userGroup = userIds.subList(0, numUsers).
				toArray(new Integer[numUsers]);
		
		
	}

	/**
	 * @return the percentage of items in the dataset which appear at 
	 * least once in the top-k recommendations made over all target users
	 */
	public double getRecommendationCoverage() {
		Set<Integer> allRecs = new HashSet<Integer>();
		int nitems = reader.getItems().size();
		for (Integer userId: userGroup) {
			List<Integer> recs = alg.getRecommendations(userId);
			for (int i = 0; i < recs.size() && i < k; i++)
				allRecs.add(recs.get(i));
		}
		
		return (nitems > 0) ? allRecs.size() * 1.0 / (nitems) : 0;
	}
	
	/**
	 * The percentage of items in the system
	 * which are capable of being recommended (i.e. those items which will be ranked 
	 * by the recsys algorithm).
	 * The value returned is the average percentage over all target users.
	 */
	public double getItemSpaceCoverage() {
		double meanCoverage = 0;
		int nrecs = 0;

		int nitems = reader.getItems().size();
		for (Integer userId: userGroup) {
			List<Integer> recs = alg.getRecommendations(userId);
			if (recs.size() > 0) {
				meanCoverage += (nitems > 0) ? recs.size() * 1.0 / nitems : 0;
				nrecs++;
			}
		}

		return (nrecs > 0) ? meanCoverage / nrecs : 0;
	}

	/**
	 * The popularity of a recommended movie is given by the percentage 
	 * of users in the system which have rated the movie.
	 * The popularity of the top-k recommendations 
	 * made is calculated by taking the average of the popularity over each 
	 * recommended movie. 
	 * The value returned is the average popularity over all target users.
	 */
	public double getRecommendationPopularity() {
		double meanPopularity = 0;
		int nrecs = 0;
		int nusers = reader.getUserIds().size();
		for (Integer userId: userGroup) {
			double popularity = 0;
			int counter = 0;
			List<Integer> recs = alg.getRecommendations(userId);
			for (int i = 0; i < recs.size() && i < k; i++) {
				popularity += reader.getItemProfiles().get(recs.get(i)).getSize() * 1.0 / nusers;
				counter++;
			}
			if (counter > 0) {
				meanPopularity += popularity / counter;
				nrecs++;
			}
		}

		return (nrecs > 0) ? meanPopularity / nrecs : 0;
	}

	/**
	 * The mean of 
	 * the ratings the item received in the training set.
	 * The relevance of the top-k recommendations 
	 * made is calculated by taking the average of the mean rating over each 
	 * recommended item. 
	 * The value returned is the average relevance over all target users.
	 */
	public double getRecommendationAverageRating() {
		double meanRelevance = 0;
		int nrecs = 0;

		for (Integer userId: userGroup) {
			double relevance = 0;
			int counter = 0;
			List<Integer> recs = alg.getRecommendations(userId);
			for (int i = 0; i < recs.size() && i < k; i++) {
				relevance += reader.getItemProfiles().get(recs.get(i)).getMeanValue();
				counter++;
			}

			if (counter > 0) {
				meanRelevance += relevance / counter;
				nrecs++;
			}
		}

		return (nrecs > 0) ? meanRelevance / nrecs : 0;
	}


	/**
	 * prints to standard output the top-k reommendations made for an item
	 * @param item - the target item
	 */
	public void printRecs(Integer userid) {
		Map<Integer,Item> items = reader.getItems();

		System.out.println("User: " + userid);
		List<Integer> recs = alg.getRecommendations(userid);
		for (int i = 0; i < recs.size() && i < k; i++)
			System.out.println("\tRec " + (i + 1) + ": " + items.get(recs.get(i)).getId()
					+" "+items.get(recs.get(i)).getName());
		System.out.println();
	}

	/**
	 * @param alg1
	 * @param alg2
	 * @return the number of common recommendations made by alg1 and alg2 (averaged over all users)
	 */
	public double getNumberCommonRecs(RecAlg alg1, RecAlg alg2) {
		int sumCommon = 0;

		for (Integer userId: reader.getUserIds()) {
			List<Integer> recs1 = alg1.getRecommendations(userId);
			List<Integer> recs2 = alg2.getRecommendations(userId);

			int count = 0;
			for (int i = 0; i < recs1.size() && i < k; i++)
				for (int j = 0; j < recs2.size() && j < k; j++)
					if (recs1.get(i) == recs2.get(j)) {
						count++;
						break;
					}

			sumCommon += count;
		}
		int nusers = reader.getUserIds().size();

		return (nusers>0) ? sumCommon * 1.0 / nusers : 0;
	}
	
	public double aggregratePerformance(TestPerfInterface perfunc) {
		Map<Integer,Profile> testProfileMap = reader.getTestProfileMap();	
		double perf = 0;
		int numUsers = 0;
		for (Integer userId: userGroup) {
			Profile p = testProfileMap.get(userId);
			List<Integer> recs = alg.getRecommendations(userId);
			
			if (p != null) {
				Double userperf = perfunc.testperf(userId,p,recs,k);
				if (userperf != null) {
					perf = perf + userperf;
					numUsers ++;
				}
			}
		}
		perf = (numUsers>0) ? perf/numUsers : 0.0;
		return perf;
	}
	
	public double[] aggregratePerformance(TestPerfInterface[] perfuncs) {
		Map<Integer,Profile> testProfileMap = reader.getTestProfileMap();	
		double[] perfs = new double[perfuncs.length];
		int[] numUsers = new int[perfuncs.length];
		for (int i=0;i<perfuncs.length;i++) {perfs[i]=0.0;numUsers[i]=0;};

		for (Integer userId: userGroup) {
			Profile p = testProfileMap.get(userId);
			List<Integer> recs = alg.getRecommendations(userId);
			for (int i=0;i<perfuncs.length;i++) {

				if (p != null) {
					Double userperf = perfuncs[i].testperf(userId,p,recs,k);
					if (userperf != null) {
						perfs[i] = perfs[i] + userperf;
						numUsers[i] ++;
					}
				}
			}
		}
		for (int i=0;i<perfuncs.length;i++) 
			perfs[i] = (numUsers[i]>0) ? perfs[i]/numUsers[i] : 0.0;
		return perfs;
	}
	


}
