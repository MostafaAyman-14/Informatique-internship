package com.library.app.service;

import com.library.app.exceptions.ResourceNotFoundException;
import com.library.app.entity.Patron;
import com.library.app.repository.PatronRepos;
import com.library.app.security.SecurityConfig;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatronService {

    @Autowired
    private PatronRepos patronRepository;

    @Autowired
    private AuditService auditService;

    @Autowired
    private SecurityConfig securityConfig;

    @Cacheable("patrons")
    public List<Patron> getAllPatrons() {
        return patronRepository.findAll();
    }

    @Cacheable(value = "patrons", key = "#id")
    public Patron getPatronById(Long id) {
        return patronRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patron not found"));
    }

    @Transactional
    @CachePut(value = "patrons", key = "#patron.id")
    public Patron addPatron(Patron patron) {
        Patron savedPatron = patronRepository.save(patron);
        auditService.log("Patron", savedPatron.getId(), "ADD", getCurrentUsername());
        return savedPatron;
    }

    @Transactional
    @CachePut(value = "patrons", key = "#id")
    public Patron updatePatron(Long id, Patron patronDetails) {
        Patron patron = getPatronById(id);
        patron.setName(patronDetails.getName());
        patron.setContactInformation(patronDetails.getContactInformation());
        Patron updatedPatron = patronRepository.save(patron);
        auditService.log("Patron", updatedPatron.getId(), "UPDATE", getCurrentUsername());
        return updatedPatron;
    }

    @Transactional
    @CacheEvict(value = "patrons", key = "#id")
    public void deletePatron(Long id) {
        patronRepository.deleteById(id);
        auditService.log("Patron", id, "DELETE", getCurrentUsername());
    }

    private String getCurrentUsername() {
        return securityConfig.getCurrentUsername();
    }
}
