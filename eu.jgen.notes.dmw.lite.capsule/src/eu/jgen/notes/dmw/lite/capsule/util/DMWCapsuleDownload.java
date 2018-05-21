/**
 * [The "BSD license"]
 * Copyright (c) 2016, JGen Notes
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions 
 *    and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *    and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS 
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package eu.jgen.notes.dmw.lite.capsule.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import com.ca.gen.jmmi.Ency;
import com.ca.gen.jmmi.EncyManager;
import com.ca.gen.jmmi.Model;
import com.ca.gen.jmmi.ModelManager;
import com.ca.gen.jmmi.exceptions.EncyException;
import com.ca.gen.jmmi.exceptions.ModelNotFoundException;
import com.ca.gen.jmmi.ids.ObjId;
import com.ca.gen.jmmi.schema.ObjTypeCode;
import com.google.inject.Injector;

import eu.jgen.notes.annot.desc.AnnotationStandaloneSetup;
import eu.jgen.notes.annot.desc.processor.AnnotationWorker;
import eu.jgen.notes.dmw.lite.capsule.proc.DMWCapsuleBuilderProcessor; 

/**
 * @author Marek Stankiewicz
 */
 
public class DMWCapsuleDownload {
 
	private static final String OPT_SUBDIRECTORY_NAME = "\\capsule";

	private Model model;
	private Ency ency;
	private Injector injector;
	private Map<String, String> options;

	public DMWCapsuleDownload(Map<String, String> options) {
		this.options = options;
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		System.out.println("DMW Capsule Download Utility, Version 0.3");
		Map<String, String> options = new HashMap<String, String>();
		if (args.length > 2) {
			displayHelp();
			return;
		}
		System.out.println("Starting...");
		options.put(DMWCapsuleBuilderProcessor.OPT_NOTES_DIRECTORY, args[0] + OPT_SUBDIRECTORY_NAME);
		options.put(DMWCapsuleBuilderProcessor.OPT_LOCAL_NAME,  Paths.get(args[0]).getFileName().toString());
		System.out.println("Connecting to " + System.getenv("GEN86") );
		options.put(DMWCapsuleBuilderProcessor.OPT_CAGEN_PATH, System.getenv("GEN86")) ;
		if(args.length > 1) {
			options.put(DMWCapsuleBuilderProcessor.OPT_PACKAGE_NAME, args[1]) ;
		} else {
			options.put(DMWCapsuleBuilderProcessor.OPT_PACKAGE_NAME, "eu.jgen.notes.dmw.lite.sample") ;
		}
		DMWCapsuleDownload downloadCapsule = new DMWCapsuleDownload(options);
		downloadCapsule.start(args[0]);
	}

	private static void displayHelp() {
		System.out.println("Usage:  java  -jar capdown.jar  modelpath  [packagename]");
	}

	private void start(String path) { 
		injector = new AnnotationStandaloneSetup().createInjectorAndDoEMFRegistration();
		try {
			ency = EncyManager.connectLocalForReadOnly(path);
			model = ModelManager.open(ency, ency.getModelIds().get(0));
			System.out.println("Connected to local model: " + model.getName());
			List<ObjId> list = model.getObjIds(ObjTypeCode.SUBJ);
			list.addAll(model.getObjIds(ObjTypeCode.ACBLKBSD));
			list.addAll(model.getObjIds(ObjTypeCode.RECIEFJ));
			AnnotationWorker worker = injector.getInstance(AnnotationWorker.class);
			worker.init(model, new DMWCapsuleBuilderProcessor(), options)
					.setSources(list).activate();
			model.close();
			ency.disconnect();
			System.out.println("Processing completed.");
		} catch (EncyException e) {
			System.out.println("ERROR:  " + e.getLocalizedMessage());
		} catch (ModelNotFoundException e) {
			System.out.println("ERROR:  " + e.getLocalizedMessage());
		}
		return;
	}

}
