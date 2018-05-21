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

import com.google.common.base.Objects;
import com.google.inject.Injector;
import eu.jgen.notes.dmw.lite.LangStandaloneSetup;
import java.io.File;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.xtext.validation.CheckMode;
import org.eclipse.xtext.validation.IResourceValidator;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.InputOutput;

@SuppressWarnings("all")
public class DMWCompiler {
  private static Injector injector;
  
  private List<String> dmwFilesList;
  
  private ResourceDescriptionsProvider resourceDescriptionsProvider;
  
  public static void main(final String[] args) {
    System.out.println("DMW Compiler, Version 0.1");
    DMWCompiler.injector = new LangStandaloneSetup().createInjectorAndDoEMFRegistration();
    DMWCompiler compiler = new DMWCompiler();
    int _size = ((List<String>)Conversions.doWrapArray(args)).size();
    boolean _equals = (_size == 2);
    if (_equals) {
      compiler.run(args[0], args[1]);
      return;
    }
    InputOutput.<String>println("Usage: java -jar dmw.jar <root path> <file name>");
  }
  
  public void run(final String rootPath, final String fragmentFileName) {
    if ((this.dmwFilesList == null)) {
      Vector<String> _vector = new Vector<String>();
      this.dmwFilesList = _vector;
    }
    this.findListOfDmwFiles(this.dmwFilesList, rootPath);
    this.resourceDescriptionsProvider = DMWCompiler.injector.<ResourceDescriptionsProvider>getInstance(ResourceDescriptionsProvider.class);
    final XtextResourceSet resourceSet = DMWCompiler.injector.<XtextResourceSet>getInstance(XtextResourceSet.class);
    for (final String item : this.dmwFilesList) {
      resourceSet.getResource(URI.createFileURI(item), true);
    }
    final Resource resource = this.findMatchingResource(resourceSet, fragmentFileName);
    if ((resource == null)) {
      InputOutput.<String>println("Specified file with the model fragment not found.");
      return;
    }
    final IResourceServiceProvider serviceProvider = DMWCompiler.injector.<IResourceServiceProvider>getInstance(IResourceServiceProvider.class);
    final IResourceValidator resourceValidator = serviceProvider.getResourceValidator();
    List<Issue> issues = resourceValidator.validate(resource, CheckMode.ALL, CancelIndicator.NullImpl);
    int _size = issues.size();
    boolean _equals = (_size == 0);
    if (_equals) {
      InputOutput.<String>println("Compilation completed without any issues.");
      return;
    }
    final Consumer<Issue> _function = (Issue issue) -> {
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
    issues.forEach(_function);
    int _size_1 = issues.size();
    String _plus = ("Compilation completed but some issues (" + Integer.valueOf(_size_1));
    String _plus_1 = (_plus + ") have been found.");
    InputOutput.<String>println(_plus_1);
  }
  
  private Resource findMatchingResource(final ResourceSet resourceSet, final String fragmentFileName) {
    final EList<Resource> list = resourceSet.getResources();
    for (final Resource resource : list) {
      String _lastSegment = resource.getURI().lastSegment();
      boolean _equals = Objects.equal(_lastSegment, fragmentFileName);
      if (_equals) {
        return resource;
      }
    }
    return null;
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
}
