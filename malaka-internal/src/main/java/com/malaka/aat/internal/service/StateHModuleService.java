package com.malaka.aat.internal.service;

import com.malaka.aat.internal.enumerators.model.ModuleState;
import com.malaka.aat.internal.model.Course;
import com.malaka.aat.internal.model.Module;
import com.malaka.aat.internal.model.User;
import com.malaka.aat.internal.model.spr.StateHMet;
import com.malaka.aat.internal.model.spr.StateHModule;
import com.malaka.aat.internal.repository.StateHModuleRepository;
import com.malaka.aat.internal.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@RequiredArgsConstructor
@Service
public class StateHModuleService {

    private final SessionService sessionService;
    private final StateHModuleRepository stateHModuleRepository;

    public StateHModule prepareStateHModule(ModuleState state, String description) {
        StateHModule stateHModule = new StateHModule();
        stateHModule.setStateNm(state.toString());
        stateHModule.setStateCd(state.getValue());
        stateHModule.setDescriptions(description);
        User currentUser = sessionService.getCurrentUser();
        stateHModule.setAplcRppnNm(ServiceUtil.extractFioFromUser(currentUser));
        stateHModule.setAplcPnfl(currentUser.getPinfl());
        currentUser.setPhone(currentUser.getPhone());
        return stateHModule;
    }

    public StateHModule createStateHModule(Module module, ModuleState state, String description) {
        StateHModule stateHModule = new StateHModule();
        stateHModule.setBodyId("MODULE_" + module.getId());
        stateHModule.setModule(module);
        stateHModule.setStateNm(state.toString());
        stateHModule.setStateCd(state.getValue());
        stateHModule.setDescriptions(description);
        User currentUser = sessionService.getCurrentUser();
        stateHModule.setAplcRppnNm(ServiceUtil.extractFioFromUser(currentUser));
        stateHModule.setAplcPnfl(currentUser.getPinfl());
        stateHModule.setHistSrno(findLastSerNum(module) + 1);
        currentUser.setPhone(currentUser.getPhone());
        StateHModule save = stateHModuleRepository.saveAndFlush(stateHModule);
        return save;
    }

    public int findLastSerNum(Module module) {
        int res = 0;
        Optional<StateHModule> byCourse = stateHModuleRepository.findFirstByModuleOrderByHistSrnoDesc(module);
        if (byCourse.isPresent()) {
            res = byCourse.get().getHistSrno();
        }
        return res;
    }
}
