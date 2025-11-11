package com.malaka.aat.external.repository;

import com.malaka.aat.external.model.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, String> {
}
