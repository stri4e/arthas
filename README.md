# arthas
This is Bean Post Processor for Spring Boot WebClient

Sorry for tests (1.0-SNAPSHOT). It's into next release. 

Example: 
    
    @Arthas(
        url = "http://localhost:8080"
    )
    public interface IClient {

        @Get(path = "/{content}")
        Mono<Models> sendGetRequest(@Path(name = "content") String content, @Query(name = "size") Integer size);

        @Put(path = "/{content}")
        Mono<Void> sendPutRequest(@Path(name = "content") String content, @Query(name = "size") Integer size, @Body Models payload);

        @Delete(path = "/{content}")
        Mono<Void> sendDeleteRequest(@Path(name = "id") Integer id);

        @Post(path = "/{content}")
        Mono<Models> sendPostRequest(@Path(name = "content") String content, @Body Models payload);

    }    
    
    OR u can use the next annoataions: 
    @ResponseToEmptyFlux, @ResponseToEmptyMono, @ResponseToFlux, @ResponseToMono 
    and @BodyToMono,  @BodyToFlux
    
    @Arthas(
        url = "http://localhost:8080"
    )
    public interface IClient {

        @Get(path = "/{content}")
        @ResponseToMono(clazz = Models.class)
        Mono<Models> sendGetRequest(@Path(name = "content") String content, @Query(name = "size") Integer size);

        @Put(path = "/{content}")
        @BodyToMono(clazz = Models.class)
        @ResponseToMono(clazz = Void.class)
        Mono<Void> sendPutRequest(@Path(name = "content") String content, @Query(name = "size") Integer size, @Body Models payload);

        @Delete(path = "/{content}")
        @ResponseToMono(clazz = Void.class)
        Mono<Void> sendDeleteRequest(@Path(name = "id") Integer id);

        @Post(path = "/{content}")
        @BodyToMono(clazz = Models.class)
        @ResponseToMono(clazz = Models.class)
        Mono<Models> sendPostRequest(@Path(name = "content") String content, @Body Models payload);

    }
    
    
Annotations:
        
        This annotation use for set http method:
        
        @Get, @Post, @Put, @Delete, @Patch, @Trace, @Options, @Head -  
        
        This annotation use for set body type:
        
        @BodyToMono,  @BodyToFlux
        
        This annotation use for set response type:
        
        @ResponseToEmptyFlux, @ResponseToEmptyMono, @ResponseToFlux, @ResponseToMono
        
        This annotation use for set path, query, and mark body:
        
        @Path, @Query, @Body