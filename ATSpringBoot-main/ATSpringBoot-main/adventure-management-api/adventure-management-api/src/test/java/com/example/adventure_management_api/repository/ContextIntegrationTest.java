package com.example.adventure_management_api.repository;

import com.example.adventure_management_api.entity.Permission;
import com.example.adventure_management_api.entity.Role;
import com.example.adventure_management_api.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testes de integração do schema audit contra PostgreSQL real.
 * 
 * Rodar com: 
 *   set RUN_INTEGRATION=true && mvn test -Dtest=ContextIntegrationTest
 * 
 * Estes testes são ignorados por padrão (requerem PostgreSQL local rodando).
 */
@DataJpaTest
@ActiveProfiles("integration")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnabledIfEnvironmentVariable(named = "RUN_INTEGRATION", matches = "true")
class ContextIntegrationTest {

    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void deveCarregarOrganizacoes() {
        var orgs = organizationRepository.findAll();
        assertThat(orgs).isNotEmpty();
        System.out.println("Organizações encontradas: " + orgs.size());
        orgs.forEach(o -> System.out.println("  - " + o.getNome()));
    }

    @Test
    void deveCarregarUsuarioComRolesEOrganizacao() {
        var users = userRepository.findAll();
        assertThat(users).isNotEmpty();

        User user = users.get(0);
        assertThat(user.getOrganization()).isNotNull();
        System.out.println("Usuário: " + user.getNome());
        System.out.println("Organização: " + user.getOrganization().getNome());

        Set<Role> roles = user.getRoles();
        assertThat(roles).isNotNull();
        System.out.println("Roles: " + roles.size());
        for (Role role : roles) {
            System.out.println("  - Role: " + role.getNome());
            Set<Permission> perms = role.getPermissions();
            assertThat(perms).isNotNull();
            for (Permission p : perms) {
                System.out.println("    - Permission: " + p.getCode());
            }
        }
    }

    @Test
    void deveCarregarRolesComPermissoes() {
        var roles = roleRepository.findAll();
        assertThat(roles).isNotEmpty();
        for (Role role : roles) {
            System.out.println("Role: " + role.getNome() + " (Org: " + role.getOrganization().getNome() + ")");
            assertThat(role.getPermissions()).isNotNull();
            role.getPermissions().forEach(p -> System.out.println("  - " + p.getCode()));
        }
    }
}
