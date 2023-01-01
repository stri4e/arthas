package com.github.arthas;

import com.github.arthas.annotations.Arthas;
import com.github.arthas.annotations.Get;
import com.github.arthas.annotations.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class ArthasApplication {

    @Autowired
    private IArthasInt arthasInt;

    public static void main(String[] args) {
        SpringApplication.run(ArthasApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.arthasInt.getString("");
    }

    @Arthas(url = "http://localhost:8080")
    interface IArthasInt {

        @Get
        Mono<String> getString(@Path(name = "xz") String myParams);

    }

    @Service
    static class ArthasService implements IArthasInt {

        @Override
        public Mono<String> getString(String myParams) {
            return null;
        }
    }

    @Override
    public String toString() {
        return "ArthasApplication{" +
                "arthasInt=" + arthasInt +
                '}';
    }
}
