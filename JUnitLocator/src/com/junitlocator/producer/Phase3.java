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
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.junitlocator.producer.ProducerClass.TestType;

public class Phase3 implements IScannerPhase {

	@Override
	public void run(InputStream is, String path, AnalysisContext context) throws FileNotFoundException, IOException {

		ClassNode cn = new ClassNode();
		try {		
			ClassReader cr = new ClassReader(is);
			cr.accept(cn, ClassReader.SKIP_DEBUG);
		} catch(Throwable t) {
			// Only print the warnings on the first phase.
			return;
		}
				
		handleClassNode(cn, path, context);
	}	
	
	@SuppressWarnings("unchecked")
	private static void handleClassNode(ClassNode cn, String path, AnalysisContext context) {

		ProducerClass c = context.getClassFromPath(path);
		if(c == null) {
			throw new RuntimeException("Could not find path in database: "+path);
		}		
		
		boolean isClassATestSuite = false;

		// If parent is a junit.framework.TestSuite, then we are a test suite.
		if(cn.superName.equals("junit/framework/TestSuite")) {
			isClassATestSuite = true;
		}
		
		// Detect the '@SuiteClasses' on test suite classes
		if(cn.visibleAnnotations != null && cn.visibleAnnotations.size() > 0) {
			List<AnnotationNode> annots = (List<AnnotationNode>)cn.visibleAnnotations;
			for(AnnotationNode an : annots) {
				
				if(an.desc == null || !an.desc.contains("org/junit/runners/Suite$SuiteClasses")) { continue; }				
				if(an.values == null) { continue; }
				
				isClassATestSuite = true;
			}
		}
		
		// Look classes that contain methods of the form: public static junit.framework.Test suite() { ... }
		if(cn.methods != null) {
			List<MethodNode> methods = cn.methods;
			for (int i = 0; i < methods.size(); ++i) {
				MethodNode method = methods.get(i);
				if(method.name.equals("suite") && method.desc != null && method.desc.equals("()Ljunit/framework/Test;") 
						&& (method.access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC) {					
					isClassATestSuite = true;
					break;
				}
			}
		}
				
		if(isClassATestSuite) {
			if(c.getType() == TestType.TEST_CASE) {
				System.err.println("Warning - I am converting test case to test suite: "+path);
			}
			c.setType(TestType.TEST_SUITE);
		}
		
		// We don't need to handle the innerClasses value of the class, here.
			
	}	
}
