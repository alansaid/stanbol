package org.apache.stanbol.ontologymanager.store.rest.resources;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.stanbol.ontologymanager.store.api.LockManager;
import org.apache.stanbol.ontologymanager.store.api.PersistenceStore;
import org.apache.stanbol.ontologymanager.store.api.ResourceManager;
import org.apache.stanbol.ontologymanager.store.model.AdministeredOntologies;
import org.apache.stanbol.ontologymanager.store.model.OntologyMetaInformation;
import org.apache.stanbol.ontologymanager.store.rest.LockManagerImp;
import org.apache.stanbol.ontologymanager.store.rest.ResourceManagerImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.view.ImplicitProduces;
import com.sun.jersey.api.view.Viewable;

//The Java class will be hosted at the URI path "/ontologies"
//@Component
//@Service(value = Object.class)
//@Property(name = "javax.ws.rs", boolValue = true)
@Path("/ontologies")
@ImplicitProduces(MediaType.TEXT_HTML + ";qs=2.0")
public class Ontologies {
    private static final Logger logger = LoggerFactory.getLogger(Ontologies.class);

    private static final String VIEWABLE_PATH = "/org/apache/stanbol/ontologymanager/store/rest/resources/ontologies";

    private PersistenceStore persistenceStore;

    public Ontologies(@Context ServletContext context) {
        this.persistenceStore = (PersistenceStore) context.getAttribute(PersistenceStore.class.getName());
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public Response getClichedMessage() {
        LockManager lockManager = LockManagerImp.getInstance();
        lockManager.obtainReadLockFor(LockManagerImp.GLOBAL_SPACE);
        Response response = null;
        try {
            AdministeredOntologies administeredOntologies = persistenceStore.retrieveAdministeredOntologies();
            response = Response.ok(administeredOntologies, MediaType.APPLICATION_XML_TYPE).build();
        } catch (Exception e) {
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            lockManager.releaseReadLockFor(LockManagerImp.GLOBAL_SPACE);
        }
        return response;
    }

    // The Java method will process HTTP POST requests
    @POST
    // The Java method will accept content identified by the MIME Media
    // type "application/x-www-form-urlencoded"
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.APPLICATION_XML)
    public Response saveOntology(@FormParam("ontologyURI") String ontologyURI,
                                 @FormParam("ontologyContent") String ontologyContent) {
        Response response = null;
        LockManager lockManager = LockManagerImp.getInstance();
        lockManager.obtainReadLockFor(LockManagerImp.GLOBAL_SPACE);
        lockManager.obtainWriteLockFor(ontologyURI);
        try {
            OntologyMetaInformation ontologyMetaInformation = persistenceStore.saveOntology(ontologyContent,
                ontologyURI, "UTF-8");
            response = Response.ok(ontologyMetaInformation, MediaType.APPLICATION_XML_TYPE).build();
        } catch (Exception e) {
            logger.error("Error ", e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            lockManager.releaseReadLockFor(LockManagerImp.GLOBAL_SPACE);
            lockManager.releaseWriteLockFor(ontologyURI);
        }
        return response;
    }

    // The Java method will process HTTP DELETE requests
    @DELETE
    public void delete() {
        LockManager lockManager = LockManagerImp.getInstance();
        lockManager.obtainWriteLockFor(LockManagerImp.GLOBAL_SPACE);
        try {

            persistenceStore.clearPersistenceStore();
            ResourceManager resourceManager = ResourceManagerImp.getInstance();
            resourceManager.clearResourceManager();
        } catch (Exception e) {
            logger.error("Error ", e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            lockManager.releaseWriteLockFor(LockManagerImp.GLOBAL_SPACE);
        }
    }

    // Methods for HTML View

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Viewable getViewable(@Context UriInfo uriInfo) {
        return new Viewable(VIEWABLE_PATH, this);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public Response createAndRedirect(@FormParam("ontologyURI") String ontologyURI,
                                      @FormParam("ontologyContent") String ontologyContent) {
        Response response = this.saveOntology(ontologyURI, ontologyContent);
        OntologyMetaInformation ont = ((OntologyMetaInformation) response.getEntity());
        try {
            return Response.seeOther(URI.create(ont.getHref())).type(MediaType.TEXT_HTML).header("Accept", MediaType.TEXT_HTML).build();
        } catch (Exception e) {
            logger.error("Error ", e);
            throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
        }

    }

    public List<OntologyMetaInformation> getOntologies() {
        LockManager lockManager = LockManagerImp.getInstance();
        lockManager.obtainReadLockFor(LockManagerImp.GLOBAL_SPACE);
        List<OntologyMetaInformation> onts = new ArrayList<OntologyMetaInformation>();
        try {
            onts = persistenceStore.retrieveAdministeredOntologies().getOntologyMetaInformation();
        } catch (Exception e) {
            logger.error("Error ", e);
            throw new RuntimeException(e);
        } finally {
            lockManager.releaseReadLockFor(LockManagerImp.GLOBAL_SPACE);
        }
        return onts;
    }
}
