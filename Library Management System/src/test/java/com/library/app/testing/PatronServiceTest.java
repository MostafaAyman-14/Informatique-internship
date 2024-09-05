package com.library.app.testing;

import com.library.app.entity.Patron;
import com.library.app.exceptions.ResourceNotFoundException;
import com.library.app.repository.PatronRepos;
import com.library.app.security.SecurityConfig;
import com.library.app.service.PatronService;
import com.library.app.service.AuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatronServiceTest {

    @Mock
    private PatronRepos patronRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private SecurityConfig securityConfig;

    @InjectMocks
    private PatronService patronService;

    private Patron patron;

    @BeforeEach
    void setUp() {
        patron = new Patron();
        patron.setId(1L);
        patron.setName("Test Patron");
        patron.setContactInformation("test@example.com");
    }

    @Test
    void testGetAllPatrons() {
        when(patronRepository.findAll()).thenReturn(Arrays.asList(patron));

        List<Patron> patrons = patronService.getAllPatrons();

        assertNotNull(patrons);
        assertEquals(1, patrons.size());
        assertEquals(patron, patrons.get(0));

        verify(patronRepository, times(1)).findAll();
    }

    @Test
    void testGetPatronById() {
        when(patronRepository.findById(1L)).thenReturn(Optional.of(patron));

        Patron foundPatron = patronService.getPatronById(1L);

        assertNotNull(foundPatron);
        assertEquals(patron, foundPatron);

        verify(patronRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPatronById_NotFound() {
        when(patronRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patronService.getPatronById(1L));

        verify(patronRepository, times(1)).findById(1L);
    }

    @Test
    void testAddPatron() {
        when(patronRepository.save(any(Patron.class))).thenReturn(patron);
        when(securityConfig.getCurrentUsername()).thenReturn("testuser");
        Patron savedPatron = patronService.addPatron(patron);

        assertNotNull(savedPatron);
        assertEquals(patron, savedPatron);

        verify(patronRepository, times(1)).save(any(Patron.class));
        verify(auditService, times(1)).log(eq("Patron"), eq(patron.getId()), eq("ADD"), eq("testuser"));
    }

    @Test
    void testUpdatePatron() {
        when(patronRepository.findById(1L)).thenReturn(Optional.of(patron));
        when(patronRepository.save(any(Patron.class))).thenReturn(patron);
        when(securityConfig.getCurrentUsername()).thenReturn("testuser");
        Patron updatedPatron = patronService.updatePatron(1L, patron);

        assertNotNull(updatedPatron);
        assertEquals(patron, updatedPatron);

        verify(patronRepository, times(1)).findById(1L);
        verify(patronRepository, times(1)).save(any(Patron.class));
        verify(auditService, times(1)).log(eq("Patron"), eq(patron.getId()), eq("UPDATE"), eq("testuser"));
    }

    @Test
    void testUpdatePatron_NotFound() {
        when(patronRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> patronService.updatePatron(1L, patron));

        verify(patronRepository, times(1)).findById(1L);
    }

    @Test
    void testDeletePatron() {
        doNothing().when(patronRepository).deleteById(1L);
        when(securityConfig.getCurrentUsername()).thenReturn("testuser");
        patronService.deletePatron(1L);

        verify(patronRepository, times(1)).deleteById(1L);
        verify(auditService, times(1)).log(eq("Patron"), eq(1L), eq("DELETE"), eq("testuser"));
    }
}
