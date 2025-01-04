package sootUp.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.ClassHierarchyAnalysisAlgorithm;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.signatures.MethodSignature;
import sootup.core.typehierarchy.ViewTypeHierarchy;
import sootup.core.types.ClassType;
import sootup.core.types.VoidType;
import sootup.core.util.DotExporter;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaIdentifierFactory;
import sootup.java.core.views.JavaView;

public class BasicSetup {

  public static void main(String[] args) {
    // Create a AnalysisInputLocation, which points to a directory. All class files will be loaded
    // from the directory
    List<AnalysisInputLocation> inputLocations = new ArrayList<>();
    inputLocations.add(
            new JavaClassPathAnalysisInputLocation("/home/giossepi/eclipse-workspace/sootup/sootUp/test/src/test/resources/Callgraph/binary"));
    inputLocations.add(
            new JavaClassPathAnalysisInputLocation("/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar")); // add rt.jar

    JavaView view = new JavaView(inputLocations);

    // Get a MethodSignature
    ClassType classTypeA = view.getIdentifierFactory().getClassType("A");
    ClassType classTypeB = view.getIdentifierFactory().getClassType("B");
    MethodSignature entryMethodSignature =
            JavaIdentifierFactory.getInstance()
                    .getMethodSignature(
                            classTypeB,
                            JavaIdentifierFactory.getInstance()
                                    .getMethodSubSignature(
                                            "calc", VoidType.getInstance(), Collections.singletonList(classTypeA)));

    // Create type hierarchy and CHA
    final ViewTypeHierarchy typeHierarchy = new ViewTypeHierarchy(view);
    System.out.println(typeHierarchy.subclassesOf(classTypeA));
    CallGraphAlgorithm cha = new ClassHierarchyAnalysisAlgorithm(view);

    // Create CG by initializing CHA with entry method(s)
    CallGraph cg = cha.initialize(Collections.singletonList(entryMethodSignature));
    String dot_export = cg.exportAsDot();
    
    try(FileWriter writer = new FileWriter("callgraph.dot")){
    	writer.write(dot_export);
    }catch(IOException e) {
    	System.err.println("Error writing file: " + e.getMessage());
    }
    
    cg.callsFrom(entryMethodSignature).forEach(System.out::println);
  }


}