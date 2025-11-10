package com.malaka.aat.internal.service;

import com.malaka.aat.internal.enumerators.model.ModuleState;
import com.malaka.aat.internal.model.Module;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.malaka.aat.internal.enumerators.course.CourseState;
import com.malaka.aat.internal.model.Course;
import com.malaka.aat.internal.model.User;
import com.malaka.aat.internal.model.spr.StateHMet;
import com.malaka.aat.internal.repository.spr.StateHMetRepository;
import com.malaka.aat.internal.util.ServiceUtil;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StateHMetService {


    private final SessionService sessionService;
    private final StateHMetRepository stateHMetRepository;

    public void saveStateForCourse(Course course, CourseState state) {
        StateHMet stateHMet = new StateHMet();
        stateHMet.setBodyId("COURSE" +  "_" + course.getId());
        stateHMet.setCourse(course);
        stateHMet.setStateNm(state.toString());
        stateHMet.setStateCd(state.getValue());
        User currentUser = sessionService.getCurrentUser();
        stateHMet.setAplcRppnNm(ServiceUtil.extractFioFromUser(currentUser));
        stateHMet.setAplcPnfl(currentUser.getPinfl());
        stateHMet.setHistSrn(findLastSerNum(course)+1);
        currentUser.setPhone(currentUser.getPhone());
        stateHMetRepository.save(stateHMet);
    }

    public int findLastSerNum(Course course) {
        int res = 0;
        Optional<StateHMet> byCourse = stateHMetRepository.findFirstByCourseOrderByHistSrn(course);
        if (byCourse.isPresent()) {
            res = byCourse.get().getHistSrn();
        }
        return res;
    }

}
