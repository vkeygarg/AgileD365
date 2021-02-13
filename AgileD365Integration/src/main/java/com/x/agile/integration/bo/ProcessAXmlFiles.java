package com.x.agile.integration.bo;

import com.x.agile.integration.service.JsonDataService;
import com.x.agile.integration.service.RestApiService;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProcessAXmlFiles
{
  static final Logger logger = Logger.getLogger(ProcessAXmlFiles.class);
  final SimpleDateFormat tmFrmt = new SimpleDateFormat("yyyyMMdd:hhmmsss");
  @Autowired
  JsonDataService datService;
  @Autowired
  RestApiService restApiService;
  @Value("${agile.in.dir}")
  private String inputFldr;
  
  public void execute()
  {
    logger.info("Job Start: " + tmFrmt.format(new Date()));
    List<Path> unProJsonFiles = datService.getUnprocesedData(inputFldr);
    
    logger.trace("Json Files to Process:" + unProJsonFiles.size());
    if (!unProJsonFiles.isEmpty())
    {
      restApiService.sendFiles(unProJsonFiles);
      datService.cleanDirectories();
    }
    logger.info("Job End: " + tmFrmt.format(new Date()));
  }
}

/* Location:
 * Qualified Name:     com.x.agile.integration.bo.ProcessAXmlFiles
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */