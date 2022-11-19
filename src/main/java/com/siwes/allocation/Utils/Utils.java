package com.siwes.allocation.Utils;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

@Component
public class Utils {


    /**
     * Generate random unique randomString
     * from the pattern provided
     * @apiNote utils.genId(sessionRepo.findAllId(), new int[]{5,5})
     * */
    public String genId(List<String> prevData, int[] pattern){
        AtomicBoolean gen = new AtomicBoolean(true);
        String alpha = "";
        if (pattern.length != 2){
            return null;
        }
        while(gen.get()){
            String id = "";
            gen.set(false);
            for(int j = 0; j < pattern[0]; j++){
                int k = (new Random().nextInt(90+1 - 65 )+65);
                alpha+=(char) k;
            }
            for (int i = 0; i < pattern[1]; i++) {
                int k = (new Random().nextInt(9));
                alpha+=k;
            }
            for (String prevId: prevData) {
                if (prevId.equalsIgnoreCase(alpha)){
                    gen.set(true);
                    alpha = "";
                }
            }
        }
        return alpha;
    }


    /**
     * This method validate fields. <br>
     * This method accepts the POJO class / Model that will be validated
     * @param field this is the class that needs to be validated
     * @apiNote The name should be descriptive for what it is used for and must contain the name.
     * @example Email-address, email, email_address.
     * @fields [email, password, name, mobile, country-code, text]
     * */
    public Object[] validate(Object field, String[] ignore){
        AtomicBoolean valid = new AtomicBoolean(true);
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Map<String, Object> data = mapper.convertValue(field, Map.class);

        //== == == Fields to be ignored during validation
        for (String s : ignore) {
            data.remove(s);
        }

        //Regex patter for each fields.
        Map<String, String[]> regex = new HashMap<>();
        regex.put("email", new String[]{
                "^(.+)@(.+)$",
                "Invalid E-mail address"
        });

        regex.put("password", new String[]{
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$",
                "Must contain at least aA-zZ, 0-9 and special characters"
        });

        regex.put("name", new String[]{
                "^[A-Za-z][A-Za-z\\'\\-]+([\\ A-Za-z][A-Za-z\\'\\-]+)*",
                "Name can only be from [a-z A-Z'-]"
        });

        regex.put("mobile", new String[]{
                "^\\\\+?[1-9][0-9]{7,14}$",
                "Mobile number can only be 11 digits and b/w [0-9]"
        });

        regex.put("countryCode", new String[]{
                "^(\\+?\\d{1,3}|\\d{1,4})$",
                "Invalid country code"
        });

        regex.put("text", new String[]{
                "\\w",
                "Invalid text value"
        });
        System.out.println("data = "+data);
        data.forEach(
                (k,v)->{
                    if (data.get(k) == null){
                        valid.set(false);
                        data.put(k, "This field is required");
                    }else if (data.get(k).toString().trim().isEmpty()){
                        valid.set(false);
                        data.put(k, "This field is required");
                    }
                    else{
                        //Checks if the model contains the regex fields
                        regex.forEach(
                                (k1, v1)->{
                                    boolean text = k.contains("text")
                                            ? data.replace(k, v, htmlSpecialChars(v.toString(), false))
                                            : data.replace(k, v, htmlSpecialChars(v.toString(), true));

                                    if (k.toLowerCase().contains(k1.toLowerCase())){
                                        if (Pattern.compile(regex.get(k1)[0]).matcher(v.toString()).find()){
                                            data.replace(k, null);
                                        }else{
                                            valid.set(false);
                                            data.replace(k, regex.get(k1)[1]);
                                        }
                                    }else data.replace(k, null);
                                }
                        );
                    }
                }
        );
        return new Object[]{valid.get(), data};
    }


    /**
     * This method replace unwanted html characters to the text value
     * @param data this is data that will be stripped
     * */
    private String htmlSpecialChars(String data, boolean remove){
        data = data.replace("&", (remove) ? "" : "&amp;");
        data.replace("\"", (remove) ? "" : "&quot;");
        data.replace("<", (remove) ? "" : "&lt;");
        data.replace(">", (remove) ? "" : "&gt;");
        data.replace(">", (remove) ? "" : "&gt;");
        data.replace("=", (remove) ? "" : "&#61;");
        return data;
    }

//    public

}
