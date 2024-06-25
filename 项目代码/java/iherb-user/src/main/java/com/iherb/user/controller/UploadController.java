package com.iherb.user.controller;

import com.iherb.common.utils.R;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("user/upload")
@CrossOrigin
public class UploadController {

    @Value("${upload.base-url}")
    private String BASE_URL;
    @Value("${upload.url-prefix}")
    private String URL_PREFIX;

    @GetMapping
    public R test() {
        System.out.println(BASE_URL);
        return R.ok();
    }

    @PostMapping("image")
    public R uploadImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return R.error(R.WX_ERROR_UPLOAD, "上传失败");
        }
        String originalName = image.getOriginalFilename();
        String suffix = originalName.substring(originalName.lastIndexOf("."));
        String filename = UUID.randomUUID().toString().trim().replaceAll("-", "");
        File file = new File(BASE_URL + "/" + filename + suffix);
        try {
            writeFile(image.getInputStream(), file);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(R.WX_ERROR_UPLOAD, "上传失败");
        }
        return R.ok().data("src", URL_PREFIX + "/" + filename + suffix);
    }

    private void writeFile(InputStream is, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
