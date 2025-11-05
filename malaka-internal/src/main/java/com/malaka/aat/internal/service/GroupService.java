package com.malaka.aat.internal.service;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.internal.clients.MalakaExternalClient;
import com.malaka.aat.internal.dto.group.GroupCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GroupService {

    private final MalakaExternalClient malakaExternalClient;

    public BaseResponse save(GroupCreateDto dto) {
        try {
            BaseResponse response = malakaExternalClient.createGroup(dto);
            return response;
        } catch (Exception e) {
            throw new SystemException("Call to create a group to a microservice failed");
        }
    }

    public ResponseWithPagination getCoursesWithPagination(int page, int size) {
        try {
            ResponseWithPagination groupsWithPagination = malakaExternalClient.getGroupsWithPagination(page, size);
            return groupsWithPagination;
        } catch (Exception e) {
            throw new SystemException("Call to get groups from a microservice failed");
        }
    }
}
