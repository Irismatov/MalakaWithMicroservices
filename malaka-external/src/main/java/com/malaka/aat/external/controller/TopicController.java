package com.malaka.aat.external.controller;

import com.malaka.aat.external.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RequestMapping("/api/external")
@RequiredArgsConstructor
@RestController
public class TopicController {

    private final TopicService topicService;

    @GetMapping("/topic/{topicId}/content/stream")
    public ResponseEntity<Resource> streamTopicContent(
            @PathVariable String topicId,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        return topicService.streamTopicContent(topicId, rangeHeader);
    }

    @GetMapping("/topic/{topicId}/presentation")
    public ResponseEntity<Resource> presentationFile(
            @PathVariable String topicId
    ) {
        return topicService.getPresentationFile(topicId);
    }

    @GetMapping("/topic/{topicId}/lecture")
    public ResponseEntity<Resource> lectureFile(
            @PathVariable String topicId
    ) {
        return topicService.getLectureFile(topicId);
    }
}
