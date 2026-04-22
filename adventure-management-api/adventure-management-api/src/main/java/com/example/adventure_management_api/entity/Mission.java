package com.example.adventure_management_api.entity;

import com.example.adventure_management_api.entity.enums.MissionDangerLevel;
import com.example.adventure_management_api.entity.enums.MissionStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "missoes", schema = "aventura")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organization organization;

    @Column(name = "titulo", length = 150, nullable = false)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_perigo", nullable = false)
    private MissionDangerLevel nivelPerigo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MissionStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "data_inicio")
    private Instant dataInicio;

    @Column(name = "data_termino")
    private Instant dataTermino;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        if (this.status == null) {
            this.status = MissionStatus.PLANEJADA;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Organization getOrganization() { return organization; }
    public void setOrganization(Organization organization) { this.organization = organization; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public MissionDangerLevel getNivelPerigo() { return nivelPerigo; }
    public void setNivelPerigo(MissionDangerLevel nivelPerigo) { this.nivelPerigo = nivelPerigo; }

    public MissionStatus getStatus() { return status; }
    public void setStatus(MissionStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getDataInicio() { return dataInicio; }
    public void setDataInicio(Instant dataInicio) { this.dataInicio = dataInicio; }

    public Instant getDataTermino() { return dataTermino; }
    public void setDataTermino(Instant dataTermino) { this.dataTermino = dataTermino; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mission mission = (Mission) o;
        return id != null && id.equals(mission.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
