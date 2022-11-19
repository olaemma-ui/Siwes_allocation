package com.siwes.allocation.Controller;

import com.siwes.allocation.Message.Response;
import com.siwes.allocation.Model.Session;
import com.siwes.allocation.Service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("siwes_allocation/api/session/")
public class SessionController {

    private final SessionService sessionService;

    @Autowired
    SessionController(SessionService sessionService){
        this.sessionService = sessionService;
    }

    @PostMapping(value = "add")
    private ResponseEntity<Response> addSession(@RequestBody Session session){
        return sessionService.addSession(session);
    }

    @PutMapping(value = "{id}/edit")
    private ResponseEntity<Response> editSession(@RequestBody Session session, @PathVariable String id){
        return sessionService.editSession(session, id);
    }

    @DeleteMapping(value = "{id}/delete")
    private ResponseEntity<Response> deleteSession(@PathVariable String id){
        return sessionService.deleteSession(id);
    }

    @GetMapping(value = "list")
    private ResponseEntity<Response> getAllSession(@RequestParam Integer pageNo, @RequestParam Integer pageSize){
        return sessionService.getAllSession(pageNo, pageSize);
    }
}
