package com.github.arthas.commands;

import java.lang.reflect.Method;

public interface ICommand {

    Object execute(Method method, Object... params);

}
