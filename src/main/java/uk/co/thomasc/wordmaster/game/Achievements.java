package uk.co.thomasc.wordmaster.game;

import android.util.SparseArray;

public enum Achievements {
	IDONTHATEYOU(4, "CgkIj_Ku7KQVEAIQAw"),
	LUCKY(5, "CgkIj_Ku7KQVEAIQBA"),
	PARTICIPANT(1, new String[] {"CgkIj_Ku7KQVEAIQBQ", "CgkIj_Ku7KQVEAIQBg", "CgkIj_Ku7KQVEAIQCA", "CgkIj_Ku7KQVEAIQBw"}, true),
	GOLDPEGS(2, new String[] {"CgkIj_Ku7KQVEAIQCQ", "CgkIj_Ku7KQVEAIQCg", "CgkIj_Ku7KQVEAIQCw", "CgkIj_Ku7KQVEAIQDA"}, true),
	SILVERPEGS(3, new String[] {"CgkIj_Ku7KQVEAIQDg", "CgkIj_Ku7KQVEAIQDw", "CgkIj_Ku7KQVEAIQEA", "CgkIj_Ku7KQVEAIQEQ"}, true),
	NEMESIS(6, "CgkIj_Ku7KQVEAIQDQ"),
	;
	
	private int sid;
	private String[] ids;
	private boolean incremental;
	
	private Achievements(String id) {
		this(0, new String[] {id});
	}
	
	private Achievements(int sid, String id) {
		this(sid, new String[] {id}, false);
	}
	
	private Achievements(int sid, String[] ids) {
		this(sid, ids, false);
	}
	
	private Achievements(int sid, String[] ids, boolean incremental) {
		this.sid = sid;
		this.ids = ids;
		this.incremental = incremental;
	}
	
	public String[] getIds() {
		return ids;
	}
	
	public boolean isIncremental() {
		return incremental;
	}
	
	private static SparseArray<Achievements> apiMap = new SparseArray<Achievements>();
	static {
		for (Achievements achievement : values()) {
			apiMap.put(achievement.sid, achievement);
		}
	}
	
	public static Achievements forServerId(int sid) {
		return apiMap.get(sid);
	}
	
}