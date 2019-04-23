package Session;

import java.util.ArrayList;

/**
 * ActiveSessions stores and provides sessions
 * @author Broceps
 *
 */
public class ActiveSessions {
	ArrayList<Session> sessions = new ArrayList<Session>();
	
	public void addSession(Session s) {
		sessions.add(s);
	}
	
	public Session getSession(String sessionID) {
		for(int i = 0; i < sessions.size(); i++) {
			if(sessions.get(i).getSessionID().equals(sessionID))
				return sessions.get(i);
		}
		return null;
	}
	
	public void removeSession(String sessionID) {
		sessions.remove(getSession(sessionID));
	}
}
