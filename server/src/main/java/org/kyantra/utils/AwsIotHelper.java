package org.kyantra.utils;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsSyncClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEvents;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClientBuilder;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClientBuilder;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iotdata.AWSIotData;
import com.amazonaws.services.iotdata.AWSIotDataClientBuilder;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import org.kyantra.config.AWSCredsProvider;
import org.kyantra.dao.ConfigDAO;

public class AwsIotHelper {

    public static AWSIot getIotClient() {
        AWSIotClientBuilder clientBuilder = AWSIotClientBuilder.standard();
        clientBuilder = (AWSIotClientBuilder) setUpBuilder(clientBuilder);
        return clientBuilder.build();
    }

    public static AmazonCloudWatchEvents getAmazonCloudWatchEvents() {
        AmazonCloudWatchEventsClientBuilder clientBuilder = AmazonCloudWatchEventsClientBuilder.standard();
        clientBuilder = (AmazonCloudWatchEventsClientBuilder) setUpBuilder(clientBuilder);
        return clientBuilder.build();
    }

    public static AWSLambda getAWSLambdaClient() {
        AWSLambdaClientBuilder clientBuilder = AWSLambdaClientBuilder.standard().standard();
        clientBuilder = (AWSLambdaClientBuilder) setUpBuilder(clientBuilder);
        return clientBuilder.build();
    }

    public static AWSIotData getIotDataClient() {
        AWSIotDataClientBuilder clientBuilder = AWSIotDataClientBuilder.standard();
        clientBuilder = (AWSIotDataClientBuilder) setUpBuilder(clientBuilder);
        return clientBuilder.build();
    }

    public static AWSIotMqttClient getMQTT() {
        String awsKey = ConfigDAO.getInstance().get("awsKey").getValue();
        String awsSecret = ConfigDAO.getInstance().get("awsSecret").getValue();
        String endPoint = ConfigDAO.getInstance().get("endpoint").getValue();
        AWSIotMqttClient client = new AWSIotMqttClient(endPoint, "server", awsKey, awsSecret);
        return client;
    }

    public static AmazonSNS getAmazonSNSClient() {
        AmazonSNSClientBuilder clientBuilder = AmazonSNSClientBuilder.standard();
        clientBuilder = (AmazonSNSClientBuilder) setUpBuilder(clientBuilder);
        return clientBuilder.build();
    }

    public static AmazonDynamoDB getAmazonDynamoDBClient() {
        AmazonDynamoDBClientBuilder clientBuilder = AmazonDynamoDBClientBuilder.standard();
        clientBuilder = (AmazonDynamoDBClientBuilder) setUpBuilder(clientBuilder);
        return clientBuilder.build();
    }

    public static AmazonSimpleEmailService getAmazonSESClient() {
        AmazonSimpleEmailServiceClientBuilder clientBuilder = AmazonSimpleEmailServiceClientBuilder.standard();
        clientBuilder = (AmazonSimpleEmailServiceClientBuilder) setUpBuilder(clientBuilder);
        return clientBuilder.build();
    }

    private static AwsSyncClientBuilder setUpBuilder(AwsSyncClientBuilder clientBuilder) {
        String awsKey = ConfigDAO.getInstance().get("awsKey").getValue();
        String awsSecret = ConfigDAO.getInstance().get("awsSecret").getValue();
        clientBuilder.setCredentials(new AWSCredsProvider(new BasicAWSCredentials(awsKey, awsSecret)));
        clientBuilder.setRegion(Regions.AP_SOUTHEAST_1.getName());
        return clientBuilder;
    }
}
