package com.example.adventure_management_api.entity;

import com.example.adventure_management_api.entity.enums.MissionDangerLevel;
import com.example.adventure_management_api.entity.enums.MissionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "mv_painel_tatico_missao", schema = "operacoes")
public class MvPainelTaticoMissao {

    @Id
    @Column(name = "missao_id")
    private Long missaoId;

    @Column(name = "titulo")
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MissionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_perigo")
    private MissionDangerLevel nivelPerigo;

    @Column(name = "organizacao_id")
    private Long organizacaoId;

    @Column(name = "total_participantes")
    private Integer totalParticipantes;

    @Column(name = "nivel_medio_equipe")
    private Double nivelMedioEquipe;

    @Column(name = "total_recompensa")
    private BigDecimal totalRecompensa;

    @Column(name = "total_mvps")
    private Integer totalMvps;

    @Column(name = "participantes_com_companheiro")
    private Integer participantesComCompanheiro;

    @Column(name = "ultima_atualizacao")
    private Instant ultimaAtualizacao;

    @Column(name = "indice_prontidao")
    private Double indiceProntidao;

    // Getters and Setters

    public Long getMissaoId() {
        return missaoId;
    }

    public void setMissaoId(Long missaoId) {
        this.missaoId = missaoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public MissionStatus getStatus() {
        return status;
    }

    public void setStatus(MissionStatus status) {
        this.status = status;
    }

    public MissionDangerLevel getNivelPerigo() {
        return nivelPerigo;
    }

    public void setNivelPerigo(MissionDangerLevel nivelPerigo) {
        this.nivelPerigo = nivelPerigo;
    }

    public Long getOrganizacaoId() {
        return organizacaoId;
    }

    public void setOrganizacaoId(Long organizacaoId) {
        this.organizacaoId = organizacaoId;
    }

    public Integer getTotalParticipantes() {
        return totalParticipantes;
    }

    public void setTotalParticipantes(Integer totalParticipantes) {
        this.totalParticipantes = totalParticipantes;
    }

    public Double getNivelMedioEquipe() {
        return nivelMedioEquipe;
    }

    public void setNivelMedioEquipe(Double nivelMedioEquipe) {
        this.nivelMedioEquipe = nivelMedioEquipe;
    }

    public BigDecimal getTotalRecompensa() {
        return totalRecompensa;
    }

    public void setTotalRecompensa(BigDecimal totalRecompensa) {
        this.totalRecompensa = totalRecompensa;
    }

    public Integer getTotalMvps() {
        return totalMvps;
    }

    public void setTotalMvps(Integer totalMvps) {
        this.totalMvps = totalMvps;
    }

    public Integer getParticipantesComCompanheiro() {
        return participantesComCompanheiro;
    }

    public void setParticipantesComCompanheiro(Integer participantesComCompanheiro) {
        this.participantesComCompanheiro = participantesComCompanheiro;
    }

    public Instant getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(Instant ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public Double getIndiceProntidao() {
        return indiceProntidao;
    }

    public void setIndiceProntidao(Double indiceProntidao) {
        this.indiceProntidao = indiceProntidao;
    }
}
