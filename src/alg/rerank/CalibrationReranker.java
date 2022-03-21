package alg.rerank;

import alg.np.BrowsedItemRecommender;
import examples.CalibrationExample;
import profile.Profile;
import similarity.SimilarityMap;
import similarity.metric.SimilarityMetric;
import similarity.metric.item.GenreMetric;
import util.ScoredThingDsc;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import util.Item;
import util.reader.DatasetReader;

// formula 6

public class CalibrationReranker implements Reranker
{
//    Map<Integer, Double> userCalibrationScoreMap;
    double lambda;
    String folder = "datasets";
    String dataset = folder + File.separator + "ml20m";

    DatasetReader reader = new DatasetReader(dataset);
    // when instantiated it should generate the calibration scores and store them.
    public CalibrationReranker(double lambda)
    {
//        this.distanceMap = distanceMap;
//        this.userCalibrationScoreMap = CalibrationExample.getCalibrationMap(reader);
        this.lambda = lambda;
    }

    // !!!
    public List<Integer> rerank(Profile userProfile,Profile scores)
    {
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
        SimilarityMetric<Item> metric = new GenreMetric();
        SimilarityMap<Item> simMap = new SimilarityMap<Item>(reader.getItems(),metric);
        // Set up a recommendation algorithm
        // 1.  choose a random item, say the 10th item in the set
        Integer[] allItems = (Integer[])reader.getItemIds().
                toArray(new Integer[reader.getUserIds().size()]);
        Integer browsedItemId = allItems[10];
        // 2. A recommender that returns the items most similar to
        //    the chosen item
        BrowsedItemRecommender alg = new
                BrowsedItemRecommender(reader,browsedItemId,simMap);

        List<Integer> rerankedList = new ArrayList<Integer>();
        // Gets Top 50 recommended items.
        List<Integer> recItems = alg.getRecommendations(userProfile.getId()).stream().limit(50).collect(Collectors.toList());
        System.out.println("Reranked List size: " + rerankedList.size());
        rerankedList.add(recItems.get(0));   // adds top recommended item to new list.
        System.out.println("Reranked List size: " + rerankedList.size());
        recItems.remove(0);

        for (int i = 0; i < 2; i++) {
            Integer bestRerankItemId = findHighestScoringItem(userProfile, rerankedList, recItems, scores);
            System.out.println("Id: " + bestRerankItemId);
            rerankedList.add(bestRerankItemId);
            recItems.remove(bestRerankItemId);
            System.out.println("Reranked List size: " + rerankedList.size() + " Original List size: " + recItems.size());
        }


//        if (scores==null)
//            return rerankedList;
//
//        // store all scores in descending order in a sorted set
//        double recScoreSum = 0;
//        SortedSet<ScoredThingDsc> ss = new TreeSet<ScoredThingDsc>();
//        for(Integer id: scores.getIds()) {
//            double s = scores.getValue(id);
//
//            if (s>0) {
//                ss.add(new ScoredThingDsc(s, id));
//                recScoreSum += s;
//            }
//
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
            System.out.println("Passing item : " + recItems.get(i));
            rerankedScore = lambda*sI - lambda * (CalibrationExample.getCKL(userProfile.getId(), rerankedList, recItems.get(i)));
            itemWithNewScoreMap.put(recItems.get(i), rerankedScore);
        }

        int highestId = itemWithNewScoreMap.keySet().stream().findFirst().get();
        return highestId;
    }

    public static void main(String[] args) {
        String folder = "datasets";
        String dataset = folder + File.separator + "ml20m";
        DatasetReader reader = new DatasetReader(dataset);

        CalibrationReranker example = new CalibrationReranker(0.1);
    }
}


