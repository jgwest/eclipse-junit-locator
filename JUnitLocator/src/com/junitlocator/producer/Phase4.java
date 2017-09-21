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
import java.util.Map;

import com.junitlocator.producer.AnalysisContext.PluginEntry;
import com.junitlocator.producer.ProducerClass.TestType;

public class Phase4 {

	public static void run(AnalysisContext ac) {

		boolean newTestSuiteFound = true;
		
		Map<String, List<ProducerClass>> map = ac.getMapNameToClass();
		
		while(newTestSuiteFound) {
			
			newTestSuiteFound = false;
		
			// For each class we have previously scanned..
			for(Map.Entry<String, List<ProducerClass>> e : map.entrySet()) {
				
				for(ProducerClass c : e.getValue()) {
					if(c.getType() == TestType.TEST_CASE) {
						continue;
					}
					if(c.getType() == TestType.TEST_SUITE) {
						continue;
					}
					
					// If parent class is a test suite, then mark the child class is a test suite
					if(isParentATestSuite(c, map)) {
						c.setType(TestType.TEST_SUITE);
//						System.out.println("new test suite found: "+c.fullClassName);
						newTestSuiteFound = true;
					}
				}
				
			}
			
		}
		
		
		Map<String /*plugin path*/, String /* plugin name w/o version */> pluginFilePathToPluginName = new HashMap<>();
		for(PluginEntry pluginEntry : ac.getPluginList()) {
//			String ppStr = pluginEntry.getPath().get().trim();
			
			
//			// Example ppStr: /plugins/org.eclipse.wst.common.tests.validation_1.0.400.v201202090336.jar
//			
//			// Strip leading slash
//			if(ppStr.startsWith("/")) {
//				ppStr = ppStr.substring(1);
//			}
//			
//			// Strip leading plugins/
//			System.out.println(ppStr);
//			ppStr = ppStr.substring(ppStr.indexOf("/")+1);
//			
//			
//			
//			
//			{
//				for(int x = 0; x < ppStr.length(); x++) {
//					
//					
//				}
//			}
//			
//			// Remove plugin version
//			ppStr = ppStr.substring(0, ppStr.lastIndexOf("_"));
			
			pluginFilePathToPluginName.put(pluginEntry.getPath().get(), pluginEntry.getName());
			
		}
		
		
		// Identify which plug-in a class is contained in
		
		for(List<ProducerClass> lc : map.values()) {
			
			for(ProducerClass c : lc) {
				// For each of the classes in our DB
				
				String classFilePath = c.getPath().get();
				
				// Find the plug-in path that best matches the class
				String longestPluginPathFound = null;
				for(Map.Entry<String, String> e : pluginFilePathToPluginName.entrySet()) {
					String pluginPath = e.getKey();
					
					if(classFilePath.startsWith(pluginPath)) {
						
						if(longestPluginPathFound == null || (pluginPath.length() > longestPluginPathFound.length() )) {
							longestPluginPathFound = pluginPath;
						}
					}
					
				}
				
				if(longestPluginPathFound != null) {
					c.setPluginName(pluginFilePathToPluginName.get(longestPluginPathFound));
				}
				
			}
		}
		
	}
	
	private static boolean isParentATestSuite(ProducerClass c, Map<String, List<ProducerClass>> map) {
		
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
		if(superClass.getType() == TestType.TEST_SUITE) {
			return true;
		} else {
			return false;
		}
		
	}
}
