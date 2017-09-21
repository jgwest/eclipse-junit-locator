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
import java.util.List;

import com.junitlocator.utils.Path;

public class JUnitTestSuite {

	private Path pathInZip;
	private String packageAndClassName;
	private String pluginName;

	private boolean containsTests = false;
	
	private boolean abstractClass = false;
	
	private List<JUnitTestSuite> parents = new ArrayList<JUnitTestSuite>();
	
	private List<Object> children = new ArrayList<>();
	
	public final String getPathInZip() {
		return pathInZip.get();
	}

	public final void setPathInZip(String pathInZip) {
		this.pathInZip = new Path(pathInZip);
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

	public List<JUnitTestSuite> getParents() {
		return parents;
	}
	
	public List<Object> getChildren() {
		return children;
	}
	
	@Override
	public String toString() {
		return packageAndClassName+" in "+pluginName;
	}

	public void setContainsTests(boolean containsTests) {
		this.containsTests = containsTests;
	}
	
	public boolean isContainsTests() {
		return containsTests;
	}
	
	public void setAbstractClass(boolean abstractClass) {
		this.abstractClass = abstractClass;
	}
	
	public boolean isAbstractClass() {
		return abstractClass;
	}
}
