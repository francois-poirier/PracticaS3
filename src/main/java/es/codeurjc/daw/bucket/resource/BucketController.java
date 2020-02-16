package es.codeurjc.daw.bucket.resource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.daw.bucket.dto.BucketDto;
import es.codeurjc.daw.bucket.service.BucketService;

@RestController
public class BucketController {

	Logger logger = LoggerFactory.getLogger(BucketController.class);

	private final BucketService bucketService;

	@Autowired
	public BucketController(BucketService bucketService) {
		this.bucketService = bucketService;
	}

	@GetMapping(value = "/api/buckets")
	public ResponseEntity<List<String>> listBuckets() {
		return ResponseEntity.ok().body(bucketService.listBuckets());
	}
	
	@GetMapping(value = "/api/buckets/{bucketName}")
    public ResponseEntity<BucketDto> getBucket(@PathVariable("bucketName") String bucketName) {
		return ResponseEntity.ok().body(bucketService.getBucket(bucketName));
	}
	

	@PostMapping(value = "​/api/buckets/{bucketName}")
	public ResponseEntity<BucketDto> createBucket(@PathVariable("bucketName") String bucketName) {
		return new ResponseEntity<>(bucketService.createBucket(bucketName), HttpStatus.CREATED);
	}
	
	
	@DeleteMapping(value = "​/api/buckets/{bucketName}")
	public ResponseEntity<Void> deleteBucket(@PathVariable("bucketName") String bucketName) {
		bucketService.deleteBucket(bucketName);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@DeleteMapping(value = "​/api/buckets/{bucketName}/{objectName}")
	public ResponseEntity<Void> deleteObject(@PathVariable("bucketName") String bucketName,@PathVariable("objectName") String objectName) {
		bucketService.deleteObject(bucketName,objectName);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@PostMapping(value = "​/api/buckets/{bucketName}/uploadObject")
	public ResponseEntity<String> uploadFile(@PathVariable("bucketName") String bucketName,@RequestPart("file") MultipartFile multipartFile, @RequestPart("isPublic") Boolean isPublic) throws IOException {
		String fileName = multipartFile.getOriginalFilename();
		File file = new File("java.io.tmpdir"+"/"+fileName);
		multipartFile.transferTo(file);
		return new ResponseEntity<>(bucketService.uploadFile(bucketName,fileName,file,isPublic), HttpStatus.CREATED);              
	}
	
	@PostMapping(value = "​/api/buckets/{bucketName}/copyObject")
	public ResponseEntity<String> copyObject(@PathVariable("bucketName") String bucketName, @RequestParam("sourceKey") String sourceKey, @RequestParam("destinationBucketName") String destinationBucketName,
			@RequestParam("destinationKey") String destinationKey) {
		return new ResponseEntity<>(bucketService.copyObject(bucketName, sourceKey, destinationBucketName, destinationKey),HttpStatus.CREATED);
	}
	
	
}
