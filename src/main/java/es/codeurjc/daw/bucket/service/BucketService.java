package es.codeurjc.daw.bucket.service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import es.codeurjc.daw.bucket.dto.BucketDto;
import es.codeurjc.daw.bucket.dto.ObjectDto;
import es.codeurjc.daw.bucket.exception.EntityNotFoundException;

@Service
public class BucketService {

	private static final String NOT_FOUND = " does not exist.";
	private static final String BUCKET = "Bucket ";
	private static final String OBJECT_KEY =  "Object key ";
	private final AmazonS3 s3client;

	@Autowired
	public BucketService(AmazonS3 s3client) {
		this.s3client = s3client;
	}

	public BucketDto getBucket(String bucketName) {
		List<Bucket> buckets = s3client.listBuckets();
		for (Bucket bucket : buckets) {
			if (bucket.getName().equals(bucketName)) {
				return toBucketDto(bucket);
			}
		}
		throw new EntityNotFoundException(BUCKET + bucketName + NOT_FOUND);
	}

	public BucketDto createBucket(String bucketName) {
		
		return toBucketDto(s3client.createBucket(bucketName));
	}

	public List<String> listBuckets() {
		return s3client.listBuckets().stream().map(b -> b.getName()).collect(Collectors.toList());
	}

	public void deleteBucket(String bucketName) {
		if (!bucketExist(bucketName)) {
			throw new EntityNotFoundException(BUCKET + bucketName + NOT_FOUND);
		}

		s3client.deleteBucket(bucketName);
	}

	public String uploadFile(String bucketName, String key, File file, Boolean isPublic) {
		if (!bucketExist(bucketName)) {
			throw new EntityNotFoundException(BUCKET + bucketName + NOT_FOUND);
		}

		if (Boolean.TRUE.equals(isPublic)) {
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,key,file);
			putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
			return s3client.putObject(putObjectRequest).getETag();
		}
        
		return s3client.putObject(bucketName,key,file).getETag();
	}

	public List<ObjectDto> listObjects(String bucketName) {
		if (!bucketExist(bucketName)) {
			throw new EntityNotFoundException(BUCKET + bucketName + NOT_FOUND);
		}
		ObjectListing objectListing = s3client.listObjects(bucketName);
		return objectListing.getObjectSummaries().stream().map(os-> toObjectDto(os)).collect(Collectors.toList());
	}

	public String copyObject(String sourceBucketName, String sourceKey, String destinationBucketName, String destinationKey) {
		
		checkSource(sourceBucketName, sourceKey);

		if (!bucketExist(destinationBucketName)) {
			throw new EntityNotFoundException(BUCKET + destinationBucketName + NOT_FOUND);
		}

		return s3client.copyObject(sourceBucketName, sourceKey, destinationBucketName, destinationKey).getETag();
	}

	public void deleteObject(String bucketName, String objectKey) {
		checkSource(bucketName, objectKey);
		s3client.deleteObject(bucketName, objectKey);
	}

	private BucketDto toBucketDto(Bucket bucket) {
		return new BucketDto.Builder().withName(bucket.getName()!=null?bucket.getName():null).withCreationDate(bucket.getCreationDate()!=null?bucket.getCreationDate():null).withOwnerName(bucket.getOwner()!=null?bucket.getOwner().getDisplayName():null)
				.build();
	}
	
	private ObjectDto toObjectDto(S3ObjectSummary s) {
		return new ObjectDto.Builder().withETag(s!=null?s.getETag():null).withLastModified(s!=null?s.getLastModified():null).withSize(s!=null?s.getSize():0).build();
	}
	
	private void checkSource(String bucketName, String objectKey) {
		if (!bucketExist(bucketName)) {
			throw new EntityNotFoundException(BUCKET + bucketName + NOT_FOUND);
		}
		if (!keyExist(bucketName,objectKey)) {
			throw new EntityNotFoundException(OBJECT_KEY + objectKey + NOT_FOUND);
		}
	}

	private boolean bucketExist(String bucketName) {
		return s3client.doesBucketExistV2(bucketName);
	}

	private boolean keyExist(String bucketName, String keyName) {
		try
		{
			GetObjectMetadataRequest request  = new GetObjectMetadataRequest(bucketName,keyName);
			s3client.getObjectMetadata(request);
		}
		catch (AmazonS3Exception e)
		{
			if (e.getStatusCode()==404)
				return false;
				
			throw e;
		}
		
		return true;
	}
}
