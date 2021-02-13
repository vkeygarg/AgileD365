package com.x.agile.integration.service;

import com.x.agile.integration.exception.CustomException;
import java.io.IOException;
import com.x.agile.integration.utility.Utility;
import java.nio.file.Paths;
import com.x.agile.integration.dto.D365LoginResponse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import com.x.agile.integration.client.ServiceClient;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class RestApiService
{
    static final Logger logger;
    @Autowired
    ServiceClient apiClient;
    @Value("${agile.out.dir}")
    private String outputFldr;
    @Value("${agile.in.dir}")
    private String inFldr;
    @Value("${agile.json.dir}")
    private String jsonFldr;
    @Value("${agile.fail.dir}")
    private String failFldr;
    @Value("${agile.pass.dir}")
    private String passFldr;
    @Value("${agile.src.file.ext}")
    private String srcFileExt;
    @Value("${agile.src.file.prefix}")
    private String srcFilePreFix;
    @Value("${agile.out.file.ext}")
    private String outFileExt;
    @Value("${agile.out.file.prefix}")
    private String outFilePreFix;
    
    public void sendFiles(final List<Path> unProJsonFiles) {
        final D365LoginResponse loginResponse = this.apiClient.getLoginToken();
        RestApiService.logger.info(("--->>>> Access token:" + loginResponse.toString()));
        unProJsonFiles.forEach(rec -> sendFileandClean(rec, loginResponse.getAccess_token()));
    }
    
    private void sendFileandClean(final Path rec, final String accesstoken) {
        try {
            final String res = apiClient.performPostRequest(rec, accesstoken);
            if ("true".equalsIgnoreCase(res)) {
                final String orgFileName = rec.getFileName().toString().replace(outFilePreFix, srcFilePreFix).replace(outFileExt, srcFileExt);
                final Path inFile = Paths.get(this.inFldr).resolve(orgFileName);
                Utility.moveFile(inFile.toAbsolutePath().toString(), outputFldr);
                if (rec.toFile().exists()) {
                	Files.delete(rec);
                }
            }
        }
        catch (IOException | CustomException e) {
            RestApiService.logger.error(e.getMessage(), e);
        }
    }
    
    static {
        logger = Logger.getLogger(RestApiService.class);
    }
}