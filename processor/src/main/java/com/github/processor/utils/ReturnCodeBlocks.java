package com.github.processor.utils;

import com.github.processor.code.RequestBodyCodeBlock;
import com.github.processor.code.RequestHeadersCodeBlock;
import com.github.processor.code.URICodeBlock;
import com.github.processor.mappers.BodyToPublisher;
import com.squareup.javapoet.CodeBlock;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.LinkedList;
import java.util.List;

public class ReturnCodeBlocks {

    private final List<CodeBlock> returnStatement = new LinkedList<>();

    public static ReturnCodeBlocks builder() {
        return new ReturnCodeBlocks();
    }

    public ReturnCodeBlocks returnCodeBlock() {
        this.returnStatement.add(CodeBlock.of("return this.client.post()"));
        return this;
    }

    public ReturnCodeBlocks uriCodeBlock() {
        this.returnStatement.add(URICodeBlock.uri());
        return this;
    }

    public ReturnCodeBlocks bodyCodeBlock(List<? extends VariableElement> parameters) {
        this.returnStatement.add(RequestBodyCodeBlock.body(parameters));
        return this;
    }

    public ReturnCodeBlocks httpAttributesCodeBlock(CodeBlock httpAttributes) {
        this.returnStatement.add(httpAttributes);
        return this;
    }

    public ReturnCodeBlocks httpHeadersCodeBlock(CodeBlock httpHeaders) {
        this.returnStatement.add(httpHeaders);
        return this;
    }

    public ReturnCodeBlocks httpCookiesCodeBlock(CodeBlock httpCookies) {
        this.returnStatement.add(httpCookies);
        return this;
    }

    public ReturnCodeBlocks requestAcceptCodeBlock(List<String> consumes) {
        if (consumes.size() != 0) {
            this.returnStatement.add(RequestHeadersCodeBlock.requestAccept(consumes));
        }
        return this;
    }

    public ReturnCodeBlocks retrieveCodeBlock() {
        this.returnStatement.add(CodeBlock.of(".retrieve()"));
        return this;
    }

    public ReturnCodeBlocks bodyToPublisherCodeBlock(TypeMirror methodReturnType) {
        this.returnStatement.add(BodyToPublisher.bodyToPublisher(methodReturnType));
        return this;
    }

    public CodeBlock build() {
        return CodeBlock.join(this.returnStatement, "$Z");
    }

}
