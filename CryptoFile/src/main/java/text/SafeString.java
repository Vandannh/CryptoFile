package main.java.text;


/**
 * @author Mattias Jönnsson
 * 
 * Written 30/05-2019
 *
 */
public class SafeString {
	/**
	 * Replaces given characters to new ones
	 * @param string
	 * @return the StringBuilder
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
	 * Replaces given characters to new ones
	 * @param string
	 * @return the StringBuilder
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
	 * Method that takes away any forwardSlashes
	 * @param string
	 * @return the String
	 */
	public static String stripForwardSlashes(String string) {
		return string.replace("\\", "");
	}
	/**
	 * Method that takes away any backslashes 
	 * @param string
	 * @return the String
	 */
	public static String stripBackwardSlashes(String string) {
		return string.replace("//", "");
	}

	/**
	 * Removes any unneccesary space in the String
	 * @param string
	 * @return the string
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
	 * Method that takes a given string though all the methods above
	 * @param string
	 * @return the string
	 */
	public String completeSafeString(String string) {
		string = trim(string);
		string = stripForwardSlashes(string);
		string = stripBackwardSlashes(string);
		string = escapeCharacters(string);
		return string;
	}
}
