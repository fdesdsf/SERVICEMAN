package co.ke.serviceman.api;

import co.ke.serviceman.dto.CompanyDTO;
import co.ke.serviceman.model.Company;
import co.ke.serviceman.service.CompanyService;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("/companies")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompanyResource {

    @Inject
    private CompanyService companyService;

    @GET
    public List<CompanyDTO> getAllCompanies() {
        return companyService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response getCompany(@PathParam("id") Long id) {
        CompanyDTO companyDTO = companyService.findById(id);
        if (companyDTO == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(companyDTO).build();
    }

    @POST
    public Response createCompany(Company company) {
        companyService.create(company);
        // Convert the created entity to DTO before returning
        CompanyDTO companyDTO = new CompanyDTO(company);
        return Response.status(Response.Status.CREATED).entity(companyDTO).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateCompany(@PathParam("id") Long id, Company company) {
        company.setId(id);
        companyService.update(company);
        // Convert the updated entity to DTO before returning
        CompanyDTO companyDTO = new CompanyDTO(company);
        return Response.ok(companyDTO).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCompany(@PathParam("id") Long id) {
        companyService.delete(id);
        return Response.noContent().build();
    }
}