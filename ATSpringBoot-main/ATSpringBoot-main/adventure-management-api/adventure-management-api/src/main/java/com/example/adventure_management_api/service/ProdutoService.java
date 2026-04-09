package com.example.adventure_management_api.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.RangeBucket;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.example.adventure_management_api.dto.es.AgregacaoCountDto;
import com.example.adventure_management_api.entity.Produto;
import com.example.adventure_management_api.repository.ProdutoRepository;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticsearchClient esClient;

    public ProdutoService(ProdutoRepository produtoRepository,
            ElasticsearchOperations elasticsearchOperations,
            ElasticsearchClient esClient) {
        this.produtoRepository = produtoRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.esClient = esClient;
    }

    public List<Produto> buscaPorNome(String termo) {
        try {
            String queryStr = """
                    { "match": { "nome": "%s" } }
                    """.formatted(sanitize(termo));
            SearchHits<Produto> hits = elasticsearchOperations.search(new StringQuery(queryStr), Produto.class);
            return hits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Produto> buscaPorDescricao(String termo) {
        try {
            String queryStr = """
                    { "match": { "descricao": "%s" } }
                    """.formatted(sanitize(termo));
            SearchHits<Produto> hits = elasticsearchOperations.search(new StringQuery(queryStr), Produto.class);
            return hits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Produto> buscaPorFrase(String frase) {
        try {
            String queryStr = """
                    { "match_phrase": { "descricao": "%s" } }
                    """.formatted(sanitize(frase));
            SearchHits<Produto> hits = elasticsearchOperations.search(new StringQuery(queryStr), Produto.class);
            return hits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Produto> buscaFuzzy(String termo) {
        try {
            String queryStr = """
                    { "match": { "nome": { "query": "%s", "fuzziness": "AUTO" } } }
                    """.formatted(sanitize(termo));
            SearchHits<Produto> hits = elasticsearchOperations.search(new StringQuery(queryStr), Produto.class);
            return hits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Produto> buscaMultiCampos(String termo) {
        try {
            String queryStr = """
                    { "multi_match": { "query": "%s", "fields": ["nome", "descricao"] } }
                    """.formatted(sanitize(termo));
            SearchHits<Produto> hits = elasticsearchOperations.search(new StringQuery(queryStr), Produto.class);
            return hits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Produto> buscaTextualComFiltroCategoria(String termo, String categoria) {
        try {
            Criteria criteria = new Criteria("descricao").matches(termo)
                    .and(new Criteria("categoria").is(categoria));
            SearchHits<Produto> hits = elasticsearchOperations.search(new CriteriaQuery(criteria), Produto.class);
            return hits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Produto> buscaPorFaixaPreco(Double min, Double max) {
        try {
            Criteria criteria = new Criteria("preco").greaterThanEqual(min).lessThanEqual(max);
            SearchHits<Produto> hits = elasticsearchOperations.search(new CriteriaQuery(criteria), Produto.class);
            return hits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Produto> buscaAvancada(String categoria, String raridade, Double min, Double max) {
        try {
            Criteria criteria = new Criteria("categoria").is(categoria)
                    .and(new Criteria("raridade").is(raridade))
                    .and(new Criteria("preco").greaterThanEqual(min).lessThanEqual(max));
            SearchHits<Produto> hits = elasticsearchOperations.search(new CriteriaQuery(criteria), Produto.class);
            return hits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<AgregacaoCountDto> agregacaoPorCategoria() {
        try {
            SearchResponse<Void> response = esClient.search(b -> b
                    .index("guilda_loja")
                    .size(0)
                    .aggregations("por_categoria", a -> a
                            .terms(t -> t.field("categoria"))),
                    Void.class);

            List<AgregacaoCountDto> result = new ArrayList<>();
            List<StringTermsBucket> buckets = response.aggregations().get("por_categoria").sterms().buckets().array();
            for (StringTermsBucket bucket : buckets) {
                result.add(new AgregacaoCountDto(bucket.key().stringValue(), bucket.docCount()));
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<AgregacaoCountDto> agregacaoPorRaridade() {
        try {
            SearchResponse<Void> response = esClient.search(b -> b
                    .index("guilda_loja")
                    .size(0)
                    .aggregations("por_raridade", a -> a
                            .terms(t -> t.field("raridade"))),
                    Void.class);

            List<AgregacaoCountDto> result = new ArrayList<>();
            List<StringTermsBucket> buckets = response.aggregations().get("por_raridade").sterms().buckets().array();
            for (StringTermsBucket bucket : buckets) {
                result.add(new AgregacaoCountDto(bucket.key().stringValue(), bucket.docCount()));
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public Double agregacaoPrecoMedio() {
        try {
            SearchResponse<Void> response = esClient.search(b -> b
                    .index("guilda_loja")
                    .size(0)
                    .aggregations("preco_medio", a -> a
                            .avg(avg -> avg.field("preco"))),
                    Void.class);

            return response.aggregations().get("preco_medio").avg().value();
        } catch (Exception e) {
            return 0.0;
        }
    }

    public List<AgregacaoCountDto> agregacaoFaixasPreco() {
        try {
            SearchResponse<Void> response = esClient.search(b -> b
                    .index("guilda_loja")
                    .size(0)
                    .aggregations("faixas", a -> a
                            .range(r -> r
                                    .field("preco")
                                    .ranges(rg -> rg.to("100"))
                                    .ranges(rg -> rg.from("100").to("300"))
                                    .ranges(rg -> rg.from("300").to("700"))
                                    .ranges(rg -> rg.from("700")))),
                    Void.class);

            List<AgregacaoCountDto> result = new ArrayList<>();
            List<RangeBucket> buckets = response.aggregations().get("faixas").range().buckets().array();
            for (RangeBucket bucket : buckets) {
                result.add(new AgregacaoCountDto(bucket.key() != null ? bucket.key() : "N/A", bucket.docCount()));
            }
            return result;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private String sanitize(String input) {
        if (input == null)
            return "";
        return input.replace("\"", "\\\"").replace("\\", "\\\\");
    }
}
