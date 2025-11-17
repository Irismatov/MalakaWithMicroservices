package com.malaka.aat.internal.service;

import com.malaka.aat.core.dto.ResponseWithPagination;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.internal.clients.MalakaExternalClient;
import com.malaka.aat.internal.dto.student_application.StudentApplicationUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class StudentApplicationService {

    private final MalakaExternalClient malakaExternalClient;



    public ResponseWithPagination getApplicationsWithPagination(int page, int size, Integer status) {
        log.info("Fetching applications from malaka-external: page={}, size={}", page, size);

        try {
            ResponseWithPagination response = malakaExternalClient.getApplications(page, size, status);
            log.debug("Successfully fetched {} applications from external service",
                response.getData() != null ? ((java.util.List<?>) response.getData()).size() : 0);
            return response;
        } catch (Exception e) {
            log.error("Error fetching applications from malaka-external: {}", e.getMessage(), e);
            throw new SystemException("Failed to fetch applications from external service");
        }
    }

    public ResponseWithPagination updateStatus(String id, StudentApplicationUpdateDto dto) {
        try {
            ResponseWithPagination response =  malakaExternalClient.updateStatus(id, dto);
            log.debug("Successfully updated the application with id: {}", id);
            return response;
        } catch (Exception e) {
            log.error("Error updating applications from malaka-external: {}", e.getMessage(), e);
            throw new SystemException("Failed to fetch applications from external service");
        }
    }

    public ResponseEntity<Resource> getApplicationFile(String id) {
        try {
            return malakaExternalClient.getApplicationFile(id);
        } catch (Exception e) {
            throw new SystemException("Error happened calling an internal service");
        }
    }
}
