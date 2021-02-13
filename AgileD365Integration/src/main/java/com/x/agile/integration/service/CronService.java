package com.x.agile.integration.service;

import com.x.agile.integration.bo.ProcessAXmlFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CronService
{
  @Autowired
  ProcessAXmlFiles processor;
  
  @Scheduled(fixedDelayString="${cron.agile.d365.fixeddelay}")
  public void sendAgileDatatoD365()
  {
    processor.execute();
  }
}