package com.practice.productservice.service;

import com.practice.productservice.constant.Constant;
import com.practice.productservice.exception.BusinessException;
import com.practice.productservice.exception.ErrorCode;
import com.practice.productservice.response.UploadImageResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageService {

    @Value("${spring.servlet.multipart.max-file-size}")
    private Long maxSize;

    @Value("${file-save-path}")
    private String fileSavePath;

    public UploadImageResponse upload(HttpServletRequest request, MultipartFile[] files) throws BusinessException {
        List<String> responses = new ArrayList<>();
        for (MultipartFile file : files) {
            checkBeforeSave(file);

            File dir = new File(fileSavePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String newFileName = generateNewFileName(file);

            File dest = new File(dir, newFileName);
            transferFile(file, dest);
            responses.add(getImageUrlFrom(request, newFileName));
        }
        return new UploadImageResponse(responses);
    }

    private static void transferFile(MultipartFile file, File dest) {
        try {
            file.transferTo(dest);
        } catch (IllegalStateException e) {
            throw new BusinessException(ErrorCode.IMAGE_STATE_EXCEPTION);
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.IMAGE_UPLOAD_EXCEPTION);
        }
    }

    private static String getImageUrlFrom(HttpServletRequest request, String newFileName) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/images/" + newFileName;
    }

    private static String generateNewFileName(MultipartFile file) {
        String suffix = Objects.requireNonNull(file.getOriginalFilename()).trim().substring(file.getOriginalFilename().lastIndexOf("."));
        return UUID.randomUUID().toString().replaceAll("-", "") + suffix;
    }

    private void checkBeforeSave(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.IMAGE_EMPTY_EXCEPTION);
        }
        if (file.getSize() > maxSize) {
            throw new BusinessException(ErrorCode.IMAGE_SIZE_EXCEPTION);
        }
        if (!Constant.IMAGE_TYPES.contains(file.getContentType())) {
            throw new BusinessException(ErrorCode.IMAGE_TYPE_EXCEPTION);
        }
    }
}
