package student;


import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.*;

import org.apache.commons.csv.*;
import org.junit.Test;


public class TweetCollection extends TreeMap<String, Tweet>{

	// DONE: add appropriate data types

	//private Iterator<Map.Entry<String, Tweet>> it;

	private TreeMap<String, Polarity> sentimentWords;
	private TreeMap<String, TreeMap<Strength, Polarity>> finegrainedWords;

	public TweetCollection() {
		// Constructor

		// DONE
		super();
	}


	/*
	 * NEXT TWO are for the neighbours
	 */
	public void push(String ID) {
		// inserts first element of list, treating as a queue
		this.put(ID, getTweetByID(ID));
	}

	public void pop() {
		// deletes first element of list, treating as a queue
		this.remove(this.firstKey());
	}


	/*
	 * functions for accessing individual tweets
	 */

	public Tweet getTweetByID (String ID) {
		// PRE: -
		// POST: Returns the Tweet object that with tweet ID

		// DONE
		for(Map.Entry<String,Tweet> entry : this.entrySet()) {
			if(entry.getKey().equals(ID))
				return entry.getValue();
		}
		return null;
	}



	public Integer numTweets() {
		// PRE: -
		// POST: Returns the number of tweets in this collection

		// DONE
		Integer count = 0;
		for(Tweet entry : this.values()) {
			count++;
		}
		return count;
	}


	/*
	 * functions for accessing sentiment words
	 */

	public Polarity getBasicSentimentWordPolarity(String w) {
		// PRE: w not null, basic sentiment words already read in from file
		// POST: Returns polarity of w

		// DONE
		for(Map.Entry<String,Polarity> entry : sentimentWords.entrySet())
			if(entry.getKey().equals(w))
				return entry.getValue();

		return Polarity.NONE;
	}



	public Polarity getFinegrainedSentimentWordPolarity(String w) {
		// PRE: w not null, finegrained sentiment words already read in from file
		// POST: Returns polarity of w

		// DONE

		for(java.util.Map.Entry<String, TreeMap<Strength, Polarity>> entry : finegrainedWords.entrySet())
			if(entry.getKey().equals(w))
				return entry.getValue().lastEntry().getValue();

		return Polarity.NONE;
	}



	public Strength getFinegrainedSentimentWordStrength(String w) {
		// PRE: w not null, finegrained sentiment words already read in from file
		// POST: Returns strength of w

		// DONE

		for(java.util.Map.Entry<String, TreeMap<Strength, Polarity>> entry : finegrainedWords.entrySet())
			if(entry.getKey().equals(w))
				return entry.getValue().lastKey();

		return Strength.NONE;
	}

	/*
	 * functions for reading in tweets
	 *
	 */


	public void ingestTweetsFromFile(String fInName) throws IOException {
		// PRE: -
		// POST: Reads tweets from .csv file, stores in data structure

		// NOTES
		// Data source, file format description at http://help.sentiment140.com/for-students
		// Using apache csv reader: https://www.callicoder.com/java-read-write-csv-file-apache-commons-csv/

        try (
                Reader reader = Files.newBufferedReader(Paths.get(fInName));
                CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            ) {

        	Iterable<CSVRecord> csvRecords = csvParser.getRecords();

        	for (CSVRecord csvRecord : csvRecords) {
                // Accessing Values by Column Index

        		Tweet tw = new Tweet(csvRecord.get(0), // gold polarity
        				csvRecord.get(1), 				// ID
        				csvRecord.get(2), 				// date
        				csvRecord.get(4), 				// user
        				csvRecord.get(5));				// text

        		// DONE: insert tweet tw into appropriate data type
        		this.put(tw.getID(), tw);
            }
        }
	}

	/*
	 * functions for sentiment words
	 */

	public void importBasicSentimentWordsFromFile (String fInName) throws IOException {
		// PRE: -
		// POST: Read in and store basic sentiment words in appropriate data type

		//Found how to import .txt here: https://docs.oracle.com/javase/tutorial/essential/io/index.html
		// DONE
		sentimentWords = new TreeMap<String, Polarity>();

		try (
				BufferedReader reader = Files.newBufferedReader(Paths.get(fInName));
			) {

			String line = reader.readLine();

			while (line != null) {
				String[] words = line.split(" ");
				sentimentWords.put(words[0], Tweet.changeToPolarity(words[1]));
				line = reader.readLine(); //next line
			}
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
	}



	public void importFinegrainedSentimentWordsFromFile (String fInName) throws IOException {
		// PRE: -
		// POST: Read in and store finegrained sentiment words in appropriate data type

		// DONE
		finegrainedWords = new TreeMap<String, TreeMap<Strength, Polarity>>();

		try (
				BufferedReader reader = Files.newBufferedReader(Paths.get(fInName));
			) {

			String line = reader.readLine();

			while (line != null) {
				String tmod = line;
				tmod = tmod.replace("type=", "");
				tmod = tmod.replace("subj len=1 word1=", " ");
				tmod = tmod.replaceAll(" pos1=.*?\\s", " ");
				tmod = tmod.replace("stemmed1=", "..");
				tmod = tmod.replace(" priorpolarity=", "..");
				tmod = tmod.replace("..n..", "");
				tmod = tmod.replace("..y..", "");
				//sorry I know this is messy but its all I could get to work

				String[] words = tmod.split(" ");

				TreeMap<Strength, Polarity> polarityTMap = new TreeMap<Strength, Polarity>();
				polarityTMap.put(Tweet.changeToStrength(words[0]), Tweet.changeToPolarity(words[2]));

				finegrainedWords.put(words[1], polarityTMap);
				line = reader.readLine();
			}
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
	}



	public Boolean isBasicSentWord (String w) {
		// PRE: Basic sentiment words have been read in and stored
		// POST: Returns true if w is a basic sentiment word, false otherwise

		// DONE
		for(String entry : sentimentWords.keySet())
			if(entry.equals(w))
				return true;

		return false;
	}



	public Boolean isFinegrainedSentWord (String w) {
		// PRE: Finegrained sentiment words have been read in and stored
		// POST: Returns true if w is a finegrained sentiment word, false otherwise

		// DONE
		for(String entry : finegrainedWords.keySet())
			if(entry.equals(w))
				return true;
		return false;
	}



	public void predictTweetSentimentFromBasicWordlist() {
		// PRE: Finegrained word sentiment already imported
		// POST: For all tweets in collection, tweet annotated with predicted sentiment
		//         based on count of sentiment words in sentWords

		// DONE
		for(Tweet entry : this.values()) {
			int posWords = 0;
			int negWords = 0;

			for(String word : entry.getWords()) {
				if(this.getBasicSentimentWordPolarity(word) == Polarity.POS)
					posWords++;
				else if(this.getBasicSentimentWordPolarity(word) == Polarity.NEG)
					negWords++;
			}

			if(posWords == 0 && negWords == 0)
				entry.setPredictedPolarity(Polarity.NONE);
			else if(posWords > negWords)
				entry.setPredictedPolarity(Polarity.POS);
			else if(posWords < negWords)
				entry.setPredictedPolarity(Polarity.NEG);
			else
				entry.setPredictedPolarity(Polarity.NEUT);
		}
	}



	public void predictTweetSentimentFromFinegrainedWordlist (Integer strongWeight, Integer weakWeight) {
		// PRE: Finegrained word sentiment already imported
		// POST: For all tweets in v, tweet annotated with predicted sentiment
		//         based on count of sentiment words in sentWords

		// DONE

		for(Tweet entry : this.values()) {
			int posWords = 0;
			int negWords = 0;
			int strongWords = 0;
			int weakWords = 0;

			for(String word : entry.getWords()) {
				if(this.getFinegrainedSentimentWordPolarity(word) == Polarity.POS)
					posWords++;
				else if(this.getFinegrainedSentimentWordPolarity(word) == Polarity.NEG)
					negWords++;

				if(this.getFinegrainedSentimentWordStrength(word) == Strength.STRONG)
					strongWords++;
				else if(this.getFinegrainedSentimentWordStrength(word) == Strength.WEAK)
					weakWords++;
			}

			if(posWords == 0 && negWords == 0) {
				entry.setPredictedPolarity(Polarity.NONE);
			}
			else if(posWords > negWords) {
				entry.setPredictedPolarity(Polarity.POS);
				entry.setWeight(weakWords*weakWeight + strongWords*strongWeight);
			}
			else if(posWords < negWords) {
				entry.setPredictedPolarity(Polarity.NEG);
				entry.setWeight(weakWords*weakWeight + strongWords*strongWeight);
			}
			else {
				entry.setPredictedPolarity(Polarity.NEUT);
			}
		}
	}
		/*
		getFinegrainedSentimentWordStrength();
		getFinegrainedSentimentWordPolarity();
		*/



	/*
	 * functions for inverse index
	 *
	 */

	public Map<String, Vector<String>> importInverseIndexFromFile (String fInName) throws IOException {
		// PRE: -
		// POST: Read in and returned contents of file as inverse index
		//         invIndex has words w as key, IDs of tweets that contain w as value

		// DONE

		//Found how to import .txt here: https://docs.oracle.com/javase/tutorial/essential/io/index.html

		Map<String, Vector<String>> inverseIndex = new TreeMap<String, Vector<String>>();

		try (
				BufferedReader reader = Files.newBufferedReader(Paths.get(fInName));
			) {

			String line = reader.readLine();

			while (line != null) {
				String[] words = line.split(" "); //gives word at [0] and ids at [1]

				Vector<String> IDs = new Vector<String>();
				String[] arrayIDs = words[1].split(","); //breaks up ids into array
				IDs.addAll(Arrays.asList(arrayIDs)); //puts ids in vector

				inverseIndex.put(words[0], IDs);
				line = reader.readLine(); //next line
			}
		}
		catch (IOException e) {
			System.out.println("in exception: " + e);
		}
		return inverseIndex;
	}


	/*
	 * functions for graph construction
	 */

	public void constructSharedWordGraph(Map<String, Vector<String>> invIndex) {
		// PRE: invIndex has words w as key, IDs of tweets that contain w as value
		// POST: Graph constructed, with tweets as vertices,
		//         and edges between them if they share a word

		// DONE
		for(Tweet tweet : this.values())
			//all the tweets in this treemap

			for(String word : tweet.getWords())
				//all the words of the tweet

				if(invIndex.get(word) != null)
					//the word is in invIndex

					for(String id : invIndex.get(word))
						//all the ids associated with this word

						if(!id.equals(tweet.getID()) && this.containsKey(id))
							//not this tweet && ID is in this treemap

								tweet.addNeighbour(id);
	}



	public Integer numConnectedComponents() {
		// PRE: -
		// POST: Returns the number of connected components

		// DONE

		Vector<String> numOfComponents = new Vector<String>();

		for(Tweet tweet : this.values())
			//all the tweets in this treemap

			if(!numOfComponents.contains(tweet.getAnnotationLabel()))
				//if the this a-label is not already in the vector

				numOfComponents.add(tweet.getAnnotationLabel());

		return numOfComponents.size();

	}



	public void annotateConnectedComponents() {
		// PRE: -
		// POST: Annotates graph so that it is partitioned into components

		// DONE
		for(Tweet t: this.values())
			annotateConnectedComponents(t); //call to my helper function below
	}

	public void annotateConnectedComponents(Tweet t) {
		for(String neighID : t.getNeighbourTweetIDs()) {
			//all the neighbours if this tweet

			if(!getTweetByID(neighID).getAnnotationLabel().equals(t.getAnnotationLabel())) {
				//as long as the neighbour's a-label is different to the parameter's

				getTweetByID(neighID).setAnnotationLabel(t.getAnnotationLabel());
				annotateConnectedComponents(getTweetByID(neighID)); //recursive call for this neighbour
			}
		}
	}


	public Integer componentSentLabelCount(String ID, Polarity p) {
		// PRE: Graph components are identified, ID is a valid tweet
		// POST: Returns count of labels corresponding to Polarity p in component containing ID

		// DONE
		Tweet tweet = getTweetByID(ID);
		Integer count = 0;

		for(Tweet item : this.values())
			//all tweets

			if(item.getAnnotationLabel().equals(tweet.getAnnotationLabel()) && item.getPredictedPolarity().equals(p))
				//tweets in same component && the tweet in component has polarity p
				count++;

		return count;
	}


	public void propagateLabelAcrossComponent(String ID, Polarity p, Boolean keepPred) {
		// PRE: ID is a tweet id in the graph
		// POST: Labels tweets in component with predicted polarity p
		//         (if keepPred == T, only tweets w pred polarity None; otherwise all tweets

		// DONE
		Tweet t = getTweetByID(ID);

		for(String neighID : t.getNeighbourTweetIDs()) {

			if(keepPred) { //only tweets w pred polarity None
				if(getTweetByID(neighID).getPredictedPolarity() == Polarity.NONE)
					getTweetByID(neighID).setPredictedPolarity(p);
			}

			else { //all tweets
				getTweetByID(neighID).setPredictedPolarity(p);
				t.setPredictedPolarity(p);

			}
		}
	}



	public void propagateMajorityLabelAcrossComponents(Boolean keepPred) {
		// PRE: Components are identified
		// POST: Tweets in each component are labelled with the majority sentiment for that component
		//       Majority label is defined as whichever of POS or NEG has the larger count;
		//         if POS and NEG are both zero, majority label is NONE
		//         otherwise, majority label is NEUT
		//       If keepPred is True, only tweets with predicted label None are labelled in this way
		//         otherwise, all tweets in the component are labelled in this way

		// DONE

		for(Tweet t : this.values()) {
			//all the tweets in this treemap
			int pos = 0;
			int neg = 0;

			if(t.getPredictedPolarity() == Polarity.POS)
				pos++;
			else if(t.getPredictedPolarity() == Polarity.NEG)
				neg++;

			for(String neighID : t.getNeighbourTweetIDs()) {
				//all the neighbours if this tweet

				if(getTweetByID(neighID).getPredictedPolarity() == Polarity.POS)
					pos++;
				else if(getTweetByID(neighID).getPredictedPolarity() == Polarity.NEG)
					neg++;
			}
			if(keepPred) {
				for(String neighID : t.getNeighbourTweetIDs()) {

					if(getTweetByID(neighID).getPredictedPolarity() == Polarity.NONE || t.getPredictedPolarity() == Polarity.NONE) {
						//if either tweet or its neighbours has polarity none

						if(pos == 0 && neg == 0) {
							t.setPredictedPolarity(Polarity.NONE);
							getTweetByID(neighID).setPredictedPolarity(Polarity.NONE);
						}
						else if(pos > neg) {
							t.setPredictedPolarity(Polarity.POS);
							getTweetByID(neighID).setPredictedPolarity(Polarity.POS);
						}
						else if(pos < neg) {
							t.setPredictedPolarity(Polarity.NEG);
							getTweetByID(neighID).setPredictedPolarity(Polarity.NEG);
						}
						else {
							t.setPredictedPolarity(Polarity.NEUT);
							getTweetByID(neighID).setPredictedPolarity(Polarity.NEUT);
						}
					}
				}
			}

			else {
				for(String neighID : t.getNeighbourTweetIDs()) {
					if(pos == 0 && neg == 0) {
						t.setPredictedPolarity(Polarity.NONE);
						getTweetByID(neighID).setPredictedPolarity(Polarity.NONE);
					}
					else if(pos > neg) {
						t.setPredictedPolarity(Polarity.POS);
						getTweetByID(neighID).setPredictedPolarity(Polarity.POS);
					}
					else if(pos < neg) {
						t.setPredictedPolarity(Polarity.NEG);
						getTweetByID(neighID).setPredictedPolarity(Polarity.NEG);
					}
					else {
						t.setPredictedPolarity(Polarity.NEUT);
						getTweetByID(neighID).setPredictedPolarity(Polarity.NEUT);
					}
				}
			}
		}
	}



	/*
	 * functions for evaluation
	 */

	public Double accuracy () {
		// PRE: -
		// POST: Calculates and returns accuracy of labelling

		// DONE
		Double numCorrect = 0.0;
		Double numPredicted = 0.0;
		for(Map.Entry<String,Tweet> entry : this.entrySet()) {

			if(entry.getValue().getGoldPolarity() != Polarity.NONE && entry.getValue().getPredictedPolarity() != Polarity.NONE) {
				numPredicted++;

				if(entry.getValue().correctlyPredictedPolarity())
					numCorrect++;
			}
		}
		return numCorrect/numPredicted;
	}


	public Double coverage () {
		// PRE: -
		// POST: Calculates and returns coverage of labelling

		// DONE
		Double totalTweets = 0.0;
		Double numPredicted = 0.0;
		for(Map.Entry<String,Tweet> entry : this.entrySet()) {
			totalTweets++;
			if(entry.getValue().getGoldPolarity() != Polarity.NONE && entry.getValue().getPredictedPolarity() != Polarity.NONE)
				numPredicted++;
		}
		return numPredicted/totalTweets;

	}

	public static void main(String[] args) {


	}
}
