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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.MultiANewArrayInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.junitlocator.producer.ProducerClass.TestType;

public class Phase5 implements IScannerPhase {

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

		ProducerClass testSuiteClass = context.getClassFromPath(path);
		if(testSuiteClass == null) {
			throw new RuntimeException("Could not find path in database: "+path);
		}		
		
		// This phase only operates on test suites
		if(testSuiteClass.getType() != TestType.TEST_SUITE) {
			return;
		}
		
		/** List of strings that may be test cases */
		Map<String /* possible test case name */, Boolean /*value not used, always true*/> stringsToCheck = new HashMap<>();
		
		// Detect the '@SuiteClasses' on test suite classes, this allows us to associate these classes with this suite
		if(cn.visibleAnnotations != null && cn.visibleAnnotations.size() > 0) {
			List<AnnotationNode> annots = (List<AnnotationNode>)cn.visibleAnnotations;
			for(AnnotationNode an : annots) {
				
				if(an.desc == null || !an.desc.contains("org/junit/runners/Suite$SuiteClasses")) { continue; }				
				if(an.values == null) { continue; }
				
				List<Object> values = (List<Object>)an.values;
				for(Object o  : values) {
					if(o instanceof ArrayList) {
						ArrayList<Object> alo = (ArrayList<Object>)o;
						for(Object t : alo) {
							
							if(t instanceof Type) {
								Type type = (Type)t;
								stringsToCheck.put(type.getClassName(), Boolean.TRUE);
							} else {
								throw new RuntimeException("Unexpected type: "+t);							
							}
						}
					}
				}
				
			}
		}		

		// Look for references to test cases in the methods, this allows us to associate these test cases with this suite
		List<MethodNode> methods = cn.methods;
		for (int i = 0; i < methods.size(); ++i) {
			MethodNode method = methods.get(i);
					
			InsnList list = method.instructions;

			for (int x = 0; x < list.size(); x++) {
				AbstractInsnNode abNode = list.get(x);
				handleAbNode(abNode, stringsToCheck, context);
			}
		}
		
		// Go through each of the possible test case strings 
		Map<String /*class name */, List<ProducerClass>> map = context.getMapNameToClass();
		for(String str : stringsToCheck.keySet()) {
			List<ProducerClass> list = map.get(str);
			if(list == null || list.size() == 0) { continue; }
						
			if(list.size() >= 1) {
				ProducerClass testcaseOrSuite = list.get(0);
				if(testcaseOrSuite.getType() != TestType.NONE && testcaseOrSuite != testSuiteClass) {
					// We have successfully identified a child test case or child test suite that is part of this parent test suite 
//					System.out.println(testSuiteClass.getSimpleClassName()+"    <-   "+testcaseOrSuite.getSimpleClassName()+"    "+testcaseOrSuite.type.name()+" "+testcaseOrSuite.getPluginName().orElse(null));
					testSuiteClass.getChildren().add(testcaseOrSuite);
				}

			}
			
		}
				
		// We don't need to handle the innerClasses value of the class, here.
		
	}

	private static void handleAbNode(AbstractInsnNode a, Map<String, Boolean> stringsToCheck, AnalysisContext context) {
		
		boolean handled = false;

		if (a instanceof FieldInsnNode) {
			// Visits a field instruction. A field instruction is an instruction that loads or stores the value of a field of an object.
			// This opcode is either GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
			handled = true;
		} else if (a instanceof FrameNode) {
			handled = true;
		} else if (a instanceof MethodInsnNode) {
			// Visits a method instruction. A method instruction is an instruction that invokes a method.
			//  This opcode is either INVOKEVIRTUAL, INVOKESPECIAL, INVOKESTATIC or INVOKEINTERFACE.
			MethodInsnNode min = (MethodInsnNode) a;
			
			if(min.desc != null && min.desc.contains("junit") && min.owner != null) {
				String ownerClass = min.owner.replace("/", ".");				
				stringsToCheck.put(ownerClass, Boolean.TRUE); 
			}

			handled = true;

		} else if (a instanceof LabelNode) {
			handled = true;
		} else if (a instanceof JumpInsnNode) {
			handled = true;
		} else if (a instanceof LdcInsnNode) {
			handled = true;
			LdcInsnNode load = (LdcInsnNode) a;

			Object cst = load.cst;

			if (cst instanceof Integer) {
				// ...
			} else if (cst instanceof Float) {
				// ...
			} else if (cst instanceof Long) {
				// ...
			} else if (cst instanceof Double) {
				// ...
			} else if (cst instanceof String) {
				// ...
			} else if (cst instanceof Type) {
				Type t = ((Type) cst);
				int sort = t.getSort();
				if (sort == Type.OBJECT) {
					stringsToCheck.put(t.getClassName(), Boolean.TRUE); // Already in proper format.
					// ...
				} else if (sort == Type.ARRAY) {
					// ...
				} else if (sort == Type.METHOD) {
					// ...
				} else {
					// throw an exception
				}
			} else if (cst instanceof Handle) {
				// ...
			} else {
				// throw an exception
			}

		} else if(a instanceof VarInsnNode) {
			// Visits a local variable instruction. A local variable instruction is an instruction that loads or stores the value of a local variable.
			// This opcode is either ILOAD, LLOAD, FLOAD, DLOAD, ALOAD, ISTORE, LSTORE, FSTORE, DSTORE, ASTORE or RET.
//			VarInsnNode vin = (VarInsnNode)a;
			handled = true;
			
		} else if(a instanceof TypeInsnNode) {
			TypeInsnNode tin = (TypeInsnNode)a;  
			// A type instruction is an instruction that takes the internal name of a class as parameter.
			// opcode - the opcode of the type instruction to be visited. This opcode is either NEW, ANEWARRAY, CHECKCAST or INSTANCEOF.
			if(tin.getOpcode() == 187) {
				String desc = tin.desc.replace("/", ".");
				stringsToCheck.put(desc, Boolean.TRUE);
			}
			
			handled = true;
			
		} else if(a instanceof InsnNode) {
			// Visits a zero operand instruction.
//			InsnNode in = (InsnNode)a;
			handled = true;
		} else if(a instanceof IntInsnNode) {
			handled = true;
			
		} else if(a instanceof IincInsnNode || a instanceof TableSwitchInsnNode || a instanceof LookupSwitchInsnNode 
				|| a instanceof MultiANewArrayInsnNode || a instanceof InvokeDynamicInsnNode) {
			handled = true;
		}

		if (!handled) {
			throw new RuntimeException("Unknown instruction type: "+a.getClass().getName());
		}
		
	}
	
}
