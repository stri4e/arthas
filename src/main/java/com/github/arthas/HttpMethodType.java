package com.github.arthas;

import com.github.arthas.handlers.IHttpMethod;
import com.github.arthas.handlers.impl.*;
import com.github.arthas.models.StaticMetaInfo;

public enum HttpMethodType {
    onlyMono, onlyFlux, monoWithoutBody, fluxWithoutBody, bodyMonoRespFlux, bodyFluxRespMono, ofFlux, ofMono;

    public IHttpMethod choose(StaticMetaInfo data) {
        switch (this) {
            case onlyMono:
                return new OnlyMonoHttpMethodHandler(data);
            case onlyFlux:
                return new OnlyFluxHttpMethodHandler(data);
            case monoWithoutBody:
                return new MonoWithoutBodyHttpMethodHandler(data);
            case fluxWithoutBody:
                return new FluxWithoutBodyHttpMethodHandler(data);
            case bodyMonoRespFlux:
                return new BodyMonoRespFluxHttpMethodHandler(data);
            case bodyFluxRespMono:
                return new BodyFluxRespMonoHttpMethodHandler(data);
            case ofFlux:
                return new FluxHttpMethodHandler(data);
            case ofMono:
                return new MonoHttpMethodHandler(data);
            default:
                throw new RuntimeException("Can not choose method type u forgot somme meta info.");
        }
    }

}
