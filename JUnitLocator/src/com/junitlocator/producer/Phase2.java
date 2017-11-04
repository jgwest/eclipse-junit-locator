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

import java.util.List;
import java.util.Map;

import com.junitlocator.producer.ProducerClass.TestType;

/** Analyze all classes in AnalysisContext, and ensure that all children with a parent class 
 * that is a test should also be tests themselves. */
public class Phase2 {

	public static void run(AnalysisContext ac) {

		boolean newTestClassFound = true;
		
		Map<String, List<ProducerClass>> map = ac.getMapNameToClass();
		
		while(newTestClassFound) {
			
			newTestClassFound = false;
		
			// For each class...
			for(Map.Entry<String, List<ProducerClass>> e : map.entrySet()) {
				
				for(ProducerClass c : e.getValue()) {
					if(c.getType() == TestType.TEST_CASE) {
						continue;
					}
					
					// If a class has a parent that is a test case, then it's child class should be too.
					if(isParentATestCase(c, map)) {
						c.setType(TestType.TEST_CASE);
						c.setContainsTests(true);
						newTestClassFound = true;
					}
				}
				
			}
			
		}
	}
	
	private static boolean isParentATestCase(ProducerClass c, Map<String, List<ProducerClass>> map) {
		
		List<ProducerClass> superClassList = map.get(c.superClassName);
		if(superClassList == null || superClassList.isEmpty()) {
			return false;
		}
		
		if(superClassList.size() > 1) {
			
//			if(c.superClassName.contains("junit") ) {
////				System.err.println("Warning: multiple superclass entries for "+c.superClassName);
////				throw new RuntimeException("Multiple superclass entries for "+c.superClassName);
//			} else {
//				return false;	
//			}
			
		}
				
		ProducerClass superClass = superClassList.get(0);
		if(superClass.getType() == TestType.TEST_CASE) {
			return true;
		} else {
			return false;
		}
		
	}
}
