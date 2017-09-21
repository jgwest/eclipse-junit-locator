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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.junitlocator.producer.AnalysisContext.PluginEntry;
import com.junitlocator.producer.ProducerClass.TestType;
import com.junitlocator.utils.Path;

public class Phase1 implements IScannerPhase {

	public void run(InputStream is, String path, AnalysisContext context) throws FileNotFoundException, IOException {

		if(path.toLowerCase().endsWith(".class")) {
		
			ClassNode cn = new ClassNode();
			try {		
				ClassReader cr = new ClassReader(is);
				cr.accept(cn, ClassReader.SKIP_DEBUG);
			} catch(Throwable t) {
				System.err.println("* bad zip: "+path);
				return;
			}
					
			handleClassNode(cn, path, context);
		}

		// Identify plugin/OSGI bundle's MANIFEST.MF files
		if(path.toLowerCase().endsWith("/meta-inf/manifest.mf")) {
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String str;
			
			List<String> contents = new ArrayList<>();

			
			String bundleName = null;
			
			while (null != (str = br.readLine())) {
				contents.add(str);
			}

			// Do NOT close BufferedReader, here.
			
			// Find the name of the plug-in in the MANIFEST.MF
			// This is more complicated than it needs to be, because MANIFEST.MF files are wrapped at 72 chars
			for(int x = 0; x < contents.size(); x++) {
				str = contents.get(x);
				if(str.contains("Bundle-SymbolicName:")/* || str.contains("Bundle-Name:") */) {
					
					String fullLine = str;
					inner_for: for(int y = x+1; y < contents.size(); y++) {
						str = contents.get(y);
						
						if(str.startsWith(" ")) {
							fullLine += str.trim();
						} else {
							break inner_for;
						}
					}
					
					fullLine = fullLine.substring(fullLine.indexOf(":")+1).trim();
					
					if(fullLine.contains(";")) {
						fullLine = fullLine.substring(0, fullLine.indexOf(";"));
					}

					bundleName = fullLine;
					
				}
			}

			if(bundleName != null) {
				
				String pluginPath = path.substring(0, path.toLowerCase().indexOf("/meta-inf/manifest.mf"));
				context.getPluginList().add( new PluginEntry(bundleName, new Path(pluginPath)));
			}
			
		}
	}	
	
	// Add all classes to the database, and flag concrete test cases
	@SuppressWarnings("unchecked")
	private static void handleClassNode(ClassNode cn, String path, AnalysisContext context) {
		
		ProducerClass c = new ProducerClass(cn.name, cn.superName, path, (cn.access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT);
		context.addClass(c.getFullClassName(), path, c);

		boolean isClassATestCase = false;

		// JUnit 3: Super class matches *.junit.TestCase
		if(cn.superName.contains("TestCase") && cn.superName.contains("junit")) {
			isClassATestCase = true;
		}

		if(!isClassATestCase) {
			
			// Look through the method annotations for JUnit 4 @Test annotations
		
			List<MethodNode> methods = cn.methods;
			outer_for: for (int i = 0; i < methods.size(); ++i) {
				MethodNode method = methods.get(i);
				
				List<AnnotationNode> annots = method.visibleAnnotations;
				if(annots != null) {
					for(AnnotationNode an : annots) {
						if(an.desc == null) { continue; }
						
						if(an.desc.contains("org/junit/Test")) {
							isClassATestCase = true;
							break outer_for;
						}
						
					}
				}			
				
			}
		}
		
		if(isClassATestCase) {
			c.setType(TestType.TEST_CASE);
			c.setContainsTests(true);
		}
		
		// We don't need to handle the innerClasses value of the class, here.
			
	}
}
