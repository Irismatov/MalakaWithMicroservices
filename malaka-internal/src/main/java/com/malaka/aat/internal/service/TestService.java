package com.malaka.aat.internal.service;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.core.dto.ResponseStatus;
import com.malaka.aat.core.exception.custom.AuthException;
import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.core.exception.custom.SystemException;
import com.malaka.aat.core.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.malaka.aat.internal.dto.test.TestDto;
import com.malaka.aat.internal.dto.test.TestQuestionUpdateDto;
import com.malaka.aat.internal.model.File;
import com.malaka.aat.internal.model.Test;
import com.malaka.aat.internal.model.TestQuestion;
import com.malaka.aat.internal.model.User;
import com.malaka.aat.internal.repository.TestQuestionRepository;
import com.malaka.aat.internal.util.FileValidationUtil;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final WordTestParserService wordTestParserService;
    private final TestQuestionRepository testQuestionRepository;
    private final SessionService sessionService;
    private final FileService fileService;

    @Transactional
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    public BaseResponse createTestFromWord(MultipartFile file) throws IOException {
        BaseResponse response = new BaseResponse();

        // Create test entity
        Test test = new Test();

        // Parse Word document and populate questions
        test = wordTestParserService.parseWordToTest(file, test);

        TestDto testDto = new TestDto(test);
        response.setData(testDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);

        return response;
    }


    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'TEACHER')")
    public BaseResponse updateQuestion(String questionId, TestQuestionUpdateDto dto) {
        BaseResponse response = new BaseResponse();
        TestQuestion testQuestion = findByIdWithAuthorization(questionId);
        if (dto.getQuestionText() != null) {
            testQuestion.setQuestionText(dto.getQuestionText());
        }
        if (dto.getImg() != null) {
            try {
                FileValidationUtil.validateImageFile(dto.getImg());
                File questionImg = fileService.save(dto.getImg());
                testQuestion.setQuestionImage(questionImg);
                testQuestion.setHasImage((short) 1);
            } catch (IOException e) {
                throw new SystemException(e.getMessage());
            }
        }

        testQuestionRepository.save(testQuestion);
        TestDto testDto = new TestDto(testQuestion.getTest());
        response.setData(testDto);
        ResponseUtil.setResponseStatus(response, ResponseStatus.SUCCESS);
        return response;
    }

    public TestQuestion findByIdWithAuthorization(String questionId) {
        TestQuestion testQuestion = testQuestionRepository.findById(questionId).orElseThrow(() -> new NotFoundException("Test question not found with id: " + questionId));
        User currentUser = sessionService.getCurrentUser();
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(role ->
                role.getName().equals("ADMIN") || role.getName().equals("SUPER_ADMIN"));
        boolean isTeacher = testQuestion.getInsuser().equals(currentUser.getId());
        if (!isTeacher && !isAdmin) {
            throw new AuthException("Current user has no authority to update this test question");
        }
        return testQuestion;
    }
}
