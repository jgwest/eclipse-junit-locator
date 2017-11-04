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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/** Phases of the producer that are required to analyze ZIP file contents must implement this interface. */
public interface IScannerPhase {

	/**
	 * 
	 * @param is Input stream of a file (usually a class file) to be analyzed
	 * @param path The path of the file under analysis.
	 * @param context The current state of the producer, this is both input and output.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void run(InputStream is, String path, AnalysisContext context) throws FileNotFoundException, IOException;
}
