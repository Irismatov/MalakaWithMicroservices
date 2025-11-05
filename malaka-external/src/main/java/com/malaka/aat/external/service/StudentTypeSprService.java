package com.malaka.aat.external.service;

import com.malaka.aat.core.exception.custom.NotFoundException;
import com.malaka.aat.external.model.spr.StudentTypeSpr;
import com.malaka.aat.external.repository.spr.StudentTypeSprRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StudentTypeSprService {

    private final StudentTypeSprRepository studentTypeSprRepository;

    public StudentTypeSpr findByName(String name) {
        Optional<StudentTypeSpr> byName = studentTypeSprRepository.findByName(name);
        return byName.orElseThrow(() -> new NotFoundException("Student type spr not found with name: " + name));
    }

    public StudentTypeSpr findById(Long id) {
        return studentTypeSprRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Student type spr not found with id: " + id) );
    }
}
