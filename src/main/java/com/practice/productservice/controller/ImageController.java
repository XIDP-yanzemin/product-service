package com.practice.productservice.controller;

import com.practice.productservice.controller.response.UploadImageResponse;
import com.practice.productservice.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UploadImageResponse uploadImage(
            HttpServletRequest request,
            @RequestParam("image") MultipartFile[] files) {
        return imageService.upload(request, files);
    }
}
