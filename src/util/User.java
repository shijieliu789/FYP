/**
 * This represents an item that the users can rate
 */

package util;


import java.util.Set;
import java.util.Map;
import java.util.HashMap;



public class User
{
	private Integer id; // the numeric ID of the item
	private Map<String, String> features; // a map of key-values
	
	/**
	 * constructor - creates a new Item object
	 * @param id
	 * @param name
	 */
	public User(final Integer id)
	{
		this.id = id;
		this.features = new HashMap<String,String>();
	}

	/**
	 * constructor - creates a new User object
	 * @param id
	 * @param features2
	 * @param genres
	 * @param genomeScores
	 */
	public User(final Integer id, Map<String, String> features2)
	{
		this.id = id;
		this.features = features2;
	}
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Set<String> getFeatures()
	{
		return this.features.keySet();
	}

	/**
	 * @return a features
	 */
	public String getFeatureValue(String feature) {
		return this.features.get(feature);
	}

	/**
	 * @param name the name to set
	 */
	public void setFeatureValue(String feature, String value) {
		this.features.put(feature,value);
	}
}
