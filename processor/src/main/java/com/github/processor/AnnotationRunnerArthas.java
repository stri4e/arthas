package com.github.processor;

import com.github.processor.annotations.Arthas;
import com.github.processor.factories.*;
import com.github.processor.utils.SpringAnnotationSpecifications;
import com.github.processor.utils.SpringTypeSpecifications;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes(value = "com.github.processor.annotations.Arthas")
@SupportedSourceVersion(value = SourceVersion.RELEASE_11)
public class AnnotationRunnerArthas extends AbstractProcessor {

    private ProcessingEnvironment processingEnv;

    private List<RESTMethodsFactory> restMethodsFactories;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.processingEnv = processingEnv;
        this.restMethodsFactories = List.of(
                new GetMethodFactory(),
                new PostMethodFactory(),
                new PutMethodFactory(),
                new DeleteMethodFactory()
        );
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Arthas.class);
        elements.forEach(element -> {
            PackageElement packageElement = this.processingEnv.getElementUtils().getPackageOf(element);
            Arthas annotation = element.getAnnotation(Arthas.class);
            List<? extends Element> toMethods = element.getEnclosedElements();
            List<MethodSpec> methodsToCreation = toMethods.stream()
                    .flatMap(method -> this.restMethodsFactories.stream()
                            .map(factory -> factory.method(method, annotation))
                    ).filter(Objects::nonNull).collect(Collectors.toList());
            FieldSpec webClient = FieldSpec
                    .builder(SpringTypeSpecifications.webClient(), "client")
                    .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                    .addModifiers()
                    .build();
            TypeSpec requestService = TypeSpec
                    .classBuilder(String.format("%sImpl", element.getSimpleName()))
                    .addSuperinterface(element.asType())
                    .addAnnotation(SpringAnnotationSpecifications.component())
                    .addModifiers(Modifier.PUBLIC)
                    .addField(webClient)
                    .addMethod(ConstructorFactory.constructor(annotation))
                    .addMethods(methodsToCreation)
                    .build();
            JavaFile javaFile = JavaFile.builder(
                    String.format("%s.impl", packageElement.getQualifiedName()),
                    requestService
            ).build();
            System.out.println(javaFile);
            try {
                javaFile.writeTo(this.processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return true;
    }

}
