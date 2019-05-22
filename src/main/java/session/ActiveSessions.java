package main.java.session;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * ActiveSessions stores and provides sessions
 * @author Broceps
 *
 */
public class ActiveSessions implements Serializable {
	private static final long serialVersionUID = 1L;
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
	
	public void removeSession(Session session) {
		sessions.remove(session);
	}
}
