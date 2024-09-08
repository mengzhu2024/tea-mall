package com.graduation.config;

import com.graduation.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@RequestMapping("/file")
public class FileConfig implements WebMvcConfigurer {

    @ResponseBody
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String[] strs = file.getOriginalFilename().split("\\.");
        String newFileName = System.currentTimeMillis() + "." + strs[strs.length - 1];
        file.transferTo(new File(System.getProperty("user.dir") + "\\src\\main\\resources\\static\\file\\" + newFileName));
        return new Result<>(newFileName);
    }

    @ResponseBody
    @GetMapping("/download")
    public void download(String url, HttpServletResponse response) {
        //设置文件路径
        File file = new File(System.getProperty("user.dir") + "\\src\\main\\resources\\static\\file\\" + url);
        if (file.exists()) {
            response.setContentType("APPLICATION/OCTET-STREAM;charset=UTF-8");
            response.addHeader("Content-Disposition", "attachment;fileName=file");
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream os = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    os.write(buffer, 0, i);
                    i = bis.read(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "\\src\\main\\resources\\static\\file\\")
                .resourceChain(false);
    }
}
