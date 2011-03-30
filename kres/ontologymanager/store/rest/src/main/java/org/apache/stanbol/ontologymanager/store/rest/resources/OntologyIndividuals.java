package org.apache.stanbol.ontologymanager.store.rest.resources;

import java.net.URI;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.stanbol.ontologymanager.store.api.LockManager;
import org.apache.stanbol.ontologymanager.store.api.PersistenceStore;
import org.apache.stanbol.ontologymanager.store.api.ResourceManager;
import org.apache.stanbol.ontologymanager.store.model.IndividualMetaInformation;
import org.apache.stanbol.ontologymanager.store.model.IndividualsForOntology;
import org.apache.stanbol.ontologymanager.store.rest.LockManagerImp;
import org.apache.stanbol.ontologymanager.store.rest.ResourceManagerImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.view.ImplicitProduces;
import com.sun.jersey.api.view.Viewable;

//@Component
//@Service(value = Object.class)
//@Property(name = "javax.ws.rs", boolValue = true)
@Path("/ontologies/{ontologyPath:.+}/individuals")
@ImplicitProduces(MediaType.TEXT_HTML + ";qs=2.0")
public class OntologyIndividuals {
    private static final Logger logger = LoggerFactory.getLogger(OntologyIndividuals.class);
    public static final String VIEWABLE_PATH = "/org/apache/stanbol/ontologymanager/store/rest/resources/ontologyIndividuals";

    private PersistenceStore persistenceStore;

    // HTML Variable
    private IndividualsForOntology metadata;

    public OntologyIndividuals(@Context ServletContext context) {
        this.persistenceStore = (PersistenceStore) context.getAttribute(PersistenceStore.class.getName());
    }

    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces("application/xml")
    public Response retrieveOntologyIndividuals(@PathParam("ontologyPath") String ontologyPath) {
        Response response = null;
        LockManager lockManager = LockManagerImp.getInstance();
        lockManager.obtainReadLockFor(ontologyPath);
        try {

            ResourceManager resourceManager = ResourceManagerImp.getInstance();
            String ontologyURI = resourceManager.getOntologyURIForPath(ontologyPath);
            if (ontologyURI == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            try {
                IndividualsForOntology individualsForOntology = persistenceStore
                        .retrieveIndividualsOfOntology(ontologyURI);
                response = Response.ok(individualsForOntology, MediaType.APPLICATION_XML_TYPE).build();
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
    @Produces(MediaType.APPLICATION_XML)
    @Consumes("application/x-www-form-urlencoded")
    public Response generateIndividual(@PathParam("ontologyPath") String ontologyPath,
                                       @FormParam("classURI") String classURI,
                                       @FormParam("individualURI") String individualURI) {
        Response response = null;
        LockManager lockManager = LockManagerImp.getInstance();
        lockManager.obtainWriteLockFor(ontologyPath);
        try {

            ResourceManager resourceManager = ResourceManagerImp.getInstance();
            String ontologyURI = resourceManager.getOntologyURIForPath(ontologyPath);
            if (ontologyURI == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            try {
                IndividualMetaInformation individualMetaInformation = persistenceStore
                        .generateIndividualForOntology(ontologyURI, classURI, individualURI);
                response = Response.ok(individualMetaInformation, MediaType.APPLICATION_XML_TYPE).build();
            } catch (Exception e) {
                logger.error("Error ", e);
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
        } finally {
            lockManager.releaseWriteLockFor(ontologyPath);
        }
        return response;
    }

    // HTML View Methods
    public IndividualsForOntology getMetadata() {
        return metadata;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getViewable(@PathParam("ontologyPath") String ontologyPath) {
        Response response = retrieveOntologyIndividuals(ontologyPath);
        metadata = (IndividualsForOntology) response.getEntity();
        return new Viewable(VIEWABLE_PATH, this);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response createAndRedirect(@PathParam("ontologyPath") String ontologyPath,
                                      @FormParam("classURI") String classURI,
                                      @FormParam("individualURI") String individualURI) {
        Response response = generateIndividual(ontologyPath, classURI, individualURI);
        IndividualMetaInformation imi = (IndividualMetaInformation) response.getEntity();
        try {
            return Response.seeOther(URI.create(imi.getHref())).build();
        } catch (Exception e) {
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        }
    }
}
