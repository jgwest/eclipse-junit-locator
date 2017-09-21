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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.junitlocator.json.ResultsFileJson;
import com.junitlocator.utils.Path;

public class AnalysisContext {

	private final Map<String, List<ProducerClass>> mapNameToClass = new HashMap<>();
	private final Map<Path, ProducerClass> mapPathToClass = new HashMap<>();
	
//	private final List<Path> pluginList = new ArrayList<Path>();
	private final List<PluginEntry> pluginList = new ArrayList<>();
	
	private Optional<ResultsFileJson> result = Optional.empty();
	
	public AnalysisContext() {
	}
	
	public void addClass(String className, String path, ProducerClass c) {
		Path p = new Path(path);
		ProducerClass cp = mapPathToClass.get(p);
		if(cp == null) {
			mapPathToClass.put(p, c);
		} else {
			throw new RuntimeException("There should be no dupes");
		}
		
		List<ProducerClass> l = mapNameToClass.get(className);
		if(l == null) {
			l = new ArrayList<>();
			mapNameToClass.put(className, l);
		}
		l.add(c);
		
	}
	
	public Map<String, List<ProducerClass>> getMapNameToClass() {
		return mapNameToClass;
	}
	
	public ProducerClass getClassFromPath(String path) {
		Path p = new Path(path);
		return mapPathToClass.get(p);
	}
	
//	public List<Path> getPluginList() {
//		return pluginList;
//	}

	public List<PluginEntry> getPluginList() {
		return pluginList;
	}
	
	public Optional<ResultsFileJson> getResult() {
		return result;
	}
	
	public void setResult(ResultsFileJson result) {
		this.result = Optional.of(result);
	}
	
	public static class PluginEntry {
		private final String name;
		private final Path path;
		
		public PluginEntry(String name, Path path) {
			this.name = name;
			this.path = path;
		}
		
		public String getName() {
			return name;
		}
		
		public Path getPath() {
			return path;
		}
		
	}
}
