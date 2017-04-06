package com.example.swaggertest.web;

import com.google.common.collect.ImmutableMap;
import org.springframework.http.MediaType;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.AbstractMappingContentNegotiationStrategy;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Locale;
import java.util.Map;

/**
 * Custom strategy for spring ContentNegotiationManager.
 * Used for proper recognition of values for parameter "output".
 */
public class ApiParameterContentNegotiationStrategy extends AbstractMappingContentNegotiationStrategy {

    private static final String PARAMETER_NAME = "output";

    private static final Map<String, MediaType> MEDIA_TYPES_MAP = ImmutableMap.of(
            "xml", MediaType.valueOf("application/xml"),
            "json", MediaType.valueOf("application/json"));

    public ApiParameterContentNegotiationStrategy() {
        super(MEDIA_TYPES_MAP);
    }

    @Override
    protected String getMediaTypeKey(NativeWebRequest webRequest) {
        if (webRequest.getParameter(PARAMETER_NAME) == null) {
            return "xml";
        } else {
            return webRequest.getParameter(PARAMETER_NAME).toLowerCase(Locale.ENGLISH);
        }
    }

    @Override
    protected MediaType handleNoMatch(NativeWebRequest request, String key) throws HttpMediaTypeNotAcceptableException {
        throw new HttpMediaTypeNotAcceptableException(getAllMediaTypes());
    }

}
