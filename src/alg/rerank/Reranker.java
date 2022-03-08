package alg.rerank;
import profile.Profile;
import java.util.List;

public interface Reranker {
	
	//	takes in userProfile and a set of scores.
	public List<Integer> rerank(Profile userProfile, Profile scores);

}
