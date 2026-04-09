package com.example.adventure_management_api.repository;

import com.example.adventure_management_api.entity.MvPainelTaticoMissao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MvPainelTaticoMissaoRepository extends JpaRepository<MvPainelTaticoMissao, Long> {

    List<MvPainelTaticoMissao> findTop10ByUltimaAtualizacaoGreaterThanEqualOrderByIndiceProntidaoDesc(Instant dataLimite);
}
