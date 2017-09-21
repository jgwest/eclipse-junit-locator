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

package com.junitlocator.json;

import java.util.ArrayList;
import java.util.List;

public class ResultsFileJson {
	private List<JUnitTestCaseJson> testCases = new ArrayList<>();
	private List<JUnitTestSuiteJson> testSuites = new ArrayList<>();

	public final List<JUnitTestCaseJson> getTestCases() {
		return testCases;
	}

	public final void setTestCases(List<JUnitTestCaseJson> testCases) {
		this.testCases = testCases;
	}

	public final List<JUnitTestSuiteJson> getTestSuites() {
		return testSuites;
	}

	public final void setTestSuites(List<JUnitTestSuiteJson> testSuites) {
		this.testSuites = testSuites;
	}

}
