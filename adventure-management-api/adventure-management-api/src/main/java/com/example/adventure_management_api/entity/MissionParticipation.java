package com.example.adventure_management_api.entity;

import com.example.adventure_management_api.entity.enums.MissionRole;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "participacoes_missao", schema = "aventura",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"missao_id", "aventureiro_id"})
       })
public class MissionParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "missao_id", nullable = false)
    private Mission mission;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aventureiro_id", nullable = false)
    private Adventurer adventurer;

    @Enumerated(EnumType.STRING)
    @Column(name = "papel", nullable = false)
    private MissionRole papel;

    @Column(name = "recompensa_ouro")
    private BigDecimal recompensaOuro;

    @Column(name = "destaque", nullable = false)
    private Boolean destaque;

    @Column(name = "data_registro", nullable = false, updatable = false)
    private Instant dataRegistro;

    @PrePersist
    public void prePersist() {
        this.dataRegistro = Instant.now();
        if (this.destaque == null) {
            this.destaque = false;
        }
        if (this.recompensaOuro == null) {
            this.recompensaOuro = BigDecimal.ZERO;
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Mission getMission() { return mission; }
    public void setMission(Mission mission) { this.mission = mission; }

    public Adventurer getAdventurer() { return adventurer; }
    public void setAdventurer(Adventurer adventurer) { this.adventurer = adventurer; }

    public MissionRole getPapel() { return papel; }
    public void setPapel(MissionRole papel) { this.papel = papel; }

    public BigDecimal getRecompensaOuro() { return recompensaOuro; }
    public void setRecompensaOuro(BigDecimal recompensaOuro) { this.recompensaOuro = recompensaOuro; }

    public Boolean getDestaque() { return destaque; }
    public void setDestaque(Boolean destaque) { this.destaque = destaque; }

    public Instant getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(Instant dataRegistro) { this.dataRegistro = dataRegistro; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MissionParticipation that = (MissionParticipation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
