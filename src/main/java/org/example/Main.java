package org.example;

import com.sun.source.util.JavacTask;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import org.eclipse.jdt.core.JDTCompilerAdapter;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompiler;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Completion;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    public static void main(String[] args) throws IOException {
        JavaCompiler compiler;
        if ("true".equals(System.getProperty("usejavac"))) {
            compiler = ToolProvider.getSystemJavaCompiler();
            System.out.println("Using javac compiler");
        } else {
            compiler = new EclipseCompiler();
            System.out.println("Using eclipse compiler");
        }
        DiagnosticListener<? super JavaFileObject> errors = (err) -> {
        };
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(errors, Locale.getDefault(),
                Charset.defaultCharset());
        Iterable<? extends JavaFileObject> filesToCompile = fileManager.getJavaFileObjectsFromFiles(List.of(new File(
                        "./src/main/java/org/example/BaseClass.java"),
                new File(
                        "./src/main/java/org/example/SubClass.java")));
        JavaCompiler.CompilationTask task = compiler.getTask(new OutputStreamWriter(System.err), fileManager, errors,
                List.of("-sourcepath", "./src/main/java"), List.of(), filesToCompile);
        AtomicBoolean foundM1 = new AtomicBoolean();
        task.setProcessors(List.of(new AbstractProcessor() {

            ProcessingEnvironment processingEnv;

            @Override
            public Set<String> getSupportedAnnotationTypes() {
                return Set.of("org.example.Marker");
            }

            @Override
            public void init(ProcessingEnvironment processingEnv) {
                this.processingEnv = processingEnv;
            }

            @Override
            public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
                for (TypeElement annotation : annotations) {
                    for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                        System.out.println("ANNOTATED ELEMENT " + element);
                        for (Element element1 : processingEnv.getElementUtils().getAllMembers((TypeElement) element)) {
                            System.out.println("MEMBER ELEMENT " + element1.getSimpleName());
                            if ("m1".equals(element1.getSimpleName().toString())) {
                                foundM1.set(true);
                            }
                        }
                    }
                }
                return true;
            }
        }));
        task.call();
        if (foundM1.get()) {
            System.out.println("ALL FINE!");
        } else {
            System.err.println("INHERITED METHOD \"m1\" NOT FOUND!");
        }
    }
}
