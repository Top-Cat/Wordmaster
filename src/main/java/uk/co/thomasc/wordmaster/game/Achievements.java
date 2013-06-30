package uk.co.thomasc.wordmaster.game;

import android.util.SparseArray;

public enum Achievements {
	IDONTHATEYOU(4, "CgkIj_Ku7KQVEAIQAw"),
	LUCKY(5, "CgkIj_Ku7KQVEAIQBA"),
	PARTICIPANT(1, new String[] {"CgkIj_Ku7KQVEAIQBQ", "CgkIj_Ku7KQVEAIQBg", "CgkIj_Ku7KQVEAIQCA", "CgkIj_Ku7KQVEAIQBw"}, true),
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