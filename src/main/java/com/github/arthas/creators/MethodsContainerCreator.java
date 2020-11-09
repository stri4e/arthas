package com.github.arthas.creators;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arthas.annotations.*;
import com.github.arthas.commands.ICommand;
import com.github.arthas.commands.impl.FluxCommand;
import com.github.arthas.commands.impl.MonoCommand;
import com.github.arthas.decoders.Decoder;
import com.github.arthas.decorates.FluxResponsePayload;
import com.github.arthas.decorates.MonoResponsePayload;
import com.github.arthas.encoders.Encoder;
import com.github.arthas.encoders.imp.DefaultEncoder;
import com.github.arthas.encoders.imp.JacksonEncoder;
import com.github.arthas.factory.IDecoderFactory;
import com.github.arthas.factory.IDecoratorsFactory;
import com.github.arthas.factory.IEncoderFactory;
import com.github.arthas.utils.ReflectionUtils;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.internal.StringUtil;
import reactor.netty.http.client.HttpClient;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class MethodsContainerCreator {

    private static final String MONO_NAME = "reactor.core.publisher.Mono";

    private static final String FLUX_NAME = "reactor.core.publisher.Flux";

    private final IDecoderFactory decoderFactory;

    private final IEncoderFactory encoderFactory;

    private final IDecoratorsFactory decoratorsFactory;

    private final HttpClient client;

    private final ObjectMapper objectMapper;

    public MethodsContainerCreator(IDecoderFactory decoderFactory,
                                   IEncoderFactory encoderFactory,
                                   IDecoratorsFactory decoratorsFactory,
                                   HttpClient client,
                                   ObjectMapper objectMapper) {
        this.decoderFactory = decoderFactory;
        this.encoderFactory = encoderFactory;
        this.decoratorsFactory = decoratorsFactory;
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public Map<String, ICommand> create(Method[] methods) {
        String voidTypeName = Void.class.getName();
        String strTypeName = String.class.getName();
        Map<String, ICommand> commands = new HashMap<>();
        for (Method method : methods) {
            Object[] params = method.getParameters();
            if (method.isAnnotationPresent(Get.class)) {
                Get get = method.getAnnotation(Get.class);
                String patter = get.path();
                String name = ReflectionUtils.returnTypeName(method);
                Type type = ReflectionUtils.fetchGenericType(method);
                if (FLUX_NAME.equals(name)) {
                    Decoder decoder;
                    if (strTypeName.equals(type.getTypeName())) {
                        decoder = this.decoderFactory.doDefaultDecoder();
                    } else {
                        decoder = this.decoderFactory.doJsonDecoder(this.objectMapper);
                    }
                    Encoder encoder = new DefaultEncoder();
                    FluxResponsePayload decoratorGet = this.decoratorsFactory.doFluxObj(
                            this.client, decoder, encoder, HttpMethod.GET, patter
                    );
                    commands.put(method.toString(), new FluxCommand(decoratorGet, type));
                }
                if (MONO_NAME.equals(name)) {
                    Encoder encoder = new DefaultEncoder();
                    if (strTypeName.equals(type.getTypeName())) {
                        Decoder decoder = this.decoderFactory.doDefaultDecoder();
                        MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoObj(
                                this.client, decoder, encoder, HttpMethod.GET, patter
                        );
                        commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                    } else {
                        if (voidTypeName.equals(type.getTypeName())) {
                            MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoVoid(
                                    this.client, encoder, HttpMethod.GET, patter
                            );
                            commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                        } else {
                            Decoder decoder = this.decoderFactory.doJsonDecoder(this.objectMapper);
                            MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoObj(
                                    this.client, decoder, encoder, HttpMethod.GET, patter
                            );
                            commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                        }
                    }
                }
            }
            if (method.isAnnotationPresent(Post.class)) {
                Post post = method.getAnnotation(Post.class);
                String patter = post.path();
                String name = ReflectionUtils.returnTypeName(method);
                Type type = ReflectionUtils.fetchGenericType(method);
                String bodyTypeName = ReflectionUtils.bodyTypeName(method.getParameterAnnotations(), params);
                if (FLUX_NAME.equals(name)) {
                    Decoder decoder;
                    if (strTypeName.equals(type.getTypeName())) {
                        decoder = this.decoderFactory.doDefaultDecoder();
                    } else {
                        decoder = this.decoderFactory.doJsonDecoder(this.objectMapper);
                    }
                    Encoder encoder;
                    if (!StringUtil.isNullOrEmpty(bodyTypeName)) {
                        if (strTypeName.equals(bodyTypeName)) {
                            encoder = new DefaultEncoder();
                        } else {
                            encoder = new JacksonEncoder(this.objectMapper);
                        }
                    } else {
                        encoder = new DefaultEncoder();
                    }
                    FluxResponsePayload decoratorGet = this.decoratorsFactory.doFluxObj(
                            this.client, decoder, encoder, HttpMethod.POST, patter
                    );
                    commands.put(method.toString(), new FluxCommand(decoratorGet, type));
                }
                if (MONO_NAME.equals(name)) {
                    Decoder decoder;
                    if (strTypeName.equals(type.getTypeName())) {
                        decoder = this.decoderFactory.doDefaultDecoder();
                        Encoder encoder;
                        if (!StringUtil.isNullOrEmpty(bodyTypeName)) {
                            if (strTypeName.equals(bodyTypeName)) {
                                encoder = new DefaultEncoder();
                            } else {
                                encoder = new JacksonEncoder(this.objectMapper);
                            }
                        } else {
                            encoder = new DefaultEncoder();
                        }
                        MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoObj(
                                this.client, decoder, encoder, HttpMethod.POST, patter
                        );
                        commands.put(method.toString(), new MonoCommand(decoratorGet, type));

                    } else {
                        if (voidTypeName.equals(type.getTypeName())) {
                            Encoder encoder;
                            if (!StringUtil.isNullOrEmpty(bodyTypeName)) {
                                if (strTypeName.equals(bodyTypeName)) {
                                    encoder = new DefaultEncoder();
                                } else {
                                    encoder = new JacksonEncoder(this.objectMapper);
                                }
                            } else {
                                encoder = new DefaultEncoder();
                            }
                            MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoVoid(
                                    this.client, encoder, HttpMethod.POST, patter
                            );
                            commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                        } else {
                            decoder = this.decoderFactory.doJsonDecoder(this.objectMapper);
                            Encoder encoder;
                            if (!StringUtil.isNullOrEmpty(bodyTypeName)) {
                                if (strTypeName.equals(bodyTypeName)) {
                                    encoder = new DefaultEncoder();
                                } else {
                                    encoder = new JacksonEncoder(this.objectMapper);
                                }
                            } else {
                                encoder = new DefaultEncoder();
                            }
                            MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoObj(
                                    this.client, decoder, encoder, HttpMethod.POST, patter
                            );
                            commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                        }
                    }
                }
            }
            if (method.isAnnotationPresent(Put.class)) {
                Put put = method.getAnnotation(Put.class);
                String patter = put.path();
                String name = ReflectionUtils.returnTypeName(method);
                Type type = ReflectionUtils.fetchGenericType(method);
                String bodyTypeName = ReflectionUtils.bodyTypeName(method.getParameterAnnotations(), params);
                if (FLUX_NAME.equals(name)) {
                    Decoder decoder;
                    if (strTypeName.equals(type.getTypeName())) {
                        decoder = this.decoderFactory.doDefaultDecoder();
                    } else {
                        decoder = this.decoderFactory.doJsonDecoder(this.objectMapper);
                    }
                    Encoder encoder;
                    if (!StringUtil.isNullOrEmpty(bodyTypeName)) {
                        if (strTypeName.equals(bodyTypeName)) {
                            encoder = new DefaultEncoder();
                        } else {
                            encoder = new JacksonEncoder(this.objectMapper);
                        }
                    } else {
                        encoder = new DefaultEncoder();
                    }
                    FluxResponsePayload decoratorGet = this.decoratorsFactory.doFluxObj(
                            this.client, decoder, encoder, HttpMethod.PUT, patter
                    );
                    commands.put(method.toString(), new FluxCommand(decoratorGet, type));
                }
                if (MONO_NAME.equals(name)) {
                    Decoder decoder;
                    if (strTypeName.equals(type.getTypeName())) {
                        decoder = this.decoderFactory.doDefaultDecoder();
                        Encoder encoder;
                        if (!StringUtil.isNullOrEmpty(bodyTypeName)) {
                            if (strTypeName.equals(bodyTypeName)) {
                                encoder = new DefaultEncoder();
                            } else {
                                encoder = new JacksonEncoder(this.objectMapper);
                            }
                        } else {
                            encoder = new DefaultEncoder();
                        }
                        MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoObj(
                                this.client, decoder, encoder, HttpMethod.PUT, patter
                        );
                        commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                    } else {
                        if (voidTypeName.equals(type.getTypeName())) {
                            Encoder encoder;
                            if (!StringUtil.isNullOrEmpty(bodyTypeName)) {
                                if (strTypeName.equals(bodyTypeName)) {
                                    encoder = new DefaultEncoder();
                                } else {
                                    encoder = new JacksonEncoder(this.objectMapper);
                                }
                            } else {
                                encoder = new DefaultEncoder();
                            }
                            MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoVoid(
                                    this.client, encoder, HttpMethod.PUT, patter
                            );
                            commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                        } else {
                            decoder = this.decoderFactory.doJsonDecoder(this.objectMapper);
                            Encoder encoder;
                            if (!StringUtil.isNullOrEmpty(bodyTypeName)) {
                                if (strTypeName.equals(bodyTypeName)) {
                                    encoder = new DefaultEncoder();
                                } else {
                                    encoder = new JacksonEncoder(this.objectMapper);
                                }
                            } else {
                                encoder = new DefaultEncoder();
                            }
                            MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoObj(
                                    this.client, decoder, encoder, HttpMethod.PUT, patter
                            );
                            commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                        }
                    }
                }
            }
            if (method.isAnnotationPresent(Delete.class)) {
                Delete delete = method.getAnnotation(Delete.class);
                String patter = delete.path();
                String name = ReflectionUtils.returnTypeName(method);
                Type type = ReflectionUtils.fetchGenericType(method);
                if (FLUX_NAME.equals(name)) {
                    Decoder decoder;
                    if (strTypeName.equals(type.getTypeName())) {
                        decoder = this.decoderFactory.doDefaultDecoder();
                    } else {
                        decoder = this.decoderFactory.doJsonDecoder(this.objectMapper);
                    }
                    Encoder encoder = new DefaultEncoder();
                    FluxResponsePayload decoratorGet = this.decoratorsFactory.doFluxObj(
                            this.client, decoder, encoder, HttpMethod.DELETE, patter
                    );
                    commands.put(method.toString(), new FluxCommand(decoratorGet, type));
                }
                if (MONO_NAME.equals(name)) {
                    Decoder decoder;
                    if (strTypeName.equals(type.getTypeName())) {
                        decoder = this.decoderFactory.doDefaultDecoder();
                    } else {
                        decoder = this.decoderFactory.doJsonDecoder(this.objectMapper);
                    }
                    Encoder encoder = new DefaultEncoder();
                    MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoObj(
                            this.client, decoder, encoder, HttpMethod.DELETE, patter
                    );
                    commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                }
            }
            if (method.isAnnotationPresent(Head.class)) {
                Head head = method.getAnnotation(Head.class);
                String patter = head.path();
                String name = ReflectionUtils.returnTypeName(method);
                Type type = ReflectionUtils.fetchGenericType(method);
                if (FLUX_NAME.equals(name)) {
                    throw new RuntimeException("Is it not supported in method head.");
                }
                if (MONO_NAME.equals(name)) {
                    Encoder encoder = new DefaultEncoder();
                    MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoVoid(
                            this.client, encoder, HttpMethod.HEAD, patter
                    );
                    commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                }
            }
            if (method.isAnnotationPresent(Options.class)) {
                Options options = method.getAnnotation(Options.class);
                String patter = options.path();
                String name = ReflectionUtils.returnTypeName(method);
                Type type = ReflectionUtils.fetchGenericType(method);
                if (FLUX_NAME.equals(name)) {
                    Decoder decoder;
                    if (strTypeName.equals(type.getTypeName())) {
                        decoder = this.decoderFactory.doDefaultDecoder();
                    } else {
                        decoder = this.decoderFactory.doJsonDecoder(this.objectMapper);
                    }
                    Encoder encoder = new DefaultEncoder();
                    FluxResponsePayload decoratorGet = this.decoratorsFactory.doFluxObj(
                            this.client, decoder, encoder, HttpMethod.OPTIONS, patter
                    );
                    commands.put(method.toString(), new FluxCommand(decoratorGet, type));
                }
                if (MONO_NAME.equals(name)) {
                    Encoder encoder = new DefaultEncoder();
                    if (strTypeName.equals(type.getTypeName())) {
                        Decoder decoder = this.decoderFactory.doDefaultDecoder();
                        MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoObj(
                                this.client, decoder, encoder, HttpMethod.GET, patter
                        );
                        commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                    } else {
                        if (voidTypeName.equals(type.getTypeName())) {
                            MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoVoid(
                                    this.client, encoder, HttpMethod.OPTIONS, patter
                            );
                            commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                        } else {
                            Decoder decoder = this.decoderFactory.doJsonDecoder(this.objectMapper);
                            MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoObj(
                                    this.client, decoder, encoder, HttpMethod.OPTIONS, patter
                            );
                            commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                        }
                    }
                }
            }
            if (method.isAnnotationPresent(Patch.class)) {
                Patch patch = method.getAnnotation(Patch.class);
                String patter = patch.path();
                String name = ReflectionUtils.returnTypeName(method);
                Type type = ReflectionUtils.fetchGenericType(method);
                String bodyTypeName = ReflectionUtils.bodyTypeName(method.getParameterAnnotations(), params);
                if (FLUX_NAME.equals(name)) {
                    Decoder decoder;
                    if (strTypeName.equals(type.getTypeName())) {
                        decoder = this.decoderFactory.doDefaultDecoder();
                    } else {
                        decoder = this.decoderFactory.doJsonDecoder(this.objectMapper);
                    }
                    Encoder encoder;
                    if (!StringUtil.isNullOrEmpty(bodyTypeName)) {
                        if (strTypeName.equals(bodyTypeName)) {
                            encoder = new DefaultEncoder();
                        } else {
                            encoder = new JacksonEncoder(this.objectMapper);
                        }
                    } else {
                        encoder = new DefaultEncoder();
                    }
                    FluxResponsePayload decoratorGet = this.decoratorsFactory.doFluxObj(
                            this.client, decoder, encoder, HttpMethod.PATCH, patter
                    );
                    commands.put(method.toString(), new FluxCommand(decoratorGet, type));
                }
                if (MONO_NAME.equals(name)) {
                    Decoder decoder;
                    if (strTypeName.equals(type.getTypeName())) {
                        decoder = this.decoderFactory.doDefaultDecoder();
                        Encoder encoder;
                        if (!StringUtil.isNullOrEmpty(bodyTypeName)) {
                            if (strTypeName.equals(bodyTypeName)) {
                                encoder = new DefaultEncoder();
                            } else {
                                encoder = new JacksonEncoder(this.objectMapper);
                            }
                        } else {
                            encoder = new DefaultEncoder();
                        }
                        MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoObj(
                                this.client, decoder, encoder, HttpMethod.PATCH, patter
                        );
                        commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                    } else {
                        if (voidTypeName.equals(type.getTypeName())) {
                            Encoder encoder;
                            if (!StringUtil.isNullOrEmpty(bodyTypeName)) {
                                if (strTypeName.equals(bodyTypeName)) {
                                    encoder = new DefaultEncoder();
                                } else {
                                    encoder = new JacksonEncoder(this.objectMapper);
                                }
                            } else {
                                encoder = new DefaultEncoder();
                            }
                            MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoVoid(
                                    this.client, encoder, HttpMethod.PATCH, patter
                            );
                            commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                        } else {
                            decoder = this.decoderFactory.doJsonDecoder(this.objectMapper);
                            Encoder encoder;
                            if (!StringUtil.isNullOrEmpty(bodyTypeName)) {
                                if (strTypeName.equals(bodyTypeName)) {
                                    encoder = new DefaultEncoder();
                                } else {
                                    encoder = new JacksonEncoder(this.objectMapper);
                                }
                            } else {
                                encoder = new DefaultEncoder();
                            }
                            MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoObj(
                                    this.client, decoder, encoder, HttpMethod.PATCH, patter
                            );
                            commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                        }
                    }
                }
            }
            if (method.isAnnotationPresent(Trace.class)) {
                Trace trace = method.getAnnotation(Trace.class);
                String patter = trace.path();
                String name = ReflectionUtils.returnTypeName(method);
                Type type = ReflectionUtils.fetchGenericType(method);
                if (FLUX_NAME.equals(name)) {
                    Decoder decoder;
                    if (strTypeName.equals(type.getTypeName())) {
                        decoder = this.decoderFactory.doDefaultDecoder();
                    } else {
                        decoder = this.decoderFactory.doJsonDecoder(this.objectMapper);
                    }
                    Encoder encoder = new DefaultEncoder();
                    FluxResponsePayload decoratorGet = this.decoratorsFactory.doFluxObj(
                            this.client, decoder, encoder, HttpMethod.TRACE, patter
                    );
                    commands.put(method.toString(), new FluxCommand(decoratorGet, type));
                }
                if (MONO_NAME.equals(name)) {
                    Decoder decoder;
                    if (strTypeName.equals(type.getTypeName())) {
                        decoder = this.decoderFactory.doDefaultDecoder();
                    } else {
                        decoder = this.decoderFactory.doJsonDecoder(this.objectMapper);
                    }
                    Encoder encoder = new DefaultEncoder();
                    MonoResponsePayload decoratorGet = this.decoratorsFactory.doMonoObj(
                            this.client, decoder, encoder, HttpMethod.TRACE, patter
                    );
                    commands.put(method.toString(), new MonoCommand(decoratorGet, type));
                }
            }
        }
        return commands;
    }

}
