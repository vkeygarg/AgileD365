package com.x.agile.integration.service;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.x.agile.integration.utility.Utility;

@Service
public class JsonDataService
{
    static final Logger logger;
    @Value("${agile.src.file.ext}")
    private String srcFileExt;
    @Value("${agile.src.file.prefix}")
    private String srcFilePreFix;
    @Value("${agile.out.file.ext}")
    private String outFileExt;
    @Value("${agile.out.file.prefix}")
    private String outFilePreFix;
    @Value("${agile.json.dir}")
    private String jsonDir;
    @Value("${agile.fail.dir}")
    private String failDir;
    
    public List<Path> getUnprocesedData(final String inDir) {
        final List<Path> jsonFiles = new ArrayList<>();
        try {
            final String fileExt = (srcFileExt == null) ? "*" : srcFileExt;
            final String filePreFix = (srcFilePreFix == null) ? "" : srcFilePreFix;
            final List<Path> inputFiles = readInputFiles(inDir, filePreFix, fileExt);
            logger.info(inputFiles.size());
            if (!inputFiles.isEmpty()) {
                Path tmpDir = null;
                try {
                    tmpDir = Files.createTempDirectory("tmp_");
                    logger.info(("TempDir: " + tmpDir.toAbsolutePath().toString()));
                    for (final Path file : inputFiles) {
                        processEachFile(tmpDir, file);
                    }
                    jsonFiles.addAll(readInputFiles(jsonDir, outFilePreFix, outFileExt));
                }
                finally {
                    deleteTempFiles(tmpDir);
                }
            }
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return jsonFiles;
    }
    
    private List<Path> readInputFiles(final String inDir, final String filePreFix, final String fileExt) throws IOException {
        final StringBuilder fileNameExp = new StringBuilder();
        fileNameExp.append(filePreFix);
        fileNameExp.append("*");
        fileNameExp.append(".");
        fileNameExp.append(fileExt);
        logger.trace(("Input file criteria:" + fileNameExp));
        final List<Path> inputFiles = Utility.readFilesFromDir(inDir, fileNameExp.toString());
        logger.trace(("All Input Files: " + inputFiles.size() + "\n" + inputFiles));
        return inputFiles;
    }
    
    private void processEachFile(final Path tmpDir, final Path file) {
        logger.trace(("Processing File -> " + file.getFileName().toString()));
        try {
            Utility.unzip(file.toString(), tmpDir.toAbsolutePath().toString());
            generateOutFile(tmpDir, jsonDir);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            try {
                Utility.moveFile(file.toAbsolutePath().toString(), failDir);
            }
            catch (IOException e2) {
                logger.error(e2.getMessage(), e2);
            }
        }
    }
    
    private void generateOutFile(final Path tmpDir, final String jsonfldr) throws IOException {
        try (final Stream<Path> paths = Files.walk(tmpDir)) {
            paths.filter(f1 -> Files.isRegularFile(f1)).forEach(f -> createOutFile(f, jsonfldr));
        }
    }
    
    private void createOutFile(final Path f, final String jsonfldr) {
        logger.trace(("Unzipped File: " + f.toString()));
        final String outFileName = f.toFile().getName().replace(srcFilePreFix, outFilePreFix).replace(srcFileExt, outFileExt);
        logger.trace(("Printing out file: " + outFileName));
        try {
            Utility.moveFile(f.toAbsolutePath().toString(), jsonfldr, outFileName);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    private void deleteTempFiles(final Path tmpDir) {
        try {
            Utility.recursiveDeleteOnExit(tmpDir);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
    
    public void cleanDirectories() {
        deleteTempFiles(Paths.get(jsonDir));
    }
    
    static {
        logger = Logger.getLogger(JsonDataService.class);
    }
}