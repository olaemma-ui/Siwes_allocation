package com.siwes.allocation.Controller;

import com.siwes.allocation.Message.Response;
import com.siwes.allocation.Model.SiwesCenter;
import com.siwes.allocation.Service.SiwesCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("siwes_allocation/api/siwes_center/")
public class SiwesCenterController {

    private final SiwesCenterService siwesCenterService;

    @Autowired
    SiwesCenterController(SiwesCenterService siwesCenterService){
        this.siwesCenterService = siwesCenterService;
    }

    @PostMapping(value = "add")
    private ResponseEntity<Response> addSiwesCenter(@RequestParam String siwesCenter, @RequestParam MultipartFile logo, @RequestParam String sessionId){
        return siwesCenterService.addCenter(siwesCenter, logo, sessionId);
    }

    @GetMapping(value = "list")
    private ResponseEntity<Response> getAllCenter(@RequestParam String session, @RequestParam Integer pageNo, @RequestParam Integer pageSize){
        return siwesCenterService.getAllSiwesCenter(session, pageNo, pageSize);
    }

    @PutMapping(value = "edit")
    private ResponseEntity<Response> editSiwesCenter(@RequestParam String centerId, @RequestBody SiwesCenter center){
        return siwesCenterService.editSiwesCenter(centerId, center);
    }

    @DeleteMapping(value = "delete")
    private ResponseEntity<Response> deleteCenter(@RequestParam String centerId){
        return siwesCenterService.deleteSiwesCenter(centerId);
    }
}
