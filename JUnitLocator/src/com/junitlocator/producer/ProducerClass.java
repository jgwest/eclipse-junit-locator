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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.junitlocator.utils.Path;

public class ProducerClass {

	final String fullClassName;

	final String superClassName;
	
	final Path path;
	
	final boolean abstractClass;
	
	boolean containsTests = false;
	
	private Optional<Integer> finalId = Optional.empty();
	
	Optional<String> pluginName = Optional.empty();
	
	enum TestType { NONE, TEST_CASE, TEST_SUITE}
	
	private TestType type = TestType.NONE;
	
	private final List<ProducerClass> children = new ArrayList<ProducerClass>();
	
	public ProducerClass(String fullClassName, String superClassName, String path, boolean abstractClass) {
		this.fullClassName = fullClassName.replace("/", ".");
		this.superClassName = superClassName.replace("/", ".");
		this.path = new Path(path);
		this.abstractClass = abstractClass;
	}
	
	public String getFullClassName() {
		return fullClassName;
	}
	
	public TestType getType() {
		return type;
	}
	
	public void setType(TestType type) {
		this.type = type;
	}
	
	public String getSimpleClassName() {
		int lastIndex = fullClassName.lastIndexOf(".");
		if(lastIndex != -1) {
			return fullClassName.substring(lastIndex+1);
		} else {
			return fullClassName;
		}
	}
	
	public Optional<String> getPluginName() {
		return pluginName;
	}
	
	public Path getPath() {
		return path;
	}
	
	public void setPluginName(String pluginName) {
		this.pluginName = Optional.of(pluginName);
	}
	
	
	public List<ProducerClass> getChildren() {
		return children;
	}
	
	public Optional<Integer> getFinalId() {
		return finalId;
	}
	
	public void setFinalId(Integer finalId) {
		this.finalId = Optional.of(finalId);
	}
	
	public void setContainsTests(boolean containsTests) {
		this.containsTests = containsTests;
	}
	
	public boolean isContainsTests() {
		return containsTests;
	}
	
	public boolean isAbstractClass() {
		return abstractClass;
	}
	
}
