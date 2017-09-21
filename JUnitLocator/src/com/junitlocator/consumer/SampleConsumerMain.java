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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

public class SampleConsumerMain {

	public static void main(String[] args) {


		TestClassDatabase db = new TestClassDatabase();
		
		try {
			db.initialize(ConsumerUtils.readResults(new FileInputStream("d:\\delme\\tests.json")));
			
			
			Optional<Object> o = db.getTestClassById("com.your.plugin.name", 
					"com.you.TestClass");
			
			JUnitTestCase tc = (JUnitTestCase)o.get();
			System.out.println(tc.getPathInZip());
			for(JUnitTestSuite ts : tc.getParents()) {
				
				System.out.println(ts);
				
				for(Object child : ts.getChildren()) {
					System.out.println("- "+child);
				}
				
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}

}
