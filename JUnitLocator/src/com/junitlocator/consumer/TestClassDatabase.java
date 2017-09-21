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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.junitlocator.json.JUnitTestCaseJson;
import com.junitlocator.json.JUnitTestSuiteJson;
import com.junitlocator.json.ResultsFileJson;

public class TestClassDatabase {
	
	private List<JUnitTestCase> testCases = new ArrayList<>();
	
	private List<JUnitTestSuite> testSuites = new ArrayList<>();
	
	private Map<String /* plug-id+package+classname */, Object> testClassById = new HashMap<>(); 

	public TestClassDatabase() {
	}
	
	
	public void initialize(ResultsFileJson resultsFileParam) {
		
		HashMap<Integer, Object> nonJsonMap = new HashMap<>();
		
		HashMap<Integer, Object> jsonMap = new HashMap<>();
		
		for(JUnitTestCaseJson tcj : resultsFileParam.getTestCases()) {
			
			jsonMap.put(tcj.getId(), tcj);
			
			JUnitTestCase tc = new JUnitTestCase();
			tc.setPackageAndClassName(tcj.getPackageAndClassName());
			tc.setPathInZip(tcj.getPathInZip());
			tc.setPluginName(tcj.getPluginName());
			tc.setAbstractClass(tcj.isAbstractClass());
			
			tc.setContainsTests(tcj.isContainsTests());
			
			nonJsonMap.put(tcj.getId(), tc);
			
			testClassById.put(getMapKey(tc), tc);
			testCases.add(tc);
		}
		
		for(JUnitTestSuiteJson tsj : resultsFileParam.getTestSuites()) {
			
			jsonMap.put(tsj.getId(), tsj);
			
			JUnitTestSuite ts = new JUnitTestSuite();
			ts.setPackageAndClassName(tsj.getPackageAndClassName());
			ts.setPathInZip(tsj.getPathInZip());
			ts.setPluginName(tsj.getPluginName());
			ts.setContainsTests(tsj.isContainsTests());
			ts.setAbstractClass(tsj.isAbstractClass());
			
			nonJsonMap.put(tsj.getId(), ts);
			
			testClassById.put(getMapKey(ts), ts);
			testSuites.add(ts);
		}
		
		
		for(Map.Entry<Integer, Object> nonJsonEntry : nonJsonMap.entrySet()) {
			
			int id = nonJsonEntry.getKey();
			Object uncastValue = nonJsonEntry.getValue();

			if(uncastValue instanceof JUnitTestSuite) {
				JUnitTestSuite parentTs = (JUnitTestSuite)uncastValue;
				
				JUnitTestSuiteJson json = (JUnitTestSuiteJson)jsonMap.get(id);
				
				if(json.getContainedTestClasses() != null) {
					
					for(Integer childId : json.getContainedTestClasses()) {
						
						Object uncastChildObj = nonJsonMap.get(childId);
						if(uncastChildObj instanceof JUnitTestSuite) {
							JUnitTestSuite childTs = (JUnitTestSuite)uncastChildObj;
							childTs.getParents().add(parentTs); // add parent to child
							parentTs.getChildren().add(childTs); // add child to parent
						} else {
							JUnitTestCase childTestcase = (JUnitTestCase)uncastChildObj;
							childTestcase.getParents().add(parentTs); // add parent to child
							parentTs.getChildren().add(childTestcase);  // add child to parent
						}
						
						
					}
					
				}
				
			} 			
			
		}
		
	}
	
	public List<JUnitTestCase> getTestCases() {
		return Collections.unmodifiableList(testCases);
	}
	
	public List<JUnitTestSuite> getTestSuites() {
		return Collections.unmodifiableList(testSuites);
	}
	
	public Optional<Object> getTestClassById(String pluginName, String packageAndClassName) {
		
		return Optional.ofNullable(testClassById.get(pluginName+"/"+packageAndClassName));
	}
	
	
	private static String getMapKey(JUnitTestCase tc) {
		return tc.getPluginName()+"/"+tc.getPackageAndClassName();
	}
	
	private static String getMapKey(JUnitTestSuite ts) {
		return ts.getPluginName()+"/"+ts.getPackageAndClassName();
	}
	
		
}
