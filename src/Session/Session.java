package Session;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Session has a purpose of being established for every client connected
 * to server. The session keeps track of an active client and disconnects
 * the client after a certain time of inactivity is reached.
 * @author Broceps
 *
 */
public class Session {
	private String sessionID;
	private String userID;
	private Timer timer;
	private TimerTask task;
	private int secondsPassed = 0;
	private int sessionMaxTime = 15*60*1000; //15 minutes written in miliseconds


	public Session(String userID) {
		this.userID = userID;
		generateSessionID();
		startTimer();
	}

	/**
	 * Generates an ID for the session
	 */
	public void generateSessionID() {
		BCrypt bcrypt = new BCrypt();
		this.sessionID = bcrypt.hashpw(userID, bcrypt.gensalt(8));
	}

	/**
	 * Increment or reset time passed.
	 * @param 1 - for incrementing time passed, 2 - for resetting time passed
	 */
	public synchronized void changeTime(int value) {
		switch (value) {
		case 1:
			secondsPassed++;
			break;
		case 2:
			secondsPassed = 0;

		default:
			break;
		}
	}

	/**
	 * Starts a timer that counts seconds from 0 and up. Keeps running until
	 * reached sessionMaxtime
	 */
	public void startTimer() {
		timer = new Timer();
		task = new TimerTask() {
			public void run() {
				if(secondsPassed < sessionMaxTime)
					changeTime(1);
				else
					timer.cancel();
				System.out.println("Seconds passed: " + secondsPassed);
			}
		};
		timer.scheduleAtFixedRate(task, 0, 1000);
	}

	/**
	 * Restarts the timer by setting secondsPassed = 0;
	 */
	public void restartTimer() {
		changeTime(2);
	}

	/**
	 * Returns secondsPassed which is the time in seconds, passed since user
	 * was last active
	 * @return seconds passed since user last active
	 */
	public int getSecondsPassed() {
		return this.secondsPassed;
	}
	
	public String getSessionID() {
		return this.sessionID;
	}


	public static void main(String[] args) {
		Session s = new Session("test");

	}


}
