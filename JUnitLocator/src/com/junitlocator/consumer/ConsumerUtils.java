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

package com.junitlocator.consumer;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.junitlocator.json.JUnitTestCaseJson;
import com.junitlocator.json.JUnitTestSuiteJson;
import com.junitlocator.json.ResultsFileJson;

public class ConsumerUtils {
	
	public static Optional<List<Object>> findDirectChildren(int id, Map<Integer, Object> idMap) {
		List<Object> result = new ArrayList<>();
		
		Object parent = idMap.get(id);
		if(parent == null) {
			return Optional.empty();
		}
		
		if(!(parent instanceof JUnitTestSuiteJson)) {
			
			return Optional.empty();
		}
		
		JUnitTestSuiteJson tsj = (JUnitTestSuiteJson)parent;
		
		
		for(Integer childId: tsj.getContainedTestClasses()) {
			
			Object child = idMap.get(childId);
			if(child == null) {
				throw new RuntimeException("Child was not found in ID map, which should never happen.");
			}
			result.add(child);
		}
	
		return Optional.of(result);
	}
	
	public static Map<Integer, Object> idToTestCaseMap(ResultsFileJson json) {
		
		HashMap<Integer, Object> result = new HashMap<>();
		
		for(JUnitTestCaseJson tcj : json.getTestCases()) {
			
			result.put(tcj.getId(), tcj);
		}
		
		for(JUnitTestSuiteJson tsj : json.getTestSuites()) {
			result.put(tsj.getId(), tsj);
		}
		
		
		return result;
		
		
	}
	
	
	public static ResultsFileJson readResults(String str) {
		return readResults(new ByteArrayInputStream(str.getBytes()));
		
	}
	
	public static ResultsFileJson readResults(InputStream is) {
		
		ObjectMapper om = new ObjectMapper();
		
		try {
			return om.readValue(is, ResultsFileJson.class);
		} catch (Exception e) {
			// Convert to unchecked. 
			throw new RuntimeException(e);
		}
		
	}
}
