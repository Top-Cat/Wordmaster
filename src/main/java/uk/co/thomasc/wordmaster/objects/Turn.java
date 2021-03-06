package uk.co.thomasc.wordmaster.objects;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Date;

import org.json.simple.JSONObject;

@EqualsAndHashCode(of = {"turnID"})
public class Turn {

	public static String keySegment = "daP5fkiIveAqDR/auk2KsLqNVgofMp5+LweMGMcMZDwiGgedLmE+y5KzQKCI69zSzWiOz8LJerxfLFp7yHHLCNsdRjmeqHxaMS";

	/* Properties */
	private final int turnID;
	@Getter private int turnNum;
	@Getter private Date timestamp;
	@Getter private User user;
	@Getter private String guess;
	@Getter private int correctLetters, displacedLetters;
	@Getter private String opponentWord;

	/* Constructors */
	public Turn(int id) {
		turnID = id;
	}

	/* Modifiers */
	public int getID() {
		return turnID;
	}

	public long getUnixTimestamp() {
		return timestamp.getTime();
	}

	public Turn update(JSONObject turnObj) {
		turnNum = ((Long) turnObj.get("turnnum")).intValue();
		user = User.getUser((String) turnObj.get("playerid"));
		guess = (String) turnObj.get("guess");
		timestamp = new Date((Long) turnObj.get("when"));
		correctLetters = ((Long) turnObj.get("correct")).intValue();
		displacedLetters = ((Long) turnObj.get("displaced")).intValue();

		if (correctLetters == 4) {
			opponentWord = (String) turnObj.get("oppword");
		}

		return this;
	}
}
