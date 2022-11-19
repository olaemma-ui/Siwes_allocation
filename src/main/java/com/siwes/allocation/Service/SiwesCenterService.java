package com.siwes.allocation.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siwes.allocation.Message.Response;
import com.siwes.allocation.Model.SiwesCenter;
import com.siwes.allocation.Repository.SessionRepo;
import com.siwes.allocation.Repository.SiwesCenterRepo;
import com.siwes.allocation.Repository.StudentRepo;
import com.siwes.allocation.Utils.Utils;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class SiwesCenterService {
    private Boolean success;
    private String code;
    private String message;
    private Object data;
    private Object error;

    private final ObjectMapper mapper;
    private final Utils utils;
    private final SiwesCenterRepo siwesCenterRepo;
    private final SessionRepo sessionRepo;
    private final StudentRepo studentRepo;

    SiwesCenterService(ObjectMapper mapper, Utils utils, SiwesCenterRepo siwesCenterRepo, SessionRepo sessionRepo, StudentRepo studentRepo){
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper = mapper;
        this.utils = utils;
        this.siwesCenterRepo = siwesCenterRepo;
        this.sessionRepo = sessionRepo;
        this.studentRepo = studentRepo;
    }

    /**
     *
     * */
    public ResponseEntity<Response> addCenter(String center, MultipartFile logo, String sessionId){
        reset();

        try {
            SiwesCenter siwesCenter = mapper.readValue(center, SiwesCenter.class);
            siwesCenter.setLogo(Base64.getEncoder().encode(logo.getBytes()));
            siwesCenter.setFileName(logo.getOriginalFilename());

            Object[] validate = utils.validate(siwesCenter, new String[]{"createdAt", "updatedAt", "id", "session"});
            error = validate[1];
            data = siwesCenter;

            if (Boolean.parseBoolean(validate[0].toString())){
               try{
                   message = "Session does not exist!";
                   sessionRepo.findById(sessionId).ifPresent(
                       session -> {
                           if (!siwesCenterRepo.findByName(siwesCenter.getName(), session.getId()).isPresent()){

                               siwesCenter.setId(UUID.randomUUID().toString());
                               siwesCenter.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
                               siwesCenter.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                               siwesCenter.setSession(session);

                               siwesCenterRepo.save(siwesCenter);
                               success(data);

                           }else message = "SIWES center already exist!";
                       }
                   );
               }catch (Exception e){
                   e.printStackTrace();
                   code = "500";
                   message = "Sorry an error occurred!";
                   data = null;
               }
            }else message = "Invalid fields";
        } catch (IOException e) {
            e.printStackTrace();
            code = "500";
            message = "Invalid fields / format";
        }
        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }

    /**
     *
     * */
    public ResponseEntity<Response> getAllSiwesCenter(String sessionId, Integer pageNo, Integer pageSize){
        reset();
        message = "Session is required";
        Optional.ofNullable(sessionId).ifPresent(
            id->{
                message = "Invalid session!";
                try{
                    sessionRepo.findById(id).ifPresent(
                            session -> {
                                success(
                                        siwesCenterRepo.findBySession(
                                                id, PageRequest.of(
                                                        Optional.ofNullable(pageNo).orElse(0),
                                                        Optional.ofNullable(pageSize).orElse(10)
                                                )
                                        )
                                );
                            }
                    );
                }catch (Exception e){
                    code = "500";
                    message = "Sorry an error occurred!";
                }
            }
        );
        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }


    /**
     *
     * */
    public ResponseEntity<Response> deleteSiwesCenter(String id){
        reset();
        message = "SIWES center required";
        Optional.ofNullable(id).ifPresent(
            sId->{
                   try{
                       message = "Invalid SIWES center!";
                       siwesCenterRepo.findById(id).ifPresent(
                               siwesCenter -> {
                                   studentRepo.updateStatus(sId, "PENDING");
                                   siwesCenterRepo.deleteById(id);
                                   success(null);
                               }
                       );
                   }catch (Exception e){
                       message = "Sorry an error occurred!";
                       code = "500";
                       e.printStackTrace();
                   }
            }
        );
        return new ResponseEntity<>(new Response(success, code, message, data, error), HttpStatus.OK);
    }


    /**
     *
     * */
    public ResponseEntity<Response> editSiwesCenter(String centerId, SiwesCenter siwesCenter){
        reset();
        System.out.println(centerId);
        message = "SIWES center required";
        Optional.ofNullable(centerId).ifPresent(
                sId->{
                    try{
                        message = "Invalid SIWES center!";
                        siwesCenterRepo.findById(centerId).ifPresent(
                                center -> {
                                    Object[] validate = utils.validate(siwesCenter, new String[]{"createdAt", "updatedAt", "id", "session", "logo", "fileName"});
                                    error = validate[1];
                                    data = siwesCenter;
                                    if (Boolean.parseBoolean(validate[0].toString())){
                                        if (siwesCenterRepo.findByName(siwesCenter.getName(), center.getSession().getId()).isPresent()){

                                            siwesCenter.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
                                            siwesCenter.setLogo(center.getLogo());
                                            siwesCenter.setFileName(center.getFileName());

                                            siwesCenterRepo.save(siwesCenter);
                                            siwesCenter.setLogo(null);
                                            siwesCenter.setFileName(null);
                                            success(data);

                                        }else message = "SIWES center already exist!";
                                    }else message = "Invalid fields!";

                                }
                        );
                    }catch (Exception e){
                        message = "Sorry an error occurred!";
                        code = "500";
                        e.printStackTrace();
                    }
                }
        );
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
