package com.library.app.service;

import com.library.app.exceptions.ResourceNotFoundException;
import com.library.app.entity.Patron;
import com.library.app.repository.PatronRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatronService {
    @Autowired
    private PatronRepos patronRepository;

    public List<Patron> getAllPatrons() {
        return patronRepository.findAll();
    }

    public Patron getPatronById(Long id) {
        return patronRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Patron not found"));
    }

    public Patron addPatron(Patron patron) {
        return patronRepository.save(patron);
    }

    public Patron updatePatron(Long id, Patron patronDetails) {
        Patron patron = getPatronById(id);
        patron.setName(patronDetails.getName());
        patron.setContactInformation(patronDetails.getContactInformation());
        return patronRepository.save(patron);
    }

    public void deletePatron(Long id) {
        patronRepository.deleteById(id);
    }
}

