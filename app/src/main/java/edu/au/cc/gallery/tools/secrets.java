package edu.au.cc.gallery;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.*;
import software.amazon.awssdk.services.secretsmanager.model.*;

//import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;

import java.util.Base64;

// Use this code snippet in your app.
// If you need more information about configurations or implementing the sample code, visit the AWS docs:
// https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/java-dg-samples.html#prerequisites

public class secrets {

  public static String getSecretImageGallery() {

 //  String secretName = "arn:aws:secretsmanager:us-east-1:196007689115:secret:sec-ig-image_gallery-qY2GpZ";
    String secretName = "sec-ig-image_gallery";
    Region region = Region.US_EAST_1;

    // Create a Secrets Manager client
    SecretsManagerClient client  = SecretsManagerClient.builder()
                                    .region(region)
				   // .credentialsProvider(ProfileCredentialsProvider.create())
                                    .build();
    
    // In this sample we only handle the specific exceptions for the 'GetSecretValue' API.
    // See https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
    // We rethrow the exception by default.
    
    String secret, decodedBinarySecret;
    GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                    .secretId(secretName)
		    .build();
    GetSecretValueResponse getSecretValueResult = null;

    try {
        getSecretValueResult = client.getSecretValue(getSecretValueRequest);
    } catch (DecryptionFailureException e) {
        // Secrets Manager can't decrypt the protected secret text using the provided KMS key.
        // Deal with the exception here, and/or rethrow at your discretion.
        throw e;
    } catch (InternalServiceErrorException e) {
        // An error occurred on the server side.
        // Deal with the exception here, and/or rethrow at your discretion.
        throw e;
    } catch (InvalidParameterException e) {
        // You provided an invalid value for a parameter.
        // Deal with the exception here, and/or rethrow at your discretion.
        throw e;
    } catch (InvalidRequestException e) {
        // You provided a parameter value that is not valid for the current state of the resource.
        // Deal with the exception here, and/or rethrow at your discretion.
        throw e;
    } catch (ResourceNotFoundException e) {
        // We can't find the resource that you asked for.
        // Deal with the exception here, and/or rethrow at your discretion.
        throw e;
    }
/**
    // Decrypts secret using the associated KMS key.
    // Depending on whether the secret is a string or binary, one of these fields will be populated.
    if (getSecretValueResult.getSecretString() != null) {
     return   secret = getSecretValueResult.getSecretString();
    }
    else {
      return  decodedBinarySecret = new String(Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
    }
**/
    // Your code goes here
   return getSecretValueResult.secretString();
 }
}
