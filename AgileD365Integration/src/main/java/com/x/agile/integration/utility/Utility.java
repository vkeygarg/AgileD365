package com.x.agile.integration.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import net.lingala.zip4j.ZipFile;

public class Utility {
	private Utility() {
	}

	public static void unzip(String zipFilename, String... dstDir ) throws IOException {
		Path zipFilePath = Paths.get(zipFilename);
		if ((dstDir == null) || (dstDir.length < 1) || (dstDir[0] == null) || (dstDir[0].isEmpty())) {
			throw new IOException("Destination Dir cannot be null/blank.");
		}
		new File(dstDir[0]).mkdirs();
		String unzippedFileName = (dstDir.length < 2) || (dstDir[1] == null) || (dstDir[1].isEmpty())
				? zipFilePath.getFileName().toString()
				: dstDir[1];
		new ZipFile(Paths.get(zipFilename).toFile()).extractFile("agile.xml", dstDir[0], unzippedFileName);
	}

	public static void writeInFile(String fileName, String fileData) throws IOException {
		Files.write(Paths.get(fileName), fileData.getBytes());
	}

	public static List<Path> readFilesFromDir(String sourceLoc, String criteria) throws IOException {
		List<Path> inputFiles = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(sourceLoc), criteria)) {
			for (Path path : stream) {
				inputFiles.add(path.toAbsolutePath());
			}
		}
		return inputFiles;
	}

	public static void recursiveDeleteOnExit(Path pathToBeDeleted) throws IOException {
		try (Stream<Path> walk = Files.walk(pathToBeDeleted)) {
			walk.sorted(Comparator.reverseOrder()).map(Path::toFile)
					// .peek(System.out::println)
					.forEach(File::delete);
		}
	}

	public static void moveFile(String srcFile, String... dstDir) throws IOException {
		if ((dstDir == null) || (dstDir.length < 1) || (dstDir[0] == null) || (dstDir[0].isEmpty())) {
			throw new IOException("Destination Dir cannot be null/blank.");
		}
		new File(dstDir[0]).mkdirs();
		Path srcFilePath = Paths.get(srcFile);
		String tarFileName = (dstDir.length < 2) || (dstDir[1] == null) || (dstDir[1].isEmpty())
				? srcFilePath.getFileName().toString()
				: dstDir[1];
		Path tarFilePath = Paths.get(dstDir[0]).resolve(tarFileName);
		Files.move(srcFilePath, tarFilePath, StandardCopyOption.REPLACE_EXISTING);
	}

	public static boolean isEmptyStr(String str) {
		return (str == null) || (str.trim().isEmpty()) || ("null".equals(str.trim()));
	}
}
