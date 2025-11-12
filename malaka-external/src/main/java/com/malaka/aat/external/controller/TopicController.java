package com.malaka.aat.external.controller;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.external.dto.test.attempt.TestAttemptRequestDto;
import com.malaka.aat.external.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/external")
@RequiredArgsConstructor
@RestController
public class TopicController {

    private final TopicService topicService;

    @GetMapping("/group/{groupId}/topic/{topicId}/content/stream")
    public ResponseEntity<Resource> streamTopicContent(
            @PathVariable String groupId,
            @PathVariable String topicId,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        return topicService.streamTopicContent(groupId, topicId, rangeHeader);
    }

    @GetMapping("/group/{groupId}/topic/{topicId}/presentation")
    public ResponseEntity<Resource> presentationFile(
            @PathVariable String groupId,
            @PathVariable String topicId
    ) {
        return topicService.getPresentationFile(groupId, topicId);
    }

    @GetMapping("/group/{groupId}/topic/{topicId}/lecture")
    public ResponseEntity<Resource> lectureFile(
            @PathVariable String groupId,
            @PathVariable String topicId
    ) {
        return topicService.getLectureFile(groupId, topicId);
    }

    @GetMapping("/group/{groupId}/topic/{topicId}/test")
    public BaseResponse test(@PathVariable String groupId, @PathVariable String topicId) {
        return topicService.getTopicTest(groupId, topicId);
    }

    @PostMapping("/group/{groupId}/topic/{topicId}/test/attempt")
    public BaseResponse testAttempt(@PathVariable String groupId,
                                    @PathVariable String topicId,
                                    @RequestBody @Validated TestAttemptRequestDto testAttemptDto
                                    ) {
        return topicService.testAttempt(groupId, topicId, testAttemptDto);
    }

    @GetMapping("/group/{groupId}/topic/{topicId}/test/attempt")
    public BaseResponse getTestAttempts(
            @PathVariable String groupId,
            @PathVariable String topicId
    ) {
        return topicService.testAttemptList(groupId, topicId);
    }
}
