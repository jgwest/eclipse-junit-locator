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

/** JSON serializable representation of a test case that was found inside an update site ZIP. */
public class JUnitTestCaseJson {

	private int id;

	private String pathInZip;
	private String packageAndClassName;
	private String pluginName;

	private boolean abstractClass = false;
	
	private boolean containsTests = false;

	private List<Integer> parents = new ArrayList<>();

	public final int getId() {
		return id;
	}

	public final void setId(int id) {
		this.id = id;
	}

	public final String getPathInZip() {
		return pathInZip;
	}

	public final void setPathInZip(String pathInZip) {
		this.pathInZip = pathInZip;
	}

	public final String getPackageAndClassName() {
		return packageAndClassName;
	}

	public final void setPackageAndClassName(String packageAndClassName) {
		this.packageAndClassName = packageAndClassName;
	}

	public final String getPluginName() {
		return pluginName;
	}

	public final void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public List<Integer> getParents() {
		return parents;
	}
	
	public void setParents(List<Integer> parents) {
		this.parents = parents;
	}
	
	public boolean isContainsTests() {
		return containsTests;
	}
	
	public void setContainsTests(boolean containsTests) {
		this.containsTests = containsTests;
	}
	
	public boolean isAbstractClass() {
		return abstractClass;
	}
	
	public void setAbstractClass(boolean abstractClass) {
		this.abstractClass = abstractClass;
	}
}
