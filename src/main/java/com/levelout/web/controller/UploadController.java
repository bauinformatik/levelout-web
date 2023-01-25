package com.levelout.web.controller;

import com.levelout.web.model.UploadRequest;
import com.levelout.web.service.BimClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.websocket.server.PathParam;

@Controller
public class UploadController {
    @Autowired
    BimClientService bimClientService;

	@PostMapping(value = "/bim", consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> update(@RequestPart UploadRequest uploadRequest, @RequestPart MultipartFile bimFile) {
        bimClientService.updateForPid(uploadRequest, bimFile);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/bim", consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> create(@RequestPart UploadRequest uploadRequest, @RequestPart MultipartFile bimFile) {
        bimClientService.uploadNew(bimFile);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/bim/{pid}")
    public ResponseEntity<?> delete(@PathParam("pid") long pid) {
        bimClientService.deleteForPid(pid);
        return ResponseEntity.ok().build();
    }
}