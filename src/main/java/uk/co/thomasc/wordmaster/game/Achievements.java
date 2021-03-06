package uk.co.thomasc.wordmaster.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.json.simple.JSONObject;

import android.util.SparseArray;

import uk.co.thomasc.wordmaster.BaseGame;

@AllArgsConstructor
public enum Achievements {
	PARTICIPANT(1, new String[] {"CgkIj_Ku7KQVEAIQBQ", "CgkIj_Ku7KQVEAIQBg", "CgkIj_Ku7KQVEAIQCA", "CgkIj_Ku7KQVEAIQBw"}, true),
	GOLDPEGS(2, new String[] {"CgkIj_Ku7KQVEAIQCQ", "CgkIj_Ku7KQVEAIQCg", "CgkIj_Ku7KQVEAIQCw", "CgkIj_Ku7KQVEAIQDA"}, true),
	SILVERPEGS(3, new String[] {"CgkIj_Ku7KQVEAIQDg", "CgkIj_Ku7KQVEAIQDw", "CgkIj_Ku7KQVEAIQEA", "CgkIj_Ku7KQVEAIQEQ"}, true),
	IDONTHATEYOU(4, "CgkIj_Ku7KQVEAIQAw"),
	LUCKY(5, "CgkIj_Ku7KQVEAIQBA"),
	NEMESIS(6, "CgkIj_Ku7KQVEAIQDQ"),
	PUTITONRANDOM(7, "CgkIj_Ku7KQVEAIQEg"),
	EAGER(8, "CgkIj_Ku7KQVEAIQEw"),
	LONGGAME(9, "CgkIj_Ku7KQVEAIQFA"),
	NOTHINGTODO(10, "CgkIj_Ku7KQVEAIQFQ"),
	WATCHYOURLANGUAGE(11, "CgkIj_Ku7KQVEAIQFg"),
	SLEEPING(12, "CgkIj_Ku7KQVEAIQFw");

	private int sid;
	@Getter private String[] ids;
	@Getter private boolean incremental;

	private Achievements(String id) {
		this(0, new String[] {id});
	}

	private Achievements(int sid, String id) {
		this(sid, new String[] {id}, false);
	}

	private Achievements(int sid, String[] ids) {
		this(sid, ids, false);
	}

	private static SparseArray<Achievements> apiMap = new SparseArray<Achievements>();
	static {
		for (Achievements achievement : Achievements.values()) {
			Achievements.apiMap.put(achievement.sid, achievement);
		}
	}

	public static Achievements forServerId(int sid) {
		return Achievements.apiMap.get(sid);
	}

	public static void process(JSONObject json) {
		JSONObject achievements = (JSONObject) json.get("achievements");
		for (Object key : achievements.keySet()) {
			int sid = Integer.parseInt((String) key);
			Achievements achievement = Achievements.forServerId(sid);
			if (achievement != null) {
				int increment = ((Long) achievements.get(key)).intValue();
				BaseGame.unlockAchievement(achievement, increment);
			}
		}
	}

}
