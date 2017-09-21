/*
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. 
*/

package com.junitlocator.utils;

import java.util.ArrayList;
import java.util.List;

/** A method of storing a string that saves memory, for a slight trade off in CPU time.
 * 
 * Strings are split into components by /, with each component interned to save space. 
 * 
 * To reconstruct a string exactly as it was passed to the constructor, call get().
 * */
public class Path {

	private final String[] path;
	
	public Path(String param) {
		List<String> input = new ArrayList<>();
		
		String[] strarr = param.split("/");
		for(String str : strarr) {
			if(!str.trim().isEmpty()) {
				input.add(str.trim().intern());
			}
		}
		
		path = input.toArray(new String[input.size()]);		
	}
	
	public String get() {
		StringBuilder sb = new StringBuilder("");
		
		for(String p : path) {
			sb.append("/"+p);
			
		}
		
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return get().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		Path other = (Path)obj;
		return get().equals(other.get());
	}
}
