package com.malaka.aat.internal.repository;

import com.malaka.aat.internal.model.Module;
import com.malaka.aat.internal.model.spr.StateHMet;
import com.malaka.aat.internal.model.spr.StateHModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface StateHModuleRepository extends JpaRepository<StateHModule, String> {

    @Query(value = "from StateHModule s where s.module = :module order by s.histSrno desc limit 1")
    Optional<StateHModule> findFirstByModuleOrderByHistSrnoDesc(Module module);
}
