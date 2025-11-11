package com.malaka.aat.internal.controller;

import com.malaka.aat.core.dto.BaseResponse;
import com.malaka.aat.internal.dto.topic.ChunkUploadResponseDto;
import com.malaka.aat.internal.dto.topic.InitChunkUploadDto;
import com.malaka.aat.internal.dto.topic.TopicUpdateDto;
import com.malaka.aat.internal.model.BaseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.malaka.aat.internal.dto.test.TestCreateDto;
import com.malaka.aat.internal.dto.topic.TopicContentUploadDto;
import com.malaka.aat.internal.service.TopicService;

import java.io.IOException;

@Tag(name = "Mavzular boshqaruvi", description = "Kurs modul mavzularini boshqarish uchun API'lar")
@RequiredArgsConstructor
@RequestMapping("/api/topic")
@RestController
public class TopicController {

    private final TopicService topicService;

    @Operation(summary = "Ma'ruza faylini yuklash",
            description = "Mavzu uchun ma'ruza matn faylini (word, txt, pdf va h.k.) yuklash")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    @PostMapping(value = "/{topicId}/lecture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse uploadLectureFile(
            @PathVariable String topicId,
            @RequestParam("lectureFile") MultipartFile lectureFile) {
        return topicService.uploadLectureFile(topicId, lectureFile);
    }

    @Operation(summary = "Mavzu uchun test saqlash",
            description = "Word hujjatidan tahlil qilingan test savollari va javob variantlarini rasm fayllari bilan birga saqlash")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    @PostMapping(value = "/{topicId}/test")
    public BaseResponse uploadTest(@PathVariable String topicId, @RequestBody @Validated TestCreateDto dto) {
        return topicService.saveTest(topicId, dto);
    }

    @Operation(summary = "Video/audio kontent yuklash",
            description = "Mavzu uchun video yoki audio kontent faylini yuklash")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    @PostMapping(value = "/{topicId}/content", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse uploadContentFile(
            @PathVariable String topicId,
            @ModelAttribute @Validated TopicContentUploadDto dto
            ) throws IOException {
        return topicService.uploadContentFile(topicId, dto);
    }

    @Operation(summary = "Bo'laklab yuklashni boshlash",
            description = "Video yoki audio faylni bo'laklab yuklashni boshlash va upload ID olish")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    @PostMapping(value = "/{topicId}/content/chunk/init")
    public BaseResponse initChunkUpload(
            @PathVariable String topicId,
            @RequestBody @Validated InitChunkUploadDto dto
    ) {
        return topicService.initChunkUpload(topicId, dto);
    }

    @Operation(summary = "Video/audio bo'lagini yuklash",
            description = "Video yoki audio faylning bir bo'lagini yuklash")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    @PostMapping(value = "/{topicId}/content/chunk/{uploadId}/{chunkNumber}",
                 consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse uploadChunk(
            @PathVariable String topicId,
            @PathVariable String uploadId,
            @PathVariable Integer chunkNumber,
            @RequestParam("chunk") MultipartFile chunk
    ) throws IOException {
        return topicService.uploadChunk(topicId, uploadId, chunkNumber, chunk);
    }

    @Operation(summary = "Bo'laklab yuklashni tugatish",
            description = "Barcha bo'laklarni birlashtirish va faylni saqlash")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    @PostMapping(value = "/{topicId}/content/chunk/finalize/{uploadId}")
    public BaseResponse finalizeChunkUpload(
            @PathVariable String topicId,
            @PathVariable String uploadId
    ) throws IOException {
        return topicService.finalizeChunkUpload(topicId, uploadId);
    }

    @Operation(summary = "Video kontentni oqim orqali ko'rish",
            description = "Mavzuning video yoki audio kontentini Range qo'llab-quvvatlash bilan oqim orqali ko'rish")
    @GetMapping(value = "/{topicId}/content/stream")
    public ResponseEntity<Resource> streamContentFile(
            @PathVariable String topicId,
            @RequestHeader(value = "Range", required = false) String rangeHeader
    ) {
        return topicService.streamTopicContent(topicId, rangeHeader);
    }

    @Operation(summary = "Taqdimot yuklash",
            description = "Mavzu uchun taqdimot faylini yuklash")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    @PostMapping(value = "/{topicId}/presentation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse uploadPresentationFile(
            @PathVariable String topicId,
            @RequestParam("presentationFile") MultipartFile presentationFile
    ) {
        return topicService.uploadPresentationFile(topicId, presentationFile);
    }

    @GetMapping(value = "/{topicId}/content")
    public ResponseEntity<Resource> getContentFile(
            @PathVariable String topicId
    ) {
        return topicService.getTopicContentWithHeaders(topicId);
    }

    @GetMapping(value = "/{topicId}/presentation")
    public ResponseEntity<Resource> getPresentationFile(
            @PathVariable String topicId
    ) {
        Resource topicPresentationAsResource = topicService.getTopicPresentationAsResource(topicId);

        String filename = topicPresentationAsResource.getFilename();
        if (filename == null) {
            filename = "presentation.pdf";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(topicPresentationAsResource);
   }


    @GetMapping(value = "/{topicId}/lecture")
    public ResponseEntity<Resource> getLectureFile(
            @PathVariable String topicId
    ) {
        Resource topicLectureAsResource = topicService.getTopicLectureFileAsResource(topicId);

        String filename = topicLectureAsResource.getFilename();
        if (filename == null) {
            filename = "lecture.pdf";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(topicLectureAsResource);
    }


    @GetMapping("/{id}/test")
    public BaseResponse getTestFile(
            @PathVariable String id
    ) {
        return topicService.getTopicTestById(id);
    }


    @Operation(summary = "Mavzuni ID bo'yicha olish",
            description = "Mavzu ma'lumotlarini ID orqali olish")
    @GetMapping("/{id}")
    public BaseResponse getTopicById(@PathVariable String id) {
        return topicService.getTopicById(id);
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{id}")
    public BaseResponse updateTopic(@PathVariable String id, @RequestBody TopicUpdateDto dto) {
        return topicService.update(id, dto);
    }

    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public BaseResponse deleteTopic(@PathVariable String id) {
        return topicService.deleteTopic(id);
    }


}
