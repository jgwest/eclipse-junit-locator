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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SampleConsumerMain2 {

	public static void main(String[] args) {


		TestClassDatabase db = new TestClassDatabase();
		
		try {
			db.initialize(ConsumerUtils.readResults(new FileInputStream("c:\\delme\\out.json")));

			List<String> testsRun = new ArrayList<String>();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("c:\\delme\\tests-run.txt"))));
			String str;
			while(null != (str = br.readLine())) {

				testsRun.add(str);
				
//				String name = str.trim();
//				
//				boolean found = false;
//				for(TestCase tc : db.getTestCases()) {
//					if(tc.getPackageAndClassName().equals(name)) {
//						found = true;
//						break;
//					}
//					
//				}
//				
//				if(!found) {
//				}
//				
//				if(!found && !name.contains("cftools")) {
//					System.out.println(name);
//				}
		
			}
			br.close();
			
			for(JUnitTestSuite ts : db.getTestSuites()) {
				
				if(!ts.isContainsTests()) {
					continue;
				}
				
				if(ts.isAbstractClass()) {
					continue;
				}

				boolean match = false;
				for(String testRun : testsRun) {
					if(ts.getPackageAndClassName().equals(testRun)) {
						match = true;
						break;
					}
				}
				
				if(!match && !isFilteredOut(ts.getPackageAndClassName())) {
					System.out.println(ts.getPackageAndClassName());
				}

			}
			
			for(int x = 0; x < 10; x++) {
				System.out.println();
			}
			
			for(JUnitTestCase tc : db.getTestCases()) {
				
				if(!tc.isContainsTests()) {
					continue;
				}
				
				if(tc.isAbstractClass()) {
					continue;
				}

				boolean match = false;
				for(String testRun : testsRun) {
					if(tc.getPackageAndClassName().equals(testRun)) {
						match = true;
						break;
					}
				}
				
				if(!match && !isFilteredOut(tc.getPackageAndClassName()) ) {
					System.out.println(tc.getPackageAndClassName());
				}
				
				
				
			}
			
			
			
			
//			Optional<Object> o = db.getTestClassById("com.your.plugin.name", 
//					"com.you.TestClass");
//			
//			TestCase tc = (TestCase)o.get();
//			System.out.println(tc.getPathInZip());
//			for(TestSuite ts : tc.getParents()) {
//				
//				System.out.println(ts);
//				
//				for(Object child : ts.getChildren()) {
//					System.out.println("- "+child);
//				}
//				
//			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private static boolean isFilteredOut(String str) {
		if(str.startsWith("org.eclipse") || str.contains("jaxrpc") || str.contains("axis") || str.contains(".performance.") 
				|| str.contains(".perf.")) {
			return true;
		}
		
		return false;
	}
}
