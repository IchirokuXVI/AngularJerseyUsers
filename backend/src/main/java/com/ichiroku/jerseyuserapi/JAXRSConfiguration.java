package com.ichiroku.jerseyuserapi;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Application;

public class JAXRSConfiguration extends Application {
    /**
     * Register the multipart jersey features
     * https://stackoverflow.com/a/29534950/9764641
     * @return Map<String, Object>;
     */
    @Override
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("jersey.config.server.provider.classnames", 
                "org.glassfish.jersey.media.multipart.MultiPartFeature");
        return props;
    }
}
