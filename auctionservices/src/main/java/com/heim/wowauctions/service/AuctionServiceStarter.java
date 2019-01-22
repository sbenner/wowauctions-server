package com.heim.wowauctions.service;


import com.heim.wowauctions.common.persistence.solr.DateToTsConverter;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;


@SpringBootApplication
@ServletComponentScan
@EnableScheduling
@EnableSolrRepositories(basePackages = {"com.heim.wowauctions.common.persistence"})
@ComponentScan(basePackages = {"com.heim.wowauctions.service","com.heim.wowauctions.common"})
@EnableCaching
public class AuctionServiceStarter {


    public static void main(String[] args) {
        Class cls = AuctionServiceStarter.class;
        SpringApplication app = new SpringApplication(cls);
        app.setDefaultProperties(getDefaultProperties(cls));
        app.run(args);

    }
//
//    @Bean
//    @Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
//    public OAuth2RestOperations restTemplate() {
//        OAuth2RestTemplate template = new OAuth2RestTemplate(resource(), new DefaultOAuth2ClientContext(accessTokenRequest));
//        AccessTokenProviderChain provider = new AccessTokenProviderChain(Arrays.asList(new AuthorizationCodeAccessTokenProvider()));
//        provider.setClientTokenServices(clientTokenServices());
//        return template;
//    }

//
//
//    private String obtainAccessToken(String username, String password) throws Exception {
//
//        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
//        params.add("grant_type", "password");
//        params.add("client_id", "fooClientIdPassword");
//        params.add("username", username);
//        params.add("password", password);
//
//        ResultActions result
//                = mockMvc.perform(post("/oauth/token")
//                .params(params)
//                .with(httpBasic("fooClientIdPassword","secret"))
//                .accept("application/json;charset=UTF-8"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("application/json;charset=UTF-8"));
//
//        String resultString = result.andReturn().getResponse().getContentAsString();
//
//        JacksonJsonParser jsonParser = new JacksonJsonParser();
//        return jsonParser.parseMap(resultString).get("access_token").toString();
//    }


    @Bean
    public RestTemplate customRestTemplate()
    {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(10000);
        httpRequestFactory.setConnectTimeout(10000);
        httpRequestFactory.setReadTimeout(10000);
        return new RestTemplate(httpRequestFactory);
    }


    @Value("${spring.data.solr.hosts}")
    String solrHosts;

    @Bean
    public SolrClient solrClient() {
        return new HttpSolrClient.Builder(
                solrHosts).build();
    }

    @Bean
    public ConversionService conversionService() {
        DefaultConversionService service = new DefaultConversionService();
        service.addConverter(new DateToTsConverter());
        return service;
    }

    @Bean
    public SolrTemplate solrTemplate(SolrClient client) throws Exception {
        SolrTemplate solrTemplate = new SolrTemplate(client);
        //   MappingContext mappingContext = new SimpleSolrMappingContext();
        //   MappingSolrConverter converter = new MappingSolrConverter(mappingContext);
        //   List<Converter> converters = new ArrayList<>();
        //converters.add(DateToTsConverter.INSTANCE);
        //converters.add(TemporalConverters.StringToLocalDateConverter.INSTANCE);
        // SolrCustomConversions conversions = new SolrCustomConversions(converters);


        // converter.setCustomConversions(conversions);
//        conver
        // solrTemplate.setSolrConverter(converter);


        return solrTemplate;
    }

//    public HttpComponentsClientHttpRequestFactory httpRequestFactory(){
//                HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
//        httpRequestFactory.setConnectionRequestTimeout(10000);
//        httpRequestFactory.setConnectTimeout(10000);
//        httpRequestFactory.setReadTimeout(10000);
//        return httpRequestFactory;
//    }
//
//    public RemoteTokenServices tokenService() {
//    //public ClientTokenServices tokenService() {
//        RemoteTokenServices tokenService = new RemoteTokenServices();
//        tokenService.setCheckTokenEndpointUrl(
//                checkTokenUrl);
//        tokenService.setClientId("fooClientIdPassword");
//        tokenService.setClientSecret("secret");
//        return tokenService;
//    }
//
//    public OAuth2RestOperations restTemplate() {
////        OAuth2RestTemplate template = new OAuth2RestTemplate(resource(),
////                new DefaultOAuth2ClientContext(accessTokenRequest));
//        AccessTokenProviderChain provider =
//                new AccessTokenProviderChain(Arrays.asList(new AuthorizationCodeAccessTokenProvider()));
//        provider.setClientTokenServices(tokenService());
//        provider.setRequestFactory(httpRequestFactory());
//        return template;
//    }


//    @Value("${spring.data.mongodb.database}")
//    private String database;
//    @Bean
//    String database() {
//        return database;
//    }

//    @Bean
//    RestTemplate restTemplate() {
//        return new RestTemplate();
//    }


    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors()-2);
        taskExecutor.setMaxPoolSize(Runtime.getRuntime().availableProcessors()-2);
        return taskExecutor;
    }

    private static Properties getDefaultProperties(Class cls) {
        Properties properties = new Properties();
        if (cls.getPackage().getImplementationVersion() == null) {
            properties.setProperty("version", "unknown");
        } else {
            properties.setProperty("version", cls.getPackage().getImplementationVersion());
        }

        return properties;
    }

}
