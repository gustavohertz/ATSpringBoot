package com.example.adventure_management_api.controller;

import com.example.adventure_management_api.dto.es.AgregacaoCountDto;
import com.example.adventure_management_api.entity.Produto;
import com.example.adventure_management_api.service.ProdutoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    // --- Parte A ---

    @GetMapping("/busca/nome")
    public List<Produto> buscaPorNome(@RequestParam String termo) {
        return produtoService.buscaPorNome(termo);
    }

    @GetMapping("/busca/descricao")
    public List<Produto> buscaPorDescricao(@RequestParam String termo) {
        return produtoService.buscaPorDescricao(termo);
    }

    @GetMapping("/busca/frase")
    public List<Produto> buscaPorFrase(@RequestParam String termo) {
        return produtoService.buscaPorFrase(termo);
    }

    @GetMapping("/busca/fuzzy")
    public List<Produto> buscaFuzzy(@RequestParam String termo) {
        return produtoService.buscaFuzzy(termo);
    }

    @GetMapping("/busca/multicampos")
    public List<Produto> buscaMultiCampos(@RequestParam String termo) {
        return produtoService.buscaMultiCampos(termo);
    }

    // --- Parte B ---

    @GetMapping("/busca/com-filtro")
    public List<Produto> buscaComFiltro(@RequestParam String termo, @RequestParam String categoria) {
        return produtoService.buscaTextualComFiltroCategoria(termo, categoria);
    }

    @GetMapping("/busca/faixa-preco")
    public List<Produto> buscaFaixaPreco(@RequestParam Double min, @RequestParam Double max) {
        return produtoService.buscaPorFaixaPreco(min, max);
    }

    @GetMapping("/busca/avancada")
    public List<Produto> buscaAvancada(@RequestParam String categoria, @RequestParam String raridade, @RequestParam Double min, @RequestParam Double max) {
        return produtoService.buscaAvancada(categoria, raridade, min, max);
    }

    // --- Parte C ---

    @GetMapping("/agregacoes/por-categoria")
    public List<AgregacaoCountDto> agregacaoPorCategoria() {
        return produtoService.agregacaoPorCategoria();
    }

    @GetMapping("/agregacoes/por-raridade")
    public List<AgregacaoCountDto> agregacaoPorRaridade() {
        return produtoService.agregacaoPorRaridade();
    }

    @GetMapping("/agregacoes/preco-medio")
    public Double agregacaoPrecoMedio() {
        return produtoService.agregacaoPrecoMedio();
    }

    @GetMapping("/agregacoes/faixas-preco")
    public List<AgregacaoCountDto> agregacaoFaixasPreco() {
        return produtoService.agregacaoFaixasPreco();
    }
}
