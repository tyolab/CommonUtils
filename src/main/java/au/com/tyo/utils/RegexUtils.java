/*
 * Copyright (c) 2018 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package au.com.tyo.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

	public static final String REGEX_HAS_NUMBER = ".*\\d+.*";

	public static final String QUOTED_ENTITY1 = "\"([^\"\\]*(?:\\.[^\"\\]*)*)\"";
	public static final String QUOTED_ENTITY2 = "((\").*(\")|(').*('))";
	public static final String QUOTED_ENTITY3 = "\"([^\"]*)\"";
	public static final String NOTE_ENTITY = "\\(.*\\)";
	
	public static ArrayList<String> quotedEntitiesToArray(String what) {
		ArrayList<String> array = new ArrayList<String>();
		Pattern pattern = Pattern.compile(QUOTED_ENTITY2);
		
		Matcher matcher = pattern.matcher(what);
		
		while (matcher.find())
			array.add(what.substring(matcher.start(), matcher.end()).replaceAll("[^a-zA-Z0-9]", " ").trim());
		return array;
	}
	
	public static String removeNotes(String what) {
//		Pattern pattern = Pattern.compile(NOTE_ENTITY);
//		Matcher matcher = pattern.matcher(what);
//		matcher.replaceAll("");
		return what.replaceAll(NOTE_ENTITY, "");
	}

	public static boolean containsNumber(String what) {
	    return what.matches(REGEX_HAS_NUMBER);
    }
}
