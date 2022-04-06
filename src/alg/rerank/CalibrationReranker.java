package alg.rerank;

import alg.np.BrowsedItemRecommender;
import profile.Profile;
import java.util.*;
import static java.util.stream.Collectors.*;
import java.util.stream.Collectors;

import util.Item;
import util.reader.DatasetReader;

// formula 6

public class CalibrationReranker implements Reranker
{
//    Map<Integer, Double> userCalibrationScoreMap;
    double lambda;
    DatasetReader reader;
    Map<Integer, Map<String, Double> > pi = new HashMap<>();




    // when instantiated it should generate the calibration scores and store them.
    public CalibrationReranker(DatasetReader reader, double lambda)
    {
//        this.distanceMap = distanceMap;
//        this.userCalibrationScoreMap = CalibrationExample.getCalibrationMap(reader);
        this.lambda = lambda;
        this.reader = reader;
    }

    // !!!
    public List<Integer> rerank(Profile userProfile,Profile scores)
    {

		// create a list to store recommendations


		// sorts scores map and takes first 10 values

		Map<Integer, Double> scoresMap = scores.getDataMap().entrySet()
				.stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect( toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
						LinkedHashMap::new));
		List<Integer> recItems =
				new ArrayList<>(scoresMap.keySet().stream().limit(20).collect(Collectors.toList()));

		List<Integer> rerankedList = new ArrayList<Integer>();

        rerankedList.add(recItems.get(0));   // adds top recommended item to new list.
        recItems.remove(0);

		double[] ckl=new double[1];
		ckl[0] = getCKL(userProfile.getId(), rerankedList, recItems.get(0));


        while(rerankedList.size() < 5 && recItems.size() > 0) {
            Integer bestRerankItemId = findHighestScoringItem(userProfile, rerankedList, recItems, scores,ckl);
            if (!userProfile.contains(bestRerankItemId)){
//                System.out.println("Highest scoring Id this round: " + bestRerankItemId);
                rerankedList.add(bestRerankItemId);
            }
            recItems.remove(bestRerankItemId);
        }

//        for (Integer i : rerankedList) {
//        	System.out.println("Rerank "+i);
//        }

		return rerankedList;
	}

    // Updates the rerankedList with a new item each call.
    private Integer findHighestScoringItem(Profile userProfile,
    		List<Integer> rerankedList, List<Integer> recItems, Profile scores,double[] ckl) {
        int highestId=-1;
        Double maxrankedScore = Double.NEGATIVE_INFINITY;
        double maxckl = 0.0;

		ckl[0] = getCKL(userProfile.getId(), rerankedList, recItems.get(0));
//		System.out.println("initial CKL for round " + rerankedList.size() + " : " + ckl[0] );

        // For greedy method, you only need to compute the change in the
        // objective when adding an item
        // keep a track of the previous CKL in order to find by how much
        // the ckl term changes each time an item is added

		// Also, only need to get the maximum value - no need to sort

        for (int i = 0; i < recItems.size(); i++) {
            double sI = 0, rerankedScore = 0;
            sI = scores.getValue(recItems.get(i));
            double newckl = getCKL(userProfile.getId(), rerankedList, recItems.get(i));
            double ckldiff = newckl - ckl[0];
            rerankedScore = lambda*sI - (1-lambda)* (ckldiff);

			if (rerankedScore > maxrankedScore) {
				highestId = recItems.get(i);
				maxrankedScore = rerankedScore;
				maxckl = newckl;
			}
        }
        ckl[0]=maxckl;
        return highestId;
    }

	// associated with each item in the system, map<itemId, map<genre, probability> >
	public Map<Integer, Map<String, Double>> getItemGenreProbabilityMap(DatasetReader reader, Map<Integer, Map<String, Double>> pi) {
		for (Integer itemId : reader.getItemIds()){		// loops over all movies

			int numGenres = reader.getItem(itemId).getGenres().size();

			Map<String, Double> gMap;	// gMap contains the genre of movie i and its probability given i
			if (pi.containsKey(itemId)) { //if item exists in outer map, assign its inner map to gMap.
				gMap = pi.get(itemId);
			}
			else {	//else declare a new genreMap for a new item.
				gMap = new HashMap<String, Double>();
				pi.put(itemId, gMap);
			}

			for (String genre : reader.getItem(itemId).getGenres()) { //fill genreMap
				gMap.put(genre, 1.0/numGenres);
			}
		}
		return pi;
	}

//	 Get the KL score for each subset combination of items.
	private double getCKL(Integer userId, List<Integer> rerankedList, Integer itemId) {
		pi = getItemGenreProbabilityMap(reader, pi);
		Map<String,Double> pu_g = getPUMap(reader, pi).get(userId);

		rerankedList.add(itemId);
		Map<String,Double> qu_g = updateUserRecommendedGenreMap(rerankedList, pi);

		// 	 Calibration calculation
		Double klScore = 0.0;
		double p, q;

		for (String genre : getGenres(reader)) {
			p = pu_g.getOrDefault(genre, 0.0);
			q = qu_g.getOrDefault(genre, 0.0);
			if (q == 0) {
				q = deltaQ2(q, p);
			}
			klScore += (p * (Math.log(p/q)) > 0 || p * (Math.log(p/q)) < 0) ? (p * (Math.log(p/q))) : 0;
		}
		rerankedList.remove(itemId);
		return klScore;
	}


	// Gets probabilities p(g|u) of movies played by each user in the past.
	private Map<Integer, Map<String, Double>> getPUMap(DatasetReader reader, Map<Integer, Map<String, Double>> pi) {
		Map<Integer, Map<String, Double>> pu = new HashMap<>();

		for (int userId : reader.getUserIds()) {
			// get the user's profile - i.e. set of items that user has interacted with
			// reader.getUserProfiles() returns userProfileMap<Integer, Profile>
			// profile consists of {ID, dataMap <itemID, value> }
			Profile uProf = reader.getUserProfiles().get(userId);

			// set p(g|u) to the sum of p(g|i) over all items that the user
			// has interacted with, divided by the total number of items store result in usergenreMap;

			Map<String, Double> usergenreMap = new HashMap<>(); 	// stores p(g|u) of each user
			updateUserInteractedGenreMap(uProf, pi, usergenreMap);
			// user , <genre of played items, probability of genre across played items>
			pu.put(userId, usergenreMap);
		}

		return pu;
	}

	private Map<Integer, Map<String, Double>> getQUMap(DatasetReader reader, Map<Integer, Map<String, Double>> pi, BrowsedItemRecommender alg) {
		Map<Integer, Map<String, Double>> qu = new HashMap<>();
		for (int userId : reader.getUserIds()) {
			// Run the recommendation algorithm to get the items recommended to the user
			List<Integer> recItems = alg.getRecommendations(userId);
			Map<String, Double> userRecGenreMap = updateUserRecommendedGenreMap(recItems, pi);
			qu.put(userId, userRecGenreMap);
		}
		return qu;
	}

	private void updateUserInteractedGenreMap(Profile uProf, Map<Integer, Map<String, Double> > pi, Map<String, Double> usergenreMap) {
		// Loop over the items that user has interacted with
		// .getIds() returns the dataMap.keySet() -- all itemIds

		Integer uProfLength = uProf.getSize();

		for (Integer itemId : uProf.getIds()) {

			if (pi.containsKey(itemId)) {		//map of items

				// genres of item and its relative probability
				Map<String, Double> itemGenreProbabilityMap = pi.get(itemId);

				for (String genre : itemGenreProbabilityMap.keySet()) {
					Double itemVal = itemGenreProbabilityMap.get(genre);

					Double v = 0.0;
					if (usergenreMap.containsKey(genre)) //if user has a probability of particular genre
						v = usergenreMap.get(genre); //take pre-existing probability

					usergenreMap.put(genre, v+itemVal/uProfLength); //update genre's new probability.
				}
			}
		}
	}

	private Map<String, Double> updateUserRecommendedGenreMap(List<Integer> recItems, Map<Integer, Map<String, Double> > pi) {
		Map<String, Double> userRecGenreMap = new HashMap<>();
		int uProfLength = recItems.size();

		// Loop over recommended items
		for (Integer itemId : recItems) {
			if (pi.containsKey(itemId)) {

				// genres of item and its relative probability
				Map<String, Double> itemGenreProbabilityMap = pi.get(itemId);

				for (String genre : itemGenreProbabilityMap.keySet()) {
					Double genreProbability = itemGenreProbabilityMap.get(genre);

					Double v = 0.0;
					if (userRecGenreMap.containsKey(genre))
						v = userRecGenreMap.get(genre);

					userRecGenreMap.put(genre, v + genreProbability/uProfLength);
				}
			}
		}
		return userRecGenreMap;
	}


	// By right this should be a method attached to the DatasetReader
	// class - implemented as a static here simply to avoid messing with
	// the DatasetReader class

	public static Set<String> getGenres(DatasetReader reader)
	{
		Set<String> s = new HashSet<>();
		for (Integer itemId : reader.getItemIds()) {

			Item item = reader.getItem(itemId);

			for (String g : item.getGenres()) {
				s.add(g);
			}
		}
		return s;
	}

	private double deltaQ2(double qgu, double pgu) {
		double deltaq;
		double alpha = 0.01;
		deltaq = (1 - alpha) * qgu + alpha * pgu;

		return deltaq;
	}
}

