package com.mapia.s3;

/**
 * Created by daehyun on 2015. 5. 10..
 */
public class Constants {
    // You should replace these values with your own
    // See the readme for details on what to fill in
    public static final String AWS_ACCOUNT_ID = "025901772400";
    public static final String COGNITO_POOL_ID =
            "us-east-1:58df8c6e-c143-4e8f-8632-110d86feb248";
    public static final String COGNITO_ROLE_UNAUTH =
            "arn:aws:iam::025901772400:role/mapia_unauth";
    // Note, the bucket will be created in all lower case letters
    // If you don't enter an all lower case title, any references you add
    // will need to be sanitized
    public static final String BUCKET_NAME = "mapia";
}
