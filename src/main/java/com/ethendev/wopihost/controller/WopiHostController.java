package com.ethendev.wopihost.controller;

import com.ethendev.wopihost.entity.FileInfo;
import com.ethendev.wopihost.service.WopiHostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * WOPI HOST mian controller
 *
 * @author hamedmohammdi3
 * @date 2023/4/15
 */
@RestController
@RequestMapping(value = "/wopi")
public class WopiHostController {

    @Value("${file.path}")
    private String filePath;

    private WopiHostService wopiHostService;

    @Autowired
    public WopiHostController(WopiHostService wopiHostService) {
        this.wopiHostService = wopiHostService;
    }

    /**
     * search a file from the host, return a file’s binary contents
     */
    @GetMapping("/files/{name}/contents")
    public void getFile(@PathVariable String name, HttpServletResponse response) {
        wopiHostService.getFile(name, response);
    }

    /**
     * The postFile operation updates a file’s binary contents.
     */
    @PostMapping("/files/{name}/contents")
    public void postFile(@PathVariable(name = "name") String name, @RequestBody byte[] content,
                         HttpServletRequest request) {
        wopiHostService.postFile(name, content, request);
    }

    /**
     * returns information about a file, a user’s permissions on that file,
     * and general information about the capabilities that the WOPI host has on the file.
     */
    @GetMapping("/files/{name}")
    public ResponseEntity<FileInfo> checkFileInfo(@PathVariable(name = "name") String name) throws Exception {
        return wopiHostService.getFileInfo(name);
    }

    /**
     * Handling lock related operations
     */
    @PostMapping("/files/{name}")
    public ResponseEntity handleLock(@PathVariable(name = "name") String name, HttpServletRequest request) {
        return wopiHostService.handleLock(name, request);
    }


    @PostMapping("/files/upload/{name}")
    public String uploadFile(@RequestParam("file") MultipartFile file,@PathVariable(name = "name") String fileName) {
        String uploadPath = filePath + File.separator + fileName;
        try (FileOutputStream out = new FileOutputStream(new File(uploadPath))) {
            out.write(file.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload file";
        }
        return "File " + fileName + " has been uploaded successfully!";
    }

    @DeleteMapping("/files/delete/{name}")
    public String deleteFile(@PathVariable(name = "name") String fileName) {
        File file = new File(filePath + File.separator + fileName);
        if (file.delete()) {
            return "File " + fileName + " has been deleted successfully!";
        } else {
            return "Failed to delete file";
        }
    }
}