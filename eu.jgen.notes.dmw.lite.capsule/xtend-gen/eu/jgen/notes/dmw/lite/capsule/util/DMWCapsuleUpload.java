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
 */
package eu.jgen.notes.dmw.lite.capsule.util;

import com.ca.gen.jmmi.Ency;
import com.ca.gen.jmmi.EncyManager;
import com.ca.gen.jmmi.Model;
import com.ca.gen.jmmi.ModelManager;
import com.google.common.io.Files;
import com.google.inject.Injector;
import eu.jgen.notes.dmw.lite.LangStandaloneSetup;
import eu.jgen.notes.dmw.lite.capsule.proc.UploadDataModelFragment;
import eu.jgen.notes.dmw.lite.lang.YAnnotEntity;
import eu.jgen.notes.dmw.lite.lang.YAnnotRel;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

@SuppressWarnings("all")
public class DMWCapsuleUpload {
  private static String MAIN_LIB = "eu/jgen/notes/lib/dmw/lang.dmw";
  
  private static String LOCATION_EMPTY_GEN_MODEL = "C:\\eu.jgen.notes.dmw.lite.capsule.models\\empty.ief\\";
  
  private static String IEF0000 = "ief0000.dat";
  
  private static String IEF0700 = "ief0700.dat";
  
  private static String IEF1200 = "ief1200.dat";
  
  private static String IEF2511 = "ief2511.dat";
  
  private static Injector injector;
  
  private UploadDataModelFragment uploadDataModelFragment;
  
  private Model model;
  
  private Ency ency;
  
  private List<String> dmwFilesList;
  
  private ResourceDescriptionsProvider resourceDescriptionsProvider;
  
  public static void main(final String[] args) {
    System.out.println("DMW Capsule Upload Utility, Version 0.2");
    DMWCapsuleUpload.injector = new LangStandaloneSetup().createInjectorAndDoEMFRegistration();
    DMWCapsuleUpload capsuleUpload = new DMWCapsuleUpload();
    int _size = ((List<String>)Conversions.doWrapArray(args)).size();
    boolean _equals = (_size == 2);
    if (_equals) {
      capsuleUpload.run(args[0], args[1]);
      return;
    }
    InputOutput.<String>println("Usage:  java  -jar capup.jar  modelpath capsulepath ");
  }
  
  public String run(final String modelPath, final String capsulePath) {
    String _xblockexpression = null;
    {
      this.connectToModel(modelPath);
      if ((this.dmwFilesList == null)) {
        Vector<String> _vector = new Vector<String>();
        this.dmwFilesList = _vector;
      }
      this.findListOfDmwFiles(this.dmwFilesList, (capsulePath + "/src"));
      this.resourceDescriptionsProvider = DMWCapsuleUpload.injector.<ResourceDescriptionsProvider>getInstance(ResourceDescriptionsProvider.class);
      final XtextResourceSet resourceSet = DMWCapsuleUpload.injector.<XtextResourceSet>getInstance(XtextResourceSet.class);
      for (final String item : this.dmwFilesList) {
        resourceSet.getResource(URI.createFileURI(item), true);
      }
      final URL url = this.getClass().getClassLoader().getResource(DMWCapsuleUpload.MAIN_LIB);
      final String urlPath = url.getPath();
      final Resource resource = resourceSet.createResource(URI.createFileURI(urlPath));
      final boolean validationOKStatus = this.validateCapluseContents(resourceSet);
      if (validationOKStatus) {
        this.uploadDataModelFragment = DMWCapsuleUpload.injector.<UploadDataModelFragment>getInstance(UploadDataModelFragment.class);
        this.uploadDataModelFragment.connect(this.model);
        this.uploadEntityTypeDefinitions(resourceSet);
        this.uploadRelationships(resourceSet);
      } else {
        InputOutput.<String>println("Cannot continue until issues resolved.");
      }
      this.disconnectFromModel();
      _xblockexpression = InputOutput.<String>println("Upload completed.");
    }
    return _xblockexpression;
  }
  
  private void uploadRelationships(final XtextResourceSet resourceSet) {
    final Consumer<Resource> _function = (Resource element) -> {
      final Procedure1<EObject> _function_1 = (EObject item) -> {
        if ((item instanceof YAnnotRel)) {
          final YAnnotRel annotRel = ((YAnnotRel) item);
          this.uploadDataModelFragment.makeRelationships(annotRel);
        }
      };
      IteratorExtensions.<EObject>forEach(element.getAllContents(), _function_1);
    };
    resourceSet.getResources().forEach(_function);
  }
  
  private void uploadEntityTypeDefinitions(final XtextResourceSet resourceSet) {
    final Consumer<Resource> _function = (Resource element) -> {
      final Procedure1<EObject> _function_1 = (EObject item) -> {
        if ((item instanceof YAnnotEntity)) {
          final YAnnotEntity annotEntity = ((YAnnotEntity) item);
          this.uploadDataModelFragment.makeEntity(annotEntity);
        }
      };
      IteratorExtensions.<EObject>forEach(element.getAllContents(), _function_1);
    };
    resourceSet.getResources().forEach(_function);
  }
  
  private void findListOfDmwFiles(final List<String> list, final String path) {
    File dir = new File(path);
    File[] _listFiles = dir.listFiles();
    for (final File item : _listFiles) {
      boolean _isFile = item.isFile();
      if (_isFile) {
        boolean _endsWith = item.getName().endsWith(".dmw");
        if (_endsWith) {
          list.add(item.getPath());
        }
      } else {
        this.findListOfDmwFiles(list, item.getPath());
      }
    }
  }
  
  private void connectToModel(final String modelPath) {
    try {
      this.createEmptyModel(modelPath);
      this.ency = EncyManager.connectLocal(modelPath);
      this.model = ModelManager.open(this.ency, this.ency.getModelIds().get(0));
      String _name = this.model.getName();
      String _plus = ("Connected to local model: " + _name);
      System.out.println(_plus);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  public void createEmptyModel(final String path) {
    System.out.println(("Creating empty model at  " + path));
    File _file = new File((DMWCapsuleUpload.LOCATION_EMPTY_GEN_MODEL + DMWCapsuleUpload.IEF0000));
    File _file_1 = new File(((path + "\\") + DMWCapsuleUpload.IEF0000));
    this.copyDatFile(_file, _file_1);
    File _file_2 = new File((DMWCapsuleUpload.LOCATION_EMPTY_GEN_MODEL + DMWCapsuleUpload.IEF0700));
    File _file_3 = new File(((path + "\\") + DMWCapsuleUpload.IEF0700));
    this.copyDatFile(_file_2, _file_3);
    File _file_4 = new File((DMWCapsuleUpload.LOCATION_EMPTY_GEN_MODEL + DMWCapsuleUpload.IEF1200));
    File _file_5 = new File(((path + "\\") + DMWCapsuleUpload.IEF1200));
    this.copyDatFile(_file_4, _file_5);
    File _file_6 = new File((DMWCapsuleUpload.LOCATION_EMPTY_GEN_MODEL + DMWCapsuleUpload.IEF2511));
    File _file_7 = new File(((path + "\\") + DMWCapsuleUpload.IEF2511));
    this.copyDatFile(_file_6, _file_7);
    System.out.println("Creating empty model completed.");
  }
  
  public void copyDatFile(final File sourceFile, final File destinationFile) {
    try {
      Files.copy(sourceFile, destinationFile);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private void disconnectFromModel() {
    String _name = this.model.getName();
    String _plus = ("Disconnecting from local model: " + _name);
    String _plus_1 = (_plus + " ....");
    System.out.println(_plus_1);
    this.model.save();
    this.model.close();
    this.ency.disconnect();
  }
  
  private boolean validateCapluseContents(final XtextResourceSet resourceSet) {
    boolean validationOKStatus = true;
    int counterFailed = 0;
    final IResourceServiceProvider serviceProvider = DMWCapsuleUpload.injector.<IResourceServiceProvider>getInstance(IResourceServiceProvider.class);
    final IResourceValidator resourceValidator = serviceProvider.getResourceValidator();
    final EList<Resource> resources = resourceSet.getResources();
    int counter = resources.size();
    InputOutput.<String>println((("Validation of the capsule with " + Integer.valueOf(counter)) + " fragments starting..."));
    final Consumer<Resource> _function = (Resource element) -> {
      String _lastSegment = ((Resource) element).getURI().lastSegment();
      String _plus = ("\t" + _lastSegment);
      InputOutput.<String>println(_plus);
    };
    resources.forEach(_function);
    for (int i = 0; (i < resources.size()); i++) {
      {
        final Resource resource = resources.get(i);
        List<Issue> issues = resourceValidator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
        int _size = issues.size();
        boolean _notEquals = (_size != 0);
        if (_notEquals) {
          final Consumer<Issue> _function_1 = (Issue issue) -> {
            String _lastSegment = issue.getUriToProblem().lastSegment();
            String _plus = (_lastSegment + ", ");
            Severity _severity = issue.getSeverity();
            String _plus_1 = (_plus + _severity);
            String _plus_2 = (_plus_1 + ", \"");
            String _message = issue.getMessage();
            String _plus_3 = (_plus_2 + _message);
            String _plus_4 = (_plus_3 + "\", line=");
            Integer _lineNumber = issue.getLineNumber();
            String _plus_5 = (_plus_4 + _lineNumber);
            String _plus_6 = (_plus_5 + ", column=");
            Integer _column = issue.getColumn();
            String _plus_7 = (_plus_6 + _column);
            InputOutput.<String>println(_plus_7);
          };
          issues.forEach(_function_1);
          int _size_1 = issues.size();
          String _plus = ("Compilation completed with some issue(s) (" + Integer.valueOf(_size_1));
          String _plus_1 = (_plus + ") have been found.");
          InputOutput.<String>println(_plus_1);
          validationOKStatus = false;
          counterFailed++;
        }
      }
    }
    if (validationOKStatus) {
      InputOutput.<String>println((("Validation of the capsule with " + Integer.valueOf(counter)) + " fragment(s) completed."));
    } else {
      InputOutput.<String>println((("Validation of the capsule completed with " + Integer.valueOf(counterFailed)) + " fragments failing."));
    }
    return validationOKStatus;
  }
}
