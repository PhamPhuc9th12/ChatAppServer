package com.example.Othellodifficult.controller;

import com.example.Othellodifficult.dto.TagOutput;
import com.example.Othellodifficult.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@AllArgsConstructor
@RestController
@RequestMapping("api/v1/tag")
public class TagController {
    private final TagService tagService;
    @Operation(summary = " lấy danh sách tag")
    @GetMapping()
    public Page<TagOutput> getAllTag(@ParameterObject Pageable pageable){
        return tagService.getAllTag(pageable);
    }
}
