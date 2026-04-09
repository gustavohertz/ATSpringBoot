package com.example.adventure_management_api.repository;

import com.example.adventure_management_api.entity.Permission;
import com.example.adventure_management_api.entity.Role;
import com.example.adventure_management_api.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes do schema audit usando H2 em memória.
 * Valida que o mapeamento JPA do sistema legado funciona corretamente.
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AuditSchemaH2Test {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void deveCarregarOrganizacaoComSucesso() {
        var org = organizationRepository.findById(1L);
        assertThat(org).isPresent();
        assertThat(org.get().getNome()).isEqualTo("Guilda dos Heróis");
        assertThat(org.get().getAtivo()).isTrue();
    }

    @Test
    void deveCarregarUsuarioComRelacionamentoOrganizacao() {
        Optional<User> userOpt = userRepository.findById(1L);
        assertThat(userOpt).isPresent();

        User user = userOpt.get();
        assertThat(user.getNome()).isEqualTo("Admin Herói");
        assertThat(user.getOrganization()).isNotNull();
        assertThat(user.getOrganization().getId()).isEqualTo(1L);
        assertThat(user.getOrganization().getNome()).isEqualTo("Guilda dos Heróis");
    }

    @Test
    void deveCarregarUsuarioComRoles() {
        Optional<User> userOpt = userRepository.findById(1L);
        assertThat(userOpt).isPresent();

        Set<Role> roles = userOpt.get().getRoles();
        assertThat(roles).isNotNull();
        assertThat(roles).hasSize(2); // ADMIN + MEMBRO
        assertThat(roles).extracting(Role::getNome).containsExactlyInAnyOrder("ADMIN", "MEMBRO");
    }

    @Test
    void deveCarregarPermissoesViaRoles() {
        Optional<User> userOpt = userRepository.findById(1L);
        assertThat(userOpt).isPresent();

        // Busca a role ADMIN do user
        Role adminRole = userOpt.get().getRoles().stream()
                .filter(r -> "ADMIN".equals(r.getNome()))
                .findFirst()
                .orElse(null);

        assertThat(adminRole).isNotNull();
        Set<Permission> permissions = adminRole.getPermissions();
        assertThat(permissions).isNotNull();
        assertThat(permissions).hasSize(4); // READ_USERS, WRITE_USERS, DELETE_USERS, READ_ADVENTURERS
        assertThat(permissions).extracting(Permission::getCode)
                .containsExactlyInAnyOrder("READ_USERS", "WRITE_USERS", "DELETE_USERS", "READ_ADVENTURERS");
    }

    @Test
    void deveCarregarUsuarioMembroComPermissoesLimitadas() {
        Optional<User> userOpt = userRepository.findById(2L);
        assertThat(userOpt).isPresent();

        Set<Role> roles = userOpt.get().getRoles();
        assertThat(roles).hasSize(1); // Apenas MEMBRO

        Role membroRole = roles.iterator().next();
        assertThat(membroRole.getNome()).isEqualTo("MEMBRO");
        assertThat(membroRole.getPermissions()).hasSize(2); // READ_USERS, READ_ADVENTURERS
    }

    @Test
    void deveListarTodasOrganizacoes() {
        var orgs = organizationRepository.findAll();
        assertThat(orgs).hasSize(2);
    }
}
