package com.malaka.aat.external.enumerators.group;


import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.external.model.Group;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum GroupStatus {

    CREATED(0),
    STARTED(1),
    EXPIRED(2);

    private final int value;


    public static void setStatus(Group group, int status) {
        Optional<GroupStatus> first = Arrays.stream(GroupStatus.values()).filter(s -> s.getValue() == status).findFirst();
        if (first.isPresent()) {
            group.setStatus(first.get());
        } else {
            throw new NotFoundException("Status not found for group status: " + status);
        }
    }
}
