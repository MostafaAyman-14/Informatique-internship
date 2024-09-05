package com.library.app.service;

import com.library.app.exceptions.ResourceNotFoundException;
import com.library.app.entity.Patron;
import com.library.app.repository.PatronRepos;
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
        return patronRepository.save(patron);
    }

    @Transactional
    @CachePut(value = "patrons", key = "#id")
    public Patron updatePatron(Long id, Patron patronDetails) {
        Patron patron = getPatronById(id);
        patron.setName(patronDetails.getName());
        patron.setContactInformation(patronDetails.getContactInformation());
        return patronRepository.save(patron);
    }

    @Transactional
    @CacheEvict(value = "patrons", key = "#id")
    public void deletePatron(Long id) {
        patronRepository.deleteById(id);
    }
}

