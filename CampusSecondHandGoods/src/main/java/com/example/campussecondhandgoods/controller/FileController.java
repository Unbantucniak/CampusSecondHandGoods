package com.example.campussecondhandgoods.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/files")
public class FileController {
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            "image/webp",
            MediaType.IMAGE_GIF_VALUE
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.access-host:http://localhost:8080}")
    private String accessHost;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        if (file == null || file.isEmpty()) {
            result.put("code", 400);
            result.put("msg", "文件为空");
            return result;
        }
        try {
            validate(file.getContentType(), file.getOriginalFilename());
            String filename = store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
            result.put("code", 200);
            result.put("msg", "上传成功");
            result.put("data", Map.of("url", buildFileUrl(filename)));
        } catch (IllegalArgumentException e) {
            result.put("code", 400);
            result.put("msg", e.getMessage());
        } catch (IOException e) {
            result.put("code", 500);
            result.put("msg", "文件保存失败");
        }
        return result;
    }

    @PostMapping("/uploadByUrl")
    public Map<String, Object> uploadByUrl(@RequestBody Map<String, String> payload) {
        Map<String, Object> result = new HashMap<>();
        String remoteUrl = payload.get("url");
        if (remoteUrl == null || remoteUrl.trim().isEmpty()) {
            result.put("code", 400);
            result.put("msg", "url不能为空");
            return result;
        }
        try {
            URL url = new URL(remoteUrl.trim());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("User-Agent", "CampusSecondHandGoods/1.0");
            String contentType = connection.getContentType();
            validate(contentType, remoteUrl);
            try (InputStream inputStream = connection.getInputStream()) {
                String filename = store(inputStream, remoteUrl, contentType);
                result.put("code", 200);
                result.put("msg", "上传成功");
                result.put("data", Map.of("url", buildFileUrl(filename)));
            }
        } catch (IllegalArgumentException e) {
            result.put("code", 400);
            result.put("msg", e.getMessage());
        } catch (IOException e) {
            result.put("code", 500);
            result.put("msg", "下载远程图片失败");
        }
        return result;
    }

    private void validate(String contentType, String filename) {
        String lowerExt = extractExtension(filename);
        boolean extAllowed = lowerExt != null && ALLOWED_EXTENSIONS.contains(lowerExt);
        boolean typeAllowed = contentType != null && ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT));
        if (!extAllowed && !typeAllowed) {
            throw new IllegalArgumentException("仅支持 JPG/PNG/GIF/WebP 图片");
        }
    }

    private String store(InputStream inputStream, String originalName, String contentType) throws IOException {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        String extension = determineExtension(originalName, contentType);
        String filename = UUID.randomUUID().toString().replaceAll("-", "") + extension;
        Path target = uploadPath.resolve(filename);
        Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    private String determineExtension(String originalName, String contentType) {
        String ext = extractExtension(originalName);
        if (ext != null && ALLOWED_EXTENSIONS.contains(ext)) {
            return ext;
        }
        if (contentType != null) {
            switch (contentType.toLowerCase(Locale.ROOT)) {
                case MediaType.IMAGE_PNG_VALUE:
                    return ".png";
                case MediaType.IMAGE_GIF_VALUE:
                    return ".gif";
                case MediaType.IMAGE_JPEG_VALUE:
                    return ".jpg";
                case "image/webp":
                    return ".webp";
                default:
                    break;
            }
        }
        return ".jpg";
    }

    private String extractExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return null;
        }
        String clean = filename.toLowerCase(Locale.ROOT);
        int queryIndex = clean.indexOf('?');
        if (queryIndex != -1) {
            clean = clean.substring(0, queryIndex);
        }
        int fragmentIndex = clean.indexOf('#');
        if (fragmentIndex != -1) {
            clean = clean.substring(0, fragmentIndex);
        }
        int dotIndex = clean.lastIndexOf('.');
        if (dotIndex == -1) {
            return null;
        }
        return clean.substring(dotIndex);
    }

    private String buildFileUrl(String filename) {
        String base = accessHost != null ? accessHost.trim() : "";
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/uploads/" + filename;
    }
}
