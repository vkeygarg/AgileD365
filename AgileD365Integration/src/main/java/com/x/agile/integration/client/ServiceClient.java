package com.x.agile.integration.client;

import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import org.json.JSONException;
import org.json.XML;
import com.x.agile.integration.exception.CustomException;
import org.json.JSONObject;
import java.nio.file.Path;
import org.springframework.web.client.RestClientException;
import org.springframework.http.HttpMethod;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import com.x.agile.integration.dto.D365LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ServiceClient
{
    static final Logger logger;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${d365.login.token.endpoint}")
    private String tokenEndPoint;
    @Value("${d365.login.token.clientid}")
    private String clientId;
    @Value("${d365.login.token.clientsecret}")
    private String clientSecret;
    @Value("${d365.login.token.scope}")
    private String scope;
    @Value("${d365.post.json.endpoint}")
    private String d365PostDataEndPoint;
    
    public D365LoginResponse getLoginToken() {
        logger.debug("Start getLoginToken.. ");
        final ResponseEntity<D365LoginResponse> response = this.getAuthToken();
        logger.debug("Return from getLoginToken.. ");
        return response.getBody();
    }
    
    public ResponseEntity<D365LoginResponse> getAuthToken() {
        logger.debug("Start getAuthToken.. ");
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final MultiValueMap<String, String> requestBodyMap = new LinkedMultiValueMap<>();
        requestBodyMap.add("grant_type", "Client_Credentials");
        requestBodyMap.add("client_id", this.clientId);
        requestBodyMap.add("client_secret", this.clientSecret);
        requestBodyMap.add("scope", this.scope);
        final HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestBodyMap, (MultiValueMap<String, String>)headers);
        ResponseEntity<D365LoginResponse> response = null;
        try {
            response = restTemplate.exchange(this.tokenEndPoint, HttpMethod.POST, entity, D365LoginResponse.class);
        }
        catch (RestClientException e) {
            logger.error(e.getMessage(), e);
        }
        logger.debug("Return from getAuthToken.. ");
        return response;
    }
    
    public String performPostRequest(final Path jsonFile, final String authToken) throws CustomException {
        logger.debug(("Start performGetRequest.. for file: " + jsonFile.getFileName().toString()));
        try {
            final HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authToken);
            headers.setContentType(MediaType.APPLICATION_JSON);
            final String reqJson = xmlToJson(jsonFile);
            final HttpEntity<String> entity = new HttpEntity<>(new JSONObject(reqJson).toString(), (MultiValueMap<String, String>)headers);
            final String res = restTemplate.postForObject(d365PostDataEndPoint, entity, String.class);
            logger.debug(("Return from performPostRequest.. " + res));
            return res;
        }
        catch (Exception e) {
            logger.trace("Exception trace: {}", e);
            throw new CustomException("Could not post message", e);
        }
    }
    
    private String xmlToJson(final Path f) {
        String resStr = "";
        try {
            final JSONObject xmlJSONObj = XML.toJSONObject(readLineByLine(f.toAbsolutePath().toString()),true);
            final JSONObject resjson = new JSONObject();
            resjson.put("changes", xmlJSONObj.toString());
            resStr = resjson.toString();
            logger.info(resStr);
        }
        catch (JSONException je) {
            logger.error(je.getMessage(), je);
        }
        return resStr;
    }
    
    static String stripExtension(final String str) {
        if (str == null) {
            return null;
        }
        final int pos = str.lastIndexOf(46);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }
    
    private String readLineByLine(final String filePath) {
        final StringBuilder contentBuilder = new StringBuilder();
        try (final Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return contentBuilder.toString();
    }
    
    static {
        logger = Logger.getLogger(ServiceClient.class);
    }
}