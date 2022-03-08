package alg.rerank;

import examples.CalibrationExample;
import profile.Profile;
import similarity.SimilarityMap;
import util.ScoredThingDsc;

import java.io.File;
import java.util.*;

import util.Item;
import util.reader.DatasetReader;

// formula 6

public class CalibrationReranker implements Reranker
{
//    SimilarityMap<Item> distanceMap;
    Map<Integer, Double> userCalibrationScoreMap;
    double lambda;
    String folder = "datasets";
    String dataset = folder + File.separator + "ml20m";
    // set the path and filename of the output file ...

    DatasetReader reader = new DatasetReader(dataset);
    // when instantiated it should generate the calibration scores and store them.
    public CalibrationReranker(double lambda)
    {
//        this.distanceMap = distanceMap;
        this.userCalibrationScoreMap = CalibrationExample.getCalibrationMap(reader);
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
        List<Integer> recs = new ArrayList<Integer>();

        if (scores==null)
            return recs;

        // store all scores in descending order in a sorted set
        double recScoreSum = 0;
        SortedSet<ScoredThingDsc> ss = new TreeSet<ScoredThingDsc>();
        for(Integer id: scores.getIds()) {
            double s = scores.getValue(id);

            if (s>0) {
                ss.add(new ScoredThingDsc(s, id));
                recScoreSum += s;
            }

        }

        double rerankScore = (1-0.9) * recScoreSum - (0.9 * userCalibrationScoreMap.get(userProfile.getId()));

        // save all recommended items in descending order of similarity in the list
        // but leaving out items that are already in the user's profile
        for(Iterator<ScoredThingDsc> iter = ss.iterator(); iter.hasNext();)
        {
            ScoredThingDsc st = iter.next();
            Integer id = (Integer)st.thing;
            if (st.score > 0.0 && !userProfile.contains(id))
            {
                recs.add(id);
            }

        }

        return recs;
    }

    public static void main(String[] args) {
        String folder = "datasets";
        String dataset = folder + File.separator + "ml20m";
        DatasetReader reader = new DatasetReader(dataset);

        CalibrationReranker example = new CalibrationReranker(0.1);
    }
}


