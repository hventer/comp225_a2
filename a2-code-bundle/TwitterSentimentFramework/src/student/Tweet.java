package student;

import java.util.*;

enum Polarity {
	POS, NEG, NEUT, NONE;
}

enum Strength {
	STRONG, WEAK, NONE;
}

public class Tweet {

	// DONE: Add appropriate data types
	private Polarity goldPolarity; //gold polarity of tweet
	private String id; //id of tweet
	private String date; //date of tweet
	private String user; //the username
	private String text; //the text that the user wrote

	private Polarity predictedPolarity; //predicted polarity of tweet

	private TweetCollection neighbour; // the collection of neighbours

	private String annotationLabel;

	private Integer weight;

	public Tweet(String p, String i, String d, String u, String t) {
		// DONE
		goldPolarity = changeToPolarity(p);
		id = i;
		date = d;
		user = u;
		text = t;
		predictedPolarity = Polarity.NONE;
		neighbour = new TweetCollection();
		annotationLabel = i; //I just made the label the id (becuase it is unique to each tweet)
		weight = 0;

	}

	public static Polarity changeToPolarity(String p) {
		switch (p) {
        case "0":
        	return Polarity.NEG;
        case "2":
        	return Polarity.NEUT;
        case "4":
        	return Polarity.POS;
        case "negative":
        	return Polarity.NEG;
        case "positive":
        	return Polarity.POS;
        default:
        	return Polarity.NONE;
		}
	}

	public static Strength changeToStrength(String s) {
		switch (s) {
        case "weak":
        	return Strength.WEAK;
        case "strong":
        	return Strength.STRONG;
        default:
        	return Strength.NONE;
		}
	}


	//annotates this tweet into specific component.
	public void setAnnotationLabel(String label) {
		annotationLabel = label;
	}

	public String getAnnotationLabel() {
		return annotationLabel;
	}


	public void addNeighbour(String ID) {
		// PRE: -
		// POST: Adds a neighbour to the current tweet as part of graph structure

		// DONE
		neighbour.push(ID);
	}

	public Integer numNeighbours() {
		// PRE: -
		// POST: Returns the number of neighbours of this tweet

		// DONE
		Integer count = 0;
		for(Tweet entry : neighbour.values())
			count++;

		return count;
	}

	public void deleteAllNeighbours() {
		// PRE: -
		// POST: Deletes all neighbours of this tweet

		// TODO
		while(!neighbour.isEmpty())
			neighbour.pop();
	}

	public Vector<String> getNeighbourTweetIDs () {
		// PRE: -
		// POST: Returns IDs of neighbouring tweets as vector of strings

		// DONE
		Vector<String> v = new Vector<String>();

		for(String neighID : neighbour.keySet())
			v.add(neighID);

		return v;
	}

	public Boolean isNeighbour(String ID) {
		// PRE: -
		// POST: Returns true if ID is neighbour of the current tweet, false otherwise

		// TODO
		for(String neighID : neighbour.keySet())
	        if(neighID.equals(ID))
	        	return true;
		return false;
	}


	public Boolean correctlyPredictedPolarity () {
		// PRE: -
		// POST: Returns true if predicted polarity matches gold, false otherwise

		// DONE
		if(goldPolarity == predictedPolarity)
			return true;
		else
			return false;
	}

	public Polarity getGoldPolarity() {
		// PRE: -
		// POST: Returns the gold polarity of the tweet

		// DONE
		return goldPolarity;
	}

	public Polarity getPredictedPolarity() {
		// PRE: -
		// POST: Returns the predicted polarity of the tweet

		// DONE
		return predictedPolarity;
	}

	public void setPredictedPolarity(Polarity p) {
		// PRE: -
		// POST: Sets the predicted polarity of the tweet

		// DONE
		predictedPolarity = p;
	}

	public Integer getWeight() {
		// PRE: -
		// POST: Returns the weight of polarity of the tweet

		// DONE
		return weight;
	}

	public void setWeight(Integer w) {
		// PRE: -
		// POST: Sets the weight of polarity of the tweet

		// DONE
		weight = w;
	}

	public String getID() {
		// PRE: -
		// POST: Returns ID of tweet

		// DONE
		return id;
	}

	public String getDate() {
		// PRE: -
		// POST: Returns date of tweet

		// DONE
		return date;
	}

	public String getUser() {
		// PRE: -
		// POST: Returns identity of tweeter

		// DONE
		return user;
	}

	public String getText() {
		// PRE: -
		// POST: Returns text of tweet as a single string

		// DONE
		return text;
	}

	public String[] getWords() {
		// PRE: -
		// POST: Returns tokenised text of tweet as array of strings

		if (this.getText() == null)
			return null;

		String[] words = null;

		String tmod = this.getText();
		tmod = tmod.replaceAll("@.*?\\s", "");
		tmod = tmod.replaceAll("http.*?\\s", "");
		tmod = tmod.replaceAll("\\s+", " ");
		tmod = tmod.replaceAll("[\\W&&[^\\s]]+", "");
		tmod = tmod.toLowerCase();
		tmod = tmod.trim();
		words = tmod.split("\\s");

		return words;
	}

	public static void main(String[] args) {

	}
}
