package util.evaluator;

import java.util.List;

import profile.Profile;

public interface TestPerfInterface {
	public Double testperf(Integer userId, Profile testProfile, 
			List<Integer> recs, Integer k);
}
