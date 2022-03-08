package alg;
import alg.rerank.Reranker;
import profile.Profile;

import java.util.List;

import alg.Recommender;
import util.reader.DatasetReader;

//
public class RerankingRecommender implements RecAlg {
	private DatasetReader reader;
	private Reranker reranker;
	private Recommender baselineRecommender;
	
	
	public RerankingRecommender(DatasetReader reader, Recommender baselineRecommender, 
			Reranker reranker) {
		this.reader = reader;
		this.reranker = reranker;
		this.baselineRecommender = baselineRecommender;
	}
	
	public List<Integer> getRecommendations(final Integer userId)
	{	
		Profile userProfile = reader.getUserProfiles().get(userId);
		
		return reranker.rerank(userProfile,
				baselineRecommender.getRecommendationScores(userId));	
	}
	
	
	
	
	

}
