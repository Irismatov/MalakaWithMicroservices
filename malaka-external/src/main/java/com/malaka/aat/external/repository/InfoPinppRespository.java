package com.malaka.aat.external.repository;

import com.malaka.aat.external.model.InfoPinpp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InfoPinppRespository extends JpaRepository<InfoPinpp, String> {

    Optional<InfoPinpp> findByPinpp(String pinpp);

}
