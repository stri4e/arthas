package com.github.arthas;

import com.github.arthas.handlers.IHttpMethod;
import com.github.arthas.handlers.impl.*;
import com.github.arthas.models.StaticMetaInfo;

public enum HttpMethodType {
    onlyMono, onlyFlux, monoWithoutBody, fluxWithoutBody, bodyMonoRespFlux, bodyFluxRespMono;

    public IHttpMethod choose(StaticMetaInfo data) {
        switch (this) {
            case onlyMono:
                return new OnlyMono(data);
            case onlyFlux:
                return new OnlyFlux(data);
            case monoWithoutBody:
                return new MonoWithoutBody(data);
            case fluxWithoutBody:
                return new FluxWithoutBody(data);
            case bodyMonoRespFlux:
                return new BodyMonoRespFlux(data);
            case bodyFluxRespMono:
                return new BodyFluxRespMono(data);
            default:
                throw new RuntimeException("Can not choose method type u forgot somme meta info.");
        }
    }

}
