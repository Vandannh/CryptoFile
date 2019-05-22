package main.java.text;

/**
 * This class is used to make a String safe
 * 
 * @version 1.0
 * @since 2019-05-07
 * @author Mattias Jönsson
 *
 */
public class SafeString {
	
	/**
	 * Escapes "bad" characters from a string
	 * 
	 * @param string A string you want to escape characters from
	 * @return The safe string
	 */
	public static String escapeCharacters(String string) {
		StringBuilder sb = new StringBuilder();
		for(char c : string.toCharArray()) {
			switch(c){
			case '<': sb.append("&lt;"); break;
			case '>': sb.append("&gt;"); break;
			case '\"': sb.append("&quot;"); break;
			case '&': sb.append("&amp;"); break;
			case '\'': sb.append("&apos;"); break;
			case '-': sb.append("&ndash;"); break;
			default:
				if(c>0x7e) {
					sb.append("&#"+((int)c)+";");
				}else
					sb.append(c);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Unescapes a safe string
	 * 
	 * @param safeString A string you want to unescape characters from
	 * @return The unescaped string
	 */
	public static String unescapeCharacters(String safeString) {
		StringBuilder sb = new StringBuilder();
		for(String s : safeString.split("&")) {
			for(String s1 : s.split(";")) {
				switch(s1) {
				case "lt": sb.append("<"); break;
				case "gt": sb.append(">"); break;
				case "quot": sb.append("\""); break;
				case "amp": sb.append("&"); break;
				case "apos": sb.append("\'"); break;
				case "ndash": sb.append("-"); break;
				default:
					sb.append(s1);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Stripes a String of all forward slashes
	 * 
	 * @param string A string to strip the forward slashes from 
	 * @return The string without forward slashes
	 */
	public static String stripForwardSlashes(String string) {
		return string.replace("\\", "");
	}
	
	/**
	 * Stripes a string of all backward slashes
	 * 
	 * @param string A string to strip the backward slashes from
	 * @return The string without backward slashes 
	 */
	public static String stripBackwardSlashes(String string) {
		return string.replace("//", "");
	}

	/**
	 * Trims the string
	 * 
	 * @param string A string to trim from
	 * @return The trimmed string
	 */
	public static String trim(String string) {
		string=string.trim();
		int i=0;
		for(char c : string.toCharArray()) {
			if(c!=' ')
				return string.substring(i);
			i++;
		}
		return string;
	}
	
	/**
	 * Make a string completely safe
	 * 
	 * @param string A string to make safe 
	 * @return The safe string
	 */
	public static String completeSafeString(String string) {
		string = trim(string);
		string = stripForwardSlashes(string);
		string = stripBackwardSlashes(string);
		string = escapeCharacters(string);
		return string;
	}
}
