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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/** Main entry point for CLI utility. This class will analyze an Eclipse update site ZIP, and 
 * generate a JSON file contain test cases/suites found inside. */
public class ProducerMain {

	public static void main(String[] args) {		
		try {

			if(args.length != 2) {
				System.out.println("Arguments required: (update site zip) (path to output json file)");
				return;
				
			}
			
			FileScanner.analyzeEclipseUpdateSite(new File(args[0]), new FileOutputStream(new File(args[1])));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
