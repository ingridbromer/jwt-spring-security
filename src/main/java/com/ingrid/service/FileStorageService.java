package com.ingrid.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import com.ingrid.config.FileStorageConfig;
import com.ingrid.exception.FileStorageException;
import com.ingrid.exception.MyFileNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
	
	private final Path fileStorageLocation;

	@Autowired
	public FileStorageService(FileStorageConfig fileStorageConfig) {
		Path path = Paths.get(fileStorageConfig.getUploadDir())
			.toAbsolutePath().normalize();
		
		this.fileStorageLocation = path;
		
		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception e) {
			throw new FileStorageException(
				"Could not create the directory where the uploaded files will be stored!", e);
		}
	}
	
	// Salvar arquivo em disco
	public String storeFile(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			// Filename..txt
			// Confere se a extensão é válida
			if (filename.contains("..")) {
				throw new FileStorageException(
					"Sorry! Filename contains invalid path sequence " + filename);
			}
			// Estabelece o caminho completo até o arquivo
			Path targetLocation = this.fileStorageLocation.resolve(filename);
			// Cria o arquivo vazio e agrega-o em diversas partes, copiando-o completo
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return filename;
		} catch (Exception e) {
			throw new FileStorageException(
				"Could not store file " + filename + ". Please try again!", e);
		}
	}
	
	//Leitura do arquivo em disco
	public Resource loadFileAsResource(String filename) {
		try {
			// Obtém o caminho o caminho até o arquivo
			Path filePath = this.fileStorageLocation.resolve(filename).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			// Se existir arquivo no caminho especificado
			if (resource.exists()) return resource;
			else throw new MyFileNotFoundException("File not found");
		} catch (Exception e) {
			throw new MyFileNotFoundException("File not found" + filename, e);
		}
	}

}
