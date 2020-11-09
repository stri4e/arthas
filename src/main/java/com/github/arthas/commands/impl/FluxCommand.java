package com.github.arthas.commands.impl;

import com.github.arthas.commands.ICommand;
import com.github.arthas.decorates.FluxResponsePayload;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class FluxCommand implements ICommand {

    private final FluxResponsePayload request;

    private final Type type;

    public FluxCommand(FluxResponsePayload decorateMono, Type type) {
        this.request = decorateMono;
        this.type = type;
    }

    @Override
    public Object execute(Method method, Object... params) {
        return this.request.response(this.type, method, params);
    }

}
