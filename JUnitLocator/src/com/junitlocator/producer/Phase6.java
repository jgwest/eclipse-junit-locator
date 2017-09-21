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

package com.junitlocator.producer;

import java.util.HashMap;
import java.util.List;

import com.junitlocator.json.JUnitTestCaseJson;
import com.junitlocator.json.JUnitTestSuiteJson;
import com.junitlocator.json.ResultsFileJson;
import com.junitlocator.producer.ProducerClass.TestType;

public class Phase6 {
	
	public static void run(AnalysisContext ac) {
		
		int nextId = 0;

		// Assign IDs to all the test cases and test suites
		for(List<ProducerClass> cl : ac.getMapNameToClass().values()) {
			
			for(ProducerClass c : cl) {
				
				if(c.getType() == TestType.TEST_CASE || c.getType() == TestType.TEST_SUITE) {
					c.setFinalId(nextId);
					
					nextId++;					
				}
			}
		}

		ResultsFileJson results = new ResultsFileJson();
		
		final HashMap<Integer /*id*/, Object /* test case json or test suite json */> idToJson = new HashMap<>();
		
		// Convert classes to TestCaseJson and TestSuiteJson
		for(List<ProducerClass> cl : ac.getMapNameToClass().values()) {
			
			for(ProducerClass c : cl) {
				
				if(c.getFinalId().isPresent()) {
					
					if(c.getType() == TestType.TEST_CASE) {
						
						JUnitTestCaseJson tcj = new JUnitTestCaseJson();
					
						
						
						tcj.setContainsTests(c.isContainsTests());
						
						tcj.setId(c.getFinalId().get().intValue());
						
						tcj.setPackageAndClassName(notNull(c.getFullClassName()));
						
						tcj.setPluginName(c.getPluginName().get());
						
						tcj.setPathInZip(c.getPath().get());

						tcj.setAbstractClass(c.isAbstractClass());
						
						results.getTestCases().add(tcj);
						
						idToJson.put(tcj.getId(), tcj);
						
					} else if(c.getType() == TestType.TEST_SUITE) {
						
						JUnitTestSuiteJson tsj = new JUnitTestSuiteJson();
						
						tsj.setContainsTests(c.isContainsTests());
						
						tsj.setId(c.getFinalId().get());
						tsj.setPackageAndClassName(notNull(c.getFullClassName()));
						tsj.setPathInZip(c.getPath().get());
						tsj.setPluginName(c.getPluginName().get());
						
						tsj.setAbstractClass(c.isAbstractClass());
						
						// Add child to parent
						for(ProducerClass child : c.getChildren()) {
							tsj.getContainedTestClasses().add(child.getFinalId().get());							
						}
						
						
						results.getTestSuites().add(tsj);
						
						idToJson.put(tsj.getId(), tsj);
						
												
					}
					
				}
				
			}
		}

		// Add parents to children
		for(JUnitTestSuiteJson tsj : results.getTestSuites()) {
			
			for(int childId : tsj.getContainedTestClasses()) {
				Object childJson = idToJson.get(childId);
				
				if(childJson == null) {
					throw new RuntimeException("Child not found: "+childId);
				}
				
				if(childJson instanceof JUnitTestCaseJson) {
					JUnitTestCaseJson cj = (JUnitTestCaseJson)childJson;
					cj.getParents().add(tsj.getId());
					
				} else if(childJson instanceof JUnitTestSuiteJson) {
					JUnitTestSuiteJson sj = (JUnitTestSuiteJson)childJson;
					sj.getParents().add(tsj.getId());
				} else {
					throw new RuntimeException("Invalid child type: "+childJson.getClass().getName());
				}

			}
			
		}
		
		ac.setResult(results);
		
	}
	
	private static String notNull(String str) {
		if(str == null || str.trim().isEmpty()) {
			throw new RuntimeException("String is null");
		}
		
		return str;
	}
}
