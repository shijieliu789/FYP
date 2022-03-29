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

        //get rid of callong baseline recommender.
        //generate recitems list using scores profile.
        // iterate through map and sort by value.

        // Profile userProfile: user interaction profile
        // ( userId, Map <recommendedMovieId, interactionScoreFromInputFile> )

        // Profile scores: profile of scores for each item
        // (0, Map <recommendedMovieId, recommendationScore>)

        // IMPLEMENT THE DIVERSIFICATION METHOD THAT
        // re-ranks a set of items to maximise a tradeoff
        // between accuracy and diversity as given by the parameter
        // lambda

        // BELOW IS THE CODE USED IN RECOMMENDER.JAVA to
        // return a list of items according to their. This code is
        // added here just to give you a working starting point.

        // You should modify/replace this code with a code to
        // that chooses the list according to the accuracy/diversity
        // tradeoff

        // create a list to store recommendations

        //.entrySet().stream()
        //            .sorted((o1, o2) -> o2.getValue() - o1.getValue())
        //            .limit(10)
        //            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

        // sorts scores map and takes first 10 values

        Map<Integer, Double> scoresMap = scores.getDataMap().entrySet()
              .stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
              .collect( toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                            LinkedHashMap::new));
        List<Integer> recItems = new ArrayList<>(scoresMap.keySet().stream().limit(10).collect(Collectors.toList()));
//        System.out.println("Size : " + recItems.size());
//        for (int i=0; i<5; i++) {
//            System.out.println("Item " + i + "has ID: " + recItems.get(i) + "has score: " + scoresMap.get(recItems.get(i)));
//        }

        List<Integer> rerankedList = new ArrayList<Integer>();
        rerankedList.add(recItems.get(0));   // adds top recommended item to new list.
        recItems.remove(0);

        while(rerankedList.size() < 5 && recItems.size() > 0) {
            Integer bestRerankItemId = findHighestScoringItem(userProfile, rerankedList, recItems, scores);
            if (!userProfile.contains(bestRerankItemId)){
                System.out.println("Id: " + bestRerankItemId);
                rerankedList.add(bestRerankItemId);
                recItems.remove(bestRerankItemId);
                System.out.println("Reranked List size: " + rerankedList.size() + " Original List size: " + recItems.size());
            }
            recItems.remove(bestRerankItemId);
        }

//        if (scores==null)
//            return rerankedList;
// store all scores in descending order in a sorted set
//        double recScoreSum = 0;
//        SortedSet<ScoredThingDsc> ss = new TreeSet<ScoredThingDsc>();
//        for(Integer id: scores.getIds()) {
//            double s = scores.getValue(id);
//            if (s>0) {
//                ss.add(new ScoredThingDsc(s, id));
//            }
//        }


        // save all recommended items in descending order of similarity in the list
        // but leaving out items that are already in the user's profile
//        for(Iterator<ScoredThingDsc> iter = ss.iterator(); iter.hasNext();)
//        {
//            ScoredThingDsc st = iter.next();
//            Integer id = (Integer)st.thing;
//            if (st.score > 0.0 && !userProfile.contains(id))
//            {
//                rerankedList.add(id);
//            }
//
//        }

        return rerankedList;
    }

    // Updates the rerankedList with a new item each call.
    private Integer findHighestScoringItem(Profile userProfile, List<Integer> rerankedList, List<Integer> recItems, Profile scores) {
        Map<Integer, Double> itemWithNewScoreMap = new TreeMap<>();     // treemap helps with sorting key value pairs in ascending order.
        for (int i = 0; i < recItems.size(); i++) {
            double sI = 0, rerankedScore = 0;
            for (int j = 0; j < rerankedList.size(); j++) {
                sI += scores.getValue(rerankedList.get(j));     // calculates s(I) scores of movies already in reranked list.
            }
            sI += scores.getValue(recItems.get(i));
            // NEED TO CALCULATE CL SCORES FOR DIFF COMBINATIONS OF items already in re-ranked list + each other item.
//            System.out.println("Passing item : " + recItems.get(i));
            rerankedScore = lambda*sI - lambda * (getCKL(userProfile.getId(), rerankedList, recItems.get(i)));
            itemWithNewScoreMap.put(recItems.get(i), rerankedScore);
        }

        int highestId = itemWithNewScoreMap.keySet().stream().findFirst().get();
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
				double pgu = (p == 0) ? 0 : pu_g.get(genre);
				q = deltaQ2(pgu);
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
	
	private double deltaQ2(double pgu) {
		double deltaq;
		double alpha = 0.01;
		deltaq = alpha * pgu;

		return deltaq;
	}
}


