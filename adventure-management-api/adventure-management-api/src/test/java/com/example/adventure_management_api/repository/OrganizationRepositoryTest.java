package com.example.adventure_management_api.repository;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrganizationRepositoryTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Test
    void deveListarOrganizacoes() {
        var lista = organizationRepository.findAll();
        assertThat(lista).isNotEmpty();
        assertThat(lista).hasSize(2);
        System.out.println("Total de organizações: " + lista.size());
        lista.forEach(o -> System.out.println("  - " + o.getNome()));
    }
}
