package com.example.adventure_management_api.entity;

import com.example.adventure_management_api.entity.enums.AdventurerClass;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "aventureiros", schema = "aventura")
public class Adventurer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User userCadastro;

    @Column(name = "nome", length = 120, nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "classe", nullable = false)
    private AdventurerClass classeAventureiro;

    @Column(name = "nivel", nullable = false)
    private Integer nivel;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToOne(mappedBy = "adventurer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Companion companion;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.nivel == null) {
            this.nivel = 1;
        }
        if (this.ativo == null) {
            this.ativo = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }

    public User getUserCadastro() { return userCadastro; }
    public void setUserCadastro(User userCadastro) { this.userCadastro = userCadastro; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public AdventurerClass getClasseAventureiro() { return classeAventureiro; }
    public void setClasseAventureiro(AdventurerClass classeAventureiro) { this.classeAventureiro = classeAventureiro; }

    public Integer getNivel() { return nivel; }
    public void setNivel(Integer nivel) { this.nivel = nivel; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Companion getCompanion() { return companion; }
    public void setCompanion(Companion companion) {
        this.companion = companion;
        if (companion != null) {
            companion.setAdventurer(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adventurer that = (Adventurer) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
