package com.womack;

import com.womack.workflows.DslWorkflow;
import com.womack.model.Flow;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;

import java.net.URL;

import static com.google.api.ResourceProto.resource;

public class Starter {

  public static void main(String[] args) {
    Flow flow = getFlowFromResource();
    
    WorkflowServiceStubsOptions stubOptions = WorkflowServiceStubsOptions.newBuilder()
            .build();
    WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(stubOptions);
    WorkflowClient client = WorkflowClient.newInstance(service);

    DslWorkflow workflow =
        client.newWorkflowStub(
            DslWorkflow.class,
            WorkflowOptions.newBuilder()
                .setWorkflowId("dsl-workflow-" + System.currentTimeMillis())
                .setTaskQueue("dsl-task-queue")
                .build());

    String result = workflow.run(flow, "WTF");

    System.out.println("Result: " + result);
    System.exit(0);
  }

  private static Flow getFlowFromResource() {
    ObjectMapper objectMapper = new ObjectMapper();
    try {
        URL resource = Starter.class.getClassLoader().getResource("dsl/sampleflow.json");
        return objectMapper.readValue(
                resource, Flow.class);
    } catch (Exception e) {
        System.out.println("Error reading flow from resource: " + resource.toString());
      e.printStackTrace();
      return null;
    }
  }
}
