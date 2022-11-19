package com.siwes.allocation.Service;

import com.siwes.allocation.Message.Response;
import com.siwes.allocation.Model.Session;
import com.siwes.allocation.Repository.SessionRepo;
import com.siwes.allocation.Utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SessionService {

    private Boolean success;
    private String code;
    private String message;
    private Object data;
    private Object error;

    private final Utils utils;
    private final SessionRepo sessionRepo;

    @Autowired
    SessionService(Utils utils, SessionRepo sessionRepo){
        this.utils = utils;
        this.sessionRepo = sessionRepo;
    }

    /**
     * Add new session
     * */
    public ResponseEntity<Response> addSession(Session session){
        reset();
        Object[] validate = utils.validate(session, new String[]{"createdAt", "updatedAt", "id", "session"});
        error = validate[1];
        data = session;

        if (Boolean.parseBoolean(validate[0].toString())){
            try {

                if (session.getYear() <= LocalDate.now().getYear()){

                    if(!sessionRepo.findByYear(session.getYear()).isPresent()){

                        session.setId(UUID.randomUUID().toString());
                        session.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        session.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                        session.setSession(session.getYear() +"/"+ (session.getYear()+1));

                        sessionRepo.save(session);
                        success(data);
                    }else message = "Session already exist";

                }else message = "Invalid session";

            }catch (Exception e){
                e.printStackTrace();
                data = null;
                code = "500";
                message = "Error occurred connecting to server!!!";
            }
        }else message = "Invalid fields/parameters";

        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }


    /**
    * Edit session details
    * */
    public ResponseEntity<Response> editSession(Session session, String id){
        reset();

        message = "session {id} parameter required";
        Optional.of(id).ifPresent(
            e->{
                Object[] validate = utils.validate(session, new String[]{"updatedAt", "id", "session"});
                error = validate[1];
                data = session;

                if (Boolean.parseBoolean(validate[0].toString())){
                    try {
                        if (session.getYear() <= LocalDate.now().getYear()){

                            if(!sessionRepo.findByYear(session.getYear()).isPresent()){
                                session.setId(e);
                                session.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                                session.setSession(session.getYear() +"/"+ (session.getYear()+1));

                                sessionRepo.save(session);
                                success(data);
                            }else message = "session already exist";

                        }else message = "Invalid session";

                    }catch (Exception ex){
                        ex.printStackTrace();
                        code = "500";
                        data = null;
                        message = "Please wait Error occurred";
                    }
                }else message = "Invalid fields/parameters";
            }
        );

        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }


    public ResponseEntity<Response> deleteSession(String id){
        reset();
        try {
            message = "session {id} parameter required";
            Optional.of(id).ifPresent(
               i->{
                   message = "Invalid session";
                   sessionRepo.findById(id).ifPresent(
                           e->{
                               sessionRepo.deleteById(id);
                               success(null);
                           }
                   );
               }
            );
        }catch (Exception e){
            e.printStackTrace();
            data = null;
            code = "500";
            message = "Error occurred connecting to server!!!";
        }
        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }

    /**
     * Get all session
     * */
    public ResponseEntity<Response> getAllSession(Integer pageNo, Integer pageSize){
        reset();
        try {

            success(
                sessionRepo.findAll(
                    PageRequest.of(
                            Optional.of(pageNo).orElse(0),
                            Optional.of(pageSize).orElse(10)
                    )
                )
            );
        }catch (Exception e){
            e.printStackTrace();
            code = "500";
            data = null;
            message = "Sorry an error occurred";
        }
        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }


    private void reset(){
        success = false;
        code = "99";
        message = "FAILED";
        data = null;
        error = null;
    }
    private void success(Object data){
        message = "Success";
        success = true;
        code = "100";
        this.data = data;
    }
}
