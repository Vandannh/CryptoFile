package main.java.session;

import java.util.ArrayList;

/**
 * ActiveSessions stores and provides sessions
 * 
 * @since 2019-04-29
 * @version 1.0
 * @author Robin Andersson
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
	
	public void removeSession(Session session) {
		sessions.remove(session);
	}
}
