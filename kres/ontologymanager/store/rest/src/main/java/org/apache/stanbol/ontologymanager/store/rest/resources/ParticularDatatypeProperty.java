package org.apache.stanbol.ontologymanager.store.rest.resources;

import java.net.URI;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.stanbol.ontologymanager.store.api.LockManager;
import org.apache.stanbol.ontologymanager.store.api.PersistenceStore;
import org.apache.stanbol.ontologymanager.store.model.DatatypePropertyContext;
import org.apache.stanbol.ontologymanager.store.rest.LockManagerImp;
import org.apache.stanbol.ontologymanager.store.rest.ResourceManagerImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.view.ImplicitProduces;
import com.sun.jersey.api.view.Viewable;

//@Component
//@Service(value = Object.class)
//@Property(name = "javax.ws.rs", boolValue = true)
@Path("/ontologies/{ontologyPath:.+}/datatypeProperties/{datatypePropertyPath:.+}")
@ImplicitProduces(MediaType.TEXT_HTML + ";qs=2.0")
public class ParticularDatatypeProperty {
    private static final Logger logger = LoggerFactory.getLogger(ParticularDatatypeProperty.class);

    private static final String VIEWABLE_PATH = "/org/apache/stanbol/ontologymanager/store/rest/resources/particularDatatypeProperty";

    private PersistenceStore persistenceStore;

    // HTML View Variable
    private DatatypePropertyContext metadata;

    public ParticularDatatypeProperty(@Context ServletContext context) {
        this.persistenceStore = (PersistenceStore) context.getAttribute(PersistenceStore.class.getName());
    }

    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "application/xml"
    @Produces("application/xml")
    public Response retrieveDatatypePropertyContext(@PathParam("ontologyPath") String ontologyPath,
                                                    @PathParam("datatypePropertyPath") String datatypePropertyPath,
                                                    @DefaultValue("false") @QueryParam("withInferredAxioms") boolean withInferredAxioms) {
        Response response = null;
        LockManager lockManager = LockManagerImp.getInstance();
        lockManager.obtainReadLockFor(ontologyPath);
        try {
            ResourceManagerImp resourceManager = ResourceManagerImp.getInstance();
            String datatypePropertyURI = resourceManager.getResourceURIForPath(ontologyPath,
                datatypePropertyPath);
            if (datatypePropertyURI == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            try {
                DatatypePropertyContext datatypePropertyContext = persistenceStore
                        .generateDatatypePropertyContext(datatypePropertyURI, withInferredAxioms);
                response = Response.ok(datatypePropertyContext, MediaType.APPLICATION_XML_TYPE).build();
            } catch (Exception e) {
                logger.error("Error ", e);
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
        } finally {
            lockManager.releaseReadLockFor(ontologyPath);
        }
        return response;
    }

    // The Java method will process HTTP POST requests
    @POST
    // The Java method will accept content identified by the MIME Media
    // type "application/x-www-form-urlencoded"
    @Consumes("application/x-www-form-urlencoded")
    public Response makeSubClassOf(@PathParam("ontologyPath") String ontologyPath,
                                   @PathParam("datatypePropertyPath") String datatypePropertyPath,
                                   @FormParam("isFunctional") Boolean isFunctional) {
        Response response = null;
        LockManager lockManager = LockManagerImp.getInstance();
        lockManager.obtainWriteLockFor(ontologyPath);
        try {

            ResourceManagerImp resourceManager = ResourceManagerImp.getInstance();
            String datatypePropertyURI = resourceManager.getResourceURIForPath(ontologyPath,
                datatypePropertyPath);
            if (datatypePropertyURI == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            try {
                if (isFunctional != null) {
                    persistenceStore.setPropertyAttributes(datatypePropertyURI, isFunctional, null, null,
                        null);
                }
                DatatypePropertyContext datatypePropertyContext = persistenceStore
                        .generateDatatypePropertyContext(datatypePropertyURI, false);

                response = Response.ok(datatypePropertyContext, MediaType.APPLICATION_XML_TYPE).build();
            } catch (Exception e) {
                logger.error("Error ", e);
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
        } finally {
            lockManager.releaseWriteLockFor(ontologyPath);
        }
        return response;
    }

    // FIXME Handle PS errors properly and return correct responses

    // The Java method will process HTTP DELETE requests
    @DELETE
    public void delete(@PathParam("ontologyPath") String ontologyPath,
                       @PathParam("datatypePropertyPath") String datatypePropertyPath) {
        LockManager lockManager = LockManagerImp.getInstance();
        lockManager.obtainWriteLockFor(ontologyPath);
        try {

            ResourceManagerImp resourceManager = ResourceManagerImp.getInstance();
            String resourceURI = resourceManager.getResourceURIForPath(ontologyPath, datatypePropertyPath);
            persistenceStore.deleteResource(resourceURI);
        } catch (Exception e) {
            logger.error("Error ", e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            lockManager.releaseWriteLockFor(ontologyPath);
        }
    }

    // HTML View Methods
    public DatatypePropertyContext getMetadata() {
        return metadata;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getViewable(@PathParam("ontologyPath") String ontologyPath,
                                @PathParam("datatypePropertyPath") String datatypePropertyPath,
                                @DefaultValue("false") @QueryParam("withInferredAxioms") boolean withInferredAxioms) {
        Response response = retrieveDatatypePropertyContext(ontologyPath, datatypePropertyPath,
            withInferredAxioms);
        metadata = (DatatypePropertyContext) response.getEntity();
        return new Viewable(VIEWABLE_PATH, this);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getViewable(@PathParam("ontologyPath") String ontologyPath,
                                @PathParam("datatypePropertyPath") String datatypePropertyPath,
                                @FormParam("isFunctional") Boolean isFunctional) {
        metadata = (DatatypePropertyContext) makeSubClassOf(ontologyPath, datatypePropertyPath, /*
                                                                                                 * domainURIs,
                                                                                                 * rangeURIs,
                                                                                                 * subPropertyOf
                                                                                                 * ,
                                                                                                 */
        isFunctional).getEntity();
        Response response = Response.seeOther(URI.create(metadata.getPropertyMetaInformation().getHref()))
                .build();
        return response;
    }

}
