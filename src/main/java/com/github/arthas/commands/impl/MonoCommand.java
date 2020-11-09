package com.github.arthas.commands.impl;

import com.github.arthas.commands.ICommand;
import com.github.arthas.decorates.MonoResponsePayload;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public final class MonoCommand implements ICommand {

    private final MonoResponsePayload request;

    private final Type type;

    public MonoCommand(MonoResponsePayload decorateMono, Type type) {
        this.request = decorateMono;
        this.type = type;
    }

    @Override
    public Object execute(Method method, Object... params) {
        return this.request.response(this.type, method, params);
    }

}
