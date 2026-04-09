package com.example.adventure_management_api.entity;

import com.example.adventure_management_api.entity.enums.CompanionSpecies;
import jakarta.persistence.*;

@Entity
@Table(name = "companheiros", schema = "aventura")
public class Companion {

    @Id
    @Column(name = "aventureiro_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "aventureiro_id")
    private Adventurer adventurer;

    @Column(name = "nome", length = 120, nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(name = "especie", nullable = false)
    private CompanionSpecies especie;

    @Column(name = "indice_lealdade", nullable = false)
    private Integer indiceLealdade;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Adventurer getAdventurer() { return adventurer; }
    public void setAdventurer(Adventurer adventurer) { this.adventurer = adventurer; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public CompanionSpecies getEspecie() { return especie; }
    public void setEspecie(CompanionSpecies especie) { this.especie = especie; }

    public Integer getIndiceLealdade() { return indiceLealdade; }
    public void setIndiceLealdade(Integer indiceLealdade) { this.indiceLealdade = indiceLealdade; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Companion that = (Companion) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
