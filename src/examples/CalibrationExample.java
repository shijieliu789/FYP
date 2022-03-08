package examples;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import alg.np.BrowsedItemRecommender;
import util.reader.DatasetReader;
import profile.Profile;
import similarity.SimilarityMap;
import similarity.metric.SimilarityMetric;
import similarity.metric.item.GenreMetric;
import util.Item;

public class CalibrationExample {

	public static void main(String[] args)
	{		
		// set the path of the dataset
		String folder = "datasets";
		String dataset = folder + File.separator + "ml20m";
		// set the path and filename of the output file ...

		DatasetReader reader = new DatasetReader(dataset);

		getCalibrationMap(reader);
	}

	public static Map<Integer, Double> getCalibrationMap(DatasetReader reader) {
		////////////////////////////////////////////////////////
		long seed = 0; // seed for random number generator
		int k = 10; // the number of recommendations to be made for each target user
		int nusers = 10; // number of users to evaluate
		////////////////////////////////////////////////////////
		SimilarityMetric<Item> metric = new GenreMetric();
		SimilarityMap<Item> simMap = new SimilarityMap<Item>(reader.getItems(),metric);


		// Set up a recommendation algorithm
		// 1.  choose a random item, say the 10th item in the set
		Integer[] allItems = (Integer[])reader.getItemIds().
				toArray(new Integer[reader.getUserIds().size()]);
		Integer browsedItemId = allItems[10];

		// 2. A recommender that returns the items most similar to the
		//    the chosen item
		BrowsedItemRecommender alg = new
				BrowsedItemRecommender(reader,browsedItemId,simMap);

		// Create a Map that will hold a set of probabilities p(g|i)
		// associated with each item in the system, map<itemId, map<genre, probability> >

		Map<Integer, Map<String, Double> > pi = new HashMap<>();
		pi = getItemGenreProbabilityMap(reader, pi);

		// A Map to hold the probabilities p(g|u) of movies played by each user in the past.
		Map<Integer, Map<String, Double> > pu = new HashMap<>();
		// A Map to hold the probabilities q(g|u) of recommended movies associated with each user
		Map<Integer, Map<String, Double> > qu = new HashMap<>();

		// Loop over all users
		for (int userId : reader.getUserIds()) {

			// get the user's profile - i.e. set of items that user has interacted with
			// reader.getUserProfiles() returns userProfileMap<Integer, Profile>
			// profile consists of {ID, dataMap <itemID, value> }
			Profile uProf = reader.getUserProfiles().get(userId);
			Integer uProfLength = uProf.getSize();

			// set p(g|u) to the sum of p(g|i) over all items that the user
			// has interacted with, divided by the total number of items store result in usergenreMap;


			Map<String, Double> usergenreMap = new HashMap<>(); 	// stores p(g|u) of each user
			updateUserInteractedGenreMap(uProf, pi, usergenreMap);
			// user , <genre of played items, probability of genre across played items>
			pu.put(userId, usergenreMap);

			///////////////////////////////////////

			// Run the recommendation algorithm to get the items recommended to the user
			Map<String, Double> userRecGenreMap = new HashMap<>();
			updateUserRecommendedGenreMap(userId, alg, userRecGenreMap, pi);
			qu.put(userId, userRecGenreMap);
		}


		Map<Integer, Double> userCalibrationScore = new HashMap<>();
		for (int userId : reader.getUserIds()) {
//			System.out.println("User "+userId);
			Map<String,Double> pu_g = pu.get(userId);
			Map<String,Double> qu_g = qu.get(userId);

			// 	 Calibration calculation
			Double klScore = 0.0;
			double p, q;

			for (String genre : getGenres(reader)) {
				if (pu_g.containsKey(genre))
					p = pu_g.get(genre);
				else
					p = 0.0;

				if (qu_g.containsKey(genre))
					q = qu_g.get(genre);
				else
					q = 0.0;

				if (q == 0) {
					q = deltaQ(pu_g, qu_g, genre);
				}

				klScore += (p * (Math.log(p/q)) > 0 || p * (Math.log(p/q)) < 0) ? (p * (Math.log(p/q))) : 0;

			}
			userCalibrationScore.put(userId, klScore);
//			System.out.println("User: "+userId + ": \t has kl calibration = " + klScore);
//			System.out.println("--------------------------------");
		}
		return userCalibrationScore;
	}

	// associated with each item in the system, map<itemId, map<genre, probability> >
	public static Map<Integer, Map<String, Double>> getItemGenreProbabilityMap(DatasetReader reader, Map<Integer, Map<String, Double>> pi) {
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

	private static void updateUserInteractedGenreMap(Profile uProf, Map<Integer, Map<String, Double> > pi, Map<String, Double> usergenreMap) {
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

	private static void updateUserRecommendedGenreMap(int userId, BrowsedItemRecommender alg,
												  Map<String, Double> userRecGenreMap, Map<Integer, Map<String, Double> > pi) {
		List<Integer> recItems = alg.getRecommendations(userId);

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
	}


	public static double deltaQ(Map<String, Double> pu_g, Map<String, Double> qu_g, String genre) {
		double deltaq;
		double alpha = 0.01;
		deltaq = (1-alpha) * qu_g.get(genre) + alpha * pu_g.get(genre);

		return deltaq;
	}


	// this should be a method in DatasetReader, but it hasn't been
	// implemented - so let's just do it here
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
}


