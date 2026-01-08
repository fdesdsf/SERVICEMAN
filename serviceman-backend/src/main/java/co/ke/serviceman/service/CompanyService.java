package co.ke.serviceman.service;

import co.ke.serviceman.dao.CompanyDAO;
import co.ke.serviceman.dao.ModuleConfigDAO;
import co.ke.serviceman.dto.CompanyDTO;
import co.ke.serviceman.model.Company;
import co.ke.serviceman.model.ModuleConfig;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class CompanyService {

    @Inject
    private CompanyDAO companyDAO;

    // Inject the ModuleConfigDAO to manage related data
    @Inject
    private ModuleConfigDAO moduleConfigDAO;

    // DTO methods for API responses
    public List<CompanyDTO> findAll() {
        List<Company> companies = companyDAO.findAll();
        return companies.stream()
                .map(CompanyDTO::new)
                .collect(Collectors.toList());
    }

    public CompanyDTO findById(Long id) {
        Company company = companyDAO.findById(id);
        return company != null ? new CompanyDTO(company) : null;
    }

    // Entity methods for internal operations
    public Company findEntityById(Long id) {
        return companyDAO.findById(id);
    }

    public void create(Company company) {
        companyDAO.save(company);
    }

    // New method to handle complex business logic
    public Company createCompanyWithDefaultConfig(Company company) {
        // 1. First, save the new Company entity. This operation will be part of the transaction.
        companyDAO.save(company);

        // 2. Create a new default ModuleConfig for this company.
        ModuleConfig defaultConfig = new ModuleConfig();
        defaultConfig.setCompany(company);
        // All module flags are true by default according to your schema.
        defaultConfig.setGateAttendantEnabled(true);
        defaultConfig.setReceptionistEnabled(true);
        defaultConfig.setSalesOrderDeskEnabled(true);
        defaultConfig.setInvoicingDeskEnabled(true);
        defaultConfig.setStoreClerkEnabled(true);

        // 3. Save the new ModuleConfig.
        // Because both save operations are within a single EJB method, they
        // are part of a single transaction. If one fails, both will be rolled back.
        moduleConfigDAO.save(defaultConfig);

        return company;
    }

    public void update(Company company) {
        companyDAO.update(company);
    }

    public void delete(Long id) {
        companyDAO.delete(id);
    }
}