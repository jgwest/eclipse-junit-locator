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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

/** 
 * This is the central 'manager' class for the producer phases. This class is response for loading the ZIP file to be analyzed, 
 * creating the analysis context, and passing those two between the various producer phases. 
 * 
 **/
public class FileScanner {
	
	public static void analyzeEclipseUpdateSite(File inputFile, OutputStream output) { 
		try {
			
			AnalysisContext context = new AnalysisContext();
			
			Phase1 phase1 = new Phase1();
			
			{
				ZipInputStream zis = new ZipInputStream(new FileInputStream(inputFile));
				recurseInsideZipFile("", zis,  phase1, context);
				zis.close();
				System.out.println("Phase 1 complete.");
				
			}
			
			{
				Phase2.run(context);
				System.out.println("Phase 2 complete.");
			}
			
			{
				Phase3 phase3 = new Phase3();
				ZipInputStream zis = new ZipInputStream(new FileInputStream(inputFile));
				recurseInsideZipFile("", zis,  phase3, context);
				zis.close();
				System.out.println("Phase 3 complete.");
			}
			
			{
				Phase4.run(context);
				System.out.println("Phase 4 complete.");
			}

			{
				Phase5 phase5 = new Phase5();
				ZipInputStream zis = new ZipInputStream(new FileInputStream(inputFile));
				recurseInsideZipFile("", zis, phase5, context);
				zis.close();
				System.out.println("Phase 5 complete.");
			}
			
			{
				Phase6.run(context);
				System.out.println("Phase 6 complete.");
			}
			
			{
				
				OutputStreamWriter osw = new OutputStreamWriter(output);
				ObjectMapper om = new ObjectMapper();	
				osw.write(om.writeValueAsString(context.getResult().get()));
				osw.close();
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unused")
	private static void recurseDirectory(File currFile, IScannerPhase phase, AnalysisContext context) throws IOException {
		File[] fileList = currFile.listFiles();
		if(fileList == null) { return; }
		
		for(File f : fileList) {
			if(f.isDirectory()) {
				recurseDirectory(f, phase, context);
			} else {
				processNonZipFile(f.getPath(), new FileInputStream(f), phase, context);
			}
		}
		
	}
	
	
	private static void recurseInsideZipFile(String pos, ZipInputStream zis, IScannerPhase phase, AnalysisContext context) throws IOException {
		
		ZipEntry ze;
		
		try {
			while(null != (ze = zis.getNextEntry())) {
				
				String path = pos+"/"+ze.getName();
				
				if(ze.isDirectory()) {
					continue;
				}
				
				if(ze.getName().toLowerCase().endsWith(".jar")) {
					
					recurseInsideZipFile(pos+"/"+ze.getName(), new ZipInputStream(zis), phase, context);
					
				} else {
					processNonZipFile(path, zis, phase, context);
				}
			}	
		} catch(ZipException zex) {
			if(phase instanceof Phase1) {
				// Only report bad zips in the first phase.
				System.err.println("* bad zip: "+pos+" ("+zex.getMessage()+")");
			}
		}
		
	}
	
	private static void processNonZipFile(String path, InputStream is, IScannerPhase phase, AnalysisContext context) throws IOException {
		String lpath = path.toLowerCase();
		
		if(lpath.endsWith(".jar")) {
			recurseInsideZipFile(path, new ZipInputStream(is), phase, context);
			return;
		}
		
		if(lpath.endsWith(".class")) {
			phase.run(is, path, context);
		}
	
		if(phase instanceof Phase1 && lpath.endsWith("meta-inf/manifest.mf")) {
			phase.run(is, path, context);
		}
	}

}
