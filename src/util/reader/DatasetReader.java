/**
 * DatasetReader is used to read in user and item profiles from the MovieLens dataset. Also reads in the test data.
 */

package util.reader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import profile.Profile;

import util.Item;
import util.UserItemPair;
import util.User;

public class DatasetReader 
{
	private Map<Integer,Profile> userProfileMap;
	private Map<Integer,Profile> itemProfileMap;
	private Map<Integer,Profile> itemGenomeScoresMap;
	private Map<Integer,Item> itemMap;
	private Map<Integer,User> userMap;
	private Map<UserItemPair,Double> testData;
	private Map<Integer,Profile> testProfileMap;

	/** 
	 * Constructs a DatasetReader 
	 * @param datatset : path to files containing the input data
	 */
	public DatasetReader(final String dataset)
	{
		
		// name of files containing input data
		String itemFile = dataset+".items";
		String itemGenomeScoresFile = dataset+".genscores";
		String trainFile = dataset+".train";
		String testFile = dataset+".test";
		String userFile = dataset+".users";
		
		loadItems(itemFile); // must be called first
		loadUsers(userFile);
		
		loadGenomeScores(itemGenomeScoresFile); 
		
		loadProfiles(trainFile);
		
		loadTestData(testFile);
		
	}

	/**
	 * Returns all items loaded.
	 * @return a HashMap containing items
	 */
	public Map<Integer,Item> getItems()
	{
		return itemMap;
	}

	/**
	 * Returns an item.
	 * @return an Item object
	 * @param the numeric ID of the Item object
	 */
	public Item getItem(Integer id)
	{
		return itemMap.get(id);
	}
	
	/**
	 * Returns a user.
	 * @return an User object
	 * @param the numeric ID of the User object
	 */
	public User getUser(Integer id)
	{
		return userMap.get(id);
	}

	/**
	 * Returns all the user profiles loaded.
	 * @return a HashMap containing user profiles
	 */
	public Map<Integer,Profile> getUserProfiles()
	{
		return userProfileMap;
	}

	/**
	 * Returns all the user ids loaded.
	 * @return a Set containing user ids
	 */

	public Set<Integer> getUserIds()
	{
		return userProfileMap.keySet();
	}
	/**
	 * Returns all the item ids loaded.
	 * @return a Set containing item ids
	 */

	public Set<Integer> getItemIds()
	{
		return getItems().keySet();
	}
	



	/**
	 * Returns all the item profiles loaded.
	 * @return a HashMap containing item profiles
	 */
	public Map<Integer,Profile> getItemProfiles()
	{
		return itemProfileMap;
	}

	/**
	 * Returns genome scores for all the items loaded.
	 * @return a HashMap containing item profiles
	 */
	public Map<Integer,Profile> getItemGenomeScores()
	{
		return itemGenomeScoresMap;
	}

	/**
	 * Returns the test data.
	 * @return a HashMap containing the test data
	 */
	public Map<UserItemPair,Double> getTestData()
	{
		return testData;
	}
	public Map<Integer,Profile> getTestProfileMap()
	{
		return testProfileMap;
	}

	/**
	 * Loads all user and item profiles.
	 * @param the path of the file containing the training user-item ratings
	 */
	private void loadProfiles(final String filename) 
	{
		userProfileMap = new HashMap<Integer,Profile>();

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line;
			while ((line = br.readLine()) != null) 
			{
				StringTokenizer st = new StringTokenizer(line, ", \t\n\r\f");
				if(st.countTokens() != 3)
				{
					System.out.println("Error reading from file \"" + filename + "\"");
					System.exit(1);
				}

				Integer userId = Integer.valueOf(st.nextToken());
				Integer itemId = Integer.valueOf(st.nextToken());
				Double rating = Double.valueOf(st.nextToken());

				// add data to user profile map
				
				if (!userMap.containsKey(userId)) {
					// user was not in users file, so construct with no features
					
					User u = new User(userId);
					userMap.put(userId,u);
				}
				if (!itemMap.containsKey(itemId)) {
					// item was not in items file, so its title is unknown
					System.out.println("Adding item "+itemId);
					Item i = new Item(itemId,"unknown");
					itemMap.put(itemId,i);
				}
				
				
				Profile up = userProfileMap.containsKey(userId) ? userProfileMap.get(userId) : new Profile(userId);
				up.addValue(itemId, rating);
				userProfileMap.put(userId, up);

				// add data to item profile map
				Profile ip = itemProfileMap.containsKey(itemId) ? itemProfileMap.get(itemId) : new Profile(itemId);
				ip.addValue(userId, rating);
				itemProfileMap.put(itemId, ip);
			}

			br.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Loads all test data.
	 * @param the path of the file containing the training user-item ratings
	 */
	private void loadTestData(final String filename) 
	{
		testData = new HashMap<UserItemPair,Double>();

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line;
			while ((line = br.readLine()) != null) 
			{
				StringTokenizer st = new StringTokenizer(line, ", \t\n\r\f");
				if(st.countTokens() != 2 && st.countTokens() != 3)
				{
					System.out.println("Error reading from file \"" + filename + "\"");
					System.exit(1);
				}

				Integer userId = Integer.valueOf(st.nextToken());
				Integer itemId = Integer.valueOf(st.nextToken());
				Double rating = (st.countTokens() == 1) ? Double.valueOf(st.nextToken()) : null; // check to see if one more token (i.e. the rating) remains
				testData.put(new UserItemPair(userId, itemId), rating);	// add data to user test data map
			}

			br.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		createTestProfiles();
	}



	/**
	 * Load item descriptions from items datafile
	 * @param filename
	 */
	private void loadItems(final String filename) 
	{
		itemMap = new HashMap<Integer,Item>();
		itemProfileMap = new HashMap<Integer,Profile>();
		itemGenomeScoresMap = new HashMap<Integer,Profile>(); 


		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line;
			while ((line = br.readLine()) != null) 
			{
				int firstIndex = line.indexOf("::");
				int lastIndex = line.lastIndexOf("::");

				Integer movieId = Integer.valueOf(line.substring(0, firstIndex));
				String title = line.substring(firstIndex + 2, lastIndex);
				String genreStr = line.substring(lastIndex + 2);
//				System.out.println(movieId);
//				System.out.println(title);
//				System.out.println(genreStr);
				
				
				// read genres
				Set<String> genres = new HashSet<String>();
				StringTokenizer st = new StringTokenizer(genreStr, "|");
				int ntokens = st.countTokens();
//				if(ntokens < 1)
//				{
//					System.out.println("Error reading from file \"" + filename + "\"");
//					System.exit(1);
//				}

				for (int i = 0; i < ntokens; i++)
					genres.add(st.nextToken());

				// create and add Item object
				Item item = new Item(movieId, title, genres, itemGenomeScoresMap.get(movieId));
				itemMap.put(movieId, item);
				itemProfileMap.put(movieId, new Profile(movieId));
				itemGenomeScoresMap.put(movieId,  new Profile(movieId));

			}
			br.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	private void loadUsers(final String filename) 
	{
		userMap = new HashMap<Integer,User>();
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line;
			while ((line = br.readLine()) != null) 
			{				
				
				// read genres
				Map<String,String> features = new HashMap<String,String>();
				StringTokenizer st = new StringTokenizer(line, "::");
				int ntokens = st.countTokens();
				
				int userId = Integer.valueOf(st.nextToken());
				
				features.put("Age", st.nextToken());
				features.put("Gender", st.nextToken());
				features.put("Occupation", st.nextToken());
				features.put("Zip", st.nextToken());
				// create and add Item object
				User user = new User(userId, features);
				userMap.put(userId, user);
			}
			br.close();
		}
		catch(IOException e)
		{
			return;
		}
	}
	
	/**
	 * Load genome scores
	 * @param filename
	 */
	private void loadGenomeScores(final String filename)
	{

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line;
			br.readLine(); // read header line
			while ((line = br.readLine()) != null) 
			{
				StringTokenizer st = new StringTokenizer(line, ",");
				int ntokens = st.countTokens();
				if(ntokens != 3)
				{
					System.out.println("Error reading from file \"" + filename + "\"");
					System.exit(1);
				}

				Integer movieId = Integer.valueOf(st.nextToken());
				Integer tagId = Integer.valueOf(st.nextToken());
				Double relevance = Double.valueOf(st.nextToken());

				Profile p = (itemGenomeScoresMap.containsKey(movieId) ? itemGenomeScoresMap.get(movieId) : new Profile(movieId));
				p.addValue(tagId, relevance);
				itemGenomeScoresMap.put(movieId, p);
			}

			br.close();
		}
		catch(IOException e)
		{
			return;
		}
	}

	// Additional functionality

	private void createTestProfiles()
	{
		testProfileMap = new HashMap<Integer,Profile>(); 

		for (UserItemPair useritem : testData.keySet()) {
			Integer user = useritem.getUserId();
			Integer item = useritem.getItemId();
			Double val = testData.get(useritem);

			Profile p = (testProfileMap.containsKey(user) ? 
					testProfileMap.get(user) : new Profile(user));
			p.addValue(item, val);
			testProfileMap.put(user, p);	

		}
	}

////////////////////////////////////////////////////////////////////////////////////////////
	/// Some test functions, not currently in use
////////////////////////////////////////////////////////////////////////////////////////////

	private Map<Integer, String> loadGenresTest(final String filename) 
	{
		Map<Integer, String> genreMap = new HashMap<Integer,String>();

		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			String line;
			while ((line = br.readLine()) != null) 
			{
				int firstIndex = line.indexOf("|");
				int lastIndex = line.length();
				if (firstIndex >0) {
					System.out.println(firstIndex);
					System.out.println(lastIndex);
					System.out.println(line.substring(0,firstIndex));
					System.out.println(line.substring(firstIndex+1,lastIndex));

					String genreStr = line.substring(0,firstIndex);
					Integer genreId = Integer.valueOf(line.substring(firstIndex+1,lastIndex));
					genreMap.put(genreId, genreStr);
				}
			}

			br.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		return genreMap;
	}

	private void loadItemsTest(final String filename, Map<Integer, String> genreMap) 
	{
		itemMap = new HashMap<Integer,Item>();
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("test")));
			String line;
			while ((line = br.readLine()) != null) 
			{
				// read genres
				Set<String> genres = new HashSet<String>();
				StringTokenizer st = new StringTokenizer(line, "|");
				int ntokens = st.countTokens();
				if(ntokens < 1)
				{
					System.out.println("Error reading from file \"" + filename + "\"");
					System.exit(1);
				}

				Integer movieId = Integer.valueOf(st.nextToken());

				System.out.println("Number of Tokens="+st.countTokens());
				System.out.println("Movie="+movieId);
				
				
				String title = st.nextToken();
				System.out.println("Title="+title);
				
				String date = st.nextToken();
				System.out.println("Date="+date);
				
		
				String web = st.nextToken();
				System.out.println("Web="+web);


				ntokens = st.countTokens();
				bw.write(movieId+"::"+title+"::");
				boolean firstgenre = true;
				for (int i = 0; i < ntokens; i++) {

					Integer includesGenre = Integer.valueOf(st.nextToken());
					if (movieId==267)
						System.out.println(includesGenre);
					if (includesGenre == 1) {
						String genreString = genreMap.get(i);
						if (movieId==267)
							System.out.println(i+" "+genreString);
						if (!firstgenre) {
							bw.write("|");
							
						} else
							firstgenre=false;
						bw.write(genreString);
					}
				}
				bw.write("\n");



//				for (int i = 1; i <= ntokens; i++) {
//
//					Integer includesGenre = Integer.valueOf(st.nextToken());
//					if (includesGenre == 1) {
//						String genreString = genreMap.get(i);
//						System.out.println(genreString);
//						genres.add(genreString);
//					}
//				}
//				Item item;
//				if (itemGenomeScoresMap.containsKey(movieId)) 
//					item = new Item(movieId, title, genres, itemGenomeScoresMap.get(movieId));
//				else
//					item = new Item(movieId, title, genres, null);
//				itemMap.put(movieId, item);
			}
			bw.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}

	}


}
