package com.example.adventure_management_api.repository;

import com.example.adventure_management_api.entity.Produto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends ElasticsearchRepository<Produto, String> {
}
