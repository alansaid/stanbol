package org.apache.stanbol.commons.web.base.utils;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.N3;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.N_TRIPLE;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.RDF_JSON;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.RDF_XML;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.TURTLE;
import static org.apache.clerezza.rdf.core.serializedform.SupportedFormat.X_TURTLE;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;


public final class MediaTypeUtil {

    private MediaTypeUtil(){}
    
    /**
     * Unmodifiable Set with the Media Types supported RDF serializations
     */
    public static final Set<String> SUPPORTED_RDF_TYPES = 
        Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
            APPLICATION_JSON,RDF_XML,N3,TURTLE,X_TURTLE,RDF_JSON,N_TRIPLE)));
    /**
     * THe default RDF Type {@link MediaType#APPLICATION_JSON_TYPE} for JSON-LD
     */
    public static final MediaType DEFAULT_RDF_TYPE = MediaType.APPLICATION_JSON_TYPE;
    
    /**
     * Searches the Header for acceptable media types and returns the first found
     * that is not the wildcard type. If no one is found the parsed default type
     * is returned.
     *
     * @param headers the request headers
     * @param defaultType the default type if no or only the wildcard type was found in
     * the header
     * @return the acceptable media type
     */
    public static MediaType getAcceptableMediaType(HttpHeaders headers, MediaType defaultType) {
        MediaType acceptedMediaType = null;
        if (!headers.getAcceptableMediaTypes().isEmpty()) {
            for (MediaType accepted : headers.getAcceptableMediaTypes()) {
                if (!accepted.isWildcardType()) {
                    acceptedMediaType = accepted;
                    break;
                }
            }
        }
        if (acceptedMediaType == null) {
            acceptedMediaType = defaultType;
        }
        return acceptedMediaType;
    }
    /**
     * Checks the parsed MediaTypes for supported types. WildCards are not
     * supported by this method. If no supported is found the defaultType
     * is returned
     * @param headers the headers of the request
     * @param supported the supported types
     * @param defaultType the default type used of no match is found
     * @return the first supported media type part of the header or the default
     * type 
     */
    public static MediaType getAcceptableMediaType(HttpHeaders headers, Collection<String> supported,MediaType defaultType) {
        MediaType acceptedMediaType = null;
        if (!headers.getAcceptableMediaTypes().isEmpty()) {
            for (MediaType accepted : headers.getAcceptableMediaTypes()) {
                if (!accepted.isWildcardType() && supported.contains(accepted.getType()+'/'+accepted.getSubtype())) {
                    acceptedMediaType = accepted;
                    break;
                }
            }
        }
        if (acceptedMediaType == null) {
            acceptedMediaType = defaultType;
        }
        return acceptedMediaType;
    }
    
    public static boolean isAcceptableMediaType(MediaType mediaType, Collection<String> supported){
        if(supported == null || mediaType == null){
            return true;
        } else if(supported.isEmpty()){
            return mediaType.isWildcardType() && mediaType.isWildcardSubtype();
        } else {
            String type = mediaType.getType()+'/'+mediaType.getSubtype();
            return supported.contains(type);
        }
    }
    
}
