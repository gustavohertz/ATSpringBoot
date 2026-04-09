package com.example.adventure_management_api.service;

import com.example.adventure_management_api.entity.MvPainelTaticoMissao;
import com.example.adventure_management_api.repository.MvPainelTaticoMissaoRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;

@Service
public class PainelTaticoMissaoService {

    private final MvPainelTaticoMissaoRepository repository;

    public PainelTaticoMissaoService(MvPainelTaticoMissaoRepository repository) {
        this.repository = repository;
    }

    @Cacheable("topMissoesCache")
    public List<MvPainelTaticoMissao> getTop10MissoesTaticas() {
        Instant quinzeDiasAtras = Instant.now().minus(15, ChronoUnit.DAYS);
        return repository.findTop10ByUltimaAtualizacaoGreaterThanEqualOrderByIndiceProntidaoDesc(quinzeDiasAtras);
    }

    @CacheEvict(value = "topMissoesCache", allEntries = true)
    @Scheduled(fixedRate = 60000) // Limpa o cache a cada 60 segundos
    public void limparCacheTopMissoes() {
        // Apenas um gatilho temporizado que invalida a chave de cache e força a view a ser reconsultada no próximo hit
    }
}
