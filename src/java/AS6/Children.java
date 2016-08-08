package AS6;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import javax.ws.rs.core.Response;

@Path("children")
@Stateless
public class Children {
    @PersistenceContext
    private EntityManager em;
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<Account> getChildren() {
        try{
            List<Account> children = new ArrayList<Account>();
            List<String> accounts = em
                    .createQuery("SELECT c.account FROM Child c")
                    .getResultList();
            for(String s : accounts){
                Account a = new Account();
                a.account = s;
                children.add(a);
            }
            return children;
        }
        catch(NoResultException e){
            return null;
        }
    }
    @POST
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public Response addChild(Child child){
       if(em.find(Child.class, child.getAccount()) == null){
           em.persist(child);
            Response.ResponseBuilder rb = 
                Response.status(201);
            return rb.build();
       } 
       else{
           Response.ResponseBuilder rb = 
                Response.status(422);
            return rb.build();
       }
    }
    
    @GET
    @Path("{account}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getChild(@PathParam("account") String account) {
        ChildDetails cd = new ChildDetails();
        try{
            Child c = em
                .createQuery("SELECT c FROM Child c WHERE c.account = :account", Child.class)
                .setParameter("account", account)
                .getSingleResult();
            cd.setDetails(c.getAccount(), c.getName(), c.getAddress());
            Response.ResponseBuilder rb = 
                Response.ok(cd);
            return rb.build();
        }
        catch(NoResultException e){
            Response.ResponseBuilder rb = 
                Response.status(404);
            return rb.build();
        }
    }
    
    @PUT
    @Path("{account}")
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updateChild(@PathParam("account") String account, Child child) {
        child.setAccount(account);
        if(em.find(Child.class, account) != null){
            em.merge(child);
            Response.ResponseBuilder rb = 
                    Response.status(200);
            return rb.build();
        }
        else{
            Response.ResponseBuilder rb = 
                Response.status(404);
            return rb.build();
        }
    }
    @POST
    @Path("{account}/wishes")
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    public Response addWish(@PathParam("account") String account, Wish wish){
       if(em.find(Child.class, account) != null){
           Child c = em.createQuery("SELECT c FROM Child c WHERE c.account = :account", Child.class)
                   .setParameter("account", account)
                   .getSingleResult();
           wish.setUser(c);
           c.getWishList().add(wish);
           em.persist(wish);
           Response.ResponseBuilder rb = 
                Response.status(201);
            return rb.build();
       } 
       else{
           Response.ResponseBuilder rb = 
                Response.status(404);
            return rb.build();
       }
    }
    
    @GET
    @Path("{account}/wishes")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Wish> getWishes(@PathParam("account") String account){
        if(em.find(Child.class, account) != null){
            return em.createQuery("SELECT c FROM Child c WHERE c.account = :account", Child.class)
                    .setParameter("account", account)
                    .getSingleResult()
                    .getWishList();
        }
        else{
            return null;
        }
    }
    
    @DELETE
    @Path("{account}/wishes/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response removeWishe(@PathParam("account") String account, @PathParam("id") Long id){
        if(em.find(Child.class, account) != null && em.find(Wish.class, id) != null){
            Child c = em.createQuery("SELECT c FROM Child c WHERE c.account = :account", Child.class)
                    .setParameter("account", account)
                    .getSingleResult();
            List<Wish> wishes = c.getWishList();
            for(Wish w : wishes){
                if(w.getId() == id){
                    em.remove(em.merge(w));
                    wishes.remove(w);
                    break;
                }
            }
            Response.ResponseBuilder rb = 
                Response.status(201);
            return rb.build();
        }
        else{
            Response.ResponseBuilder rb = 
                Response.status(404);
            return rb.build();
        }
    }
}
