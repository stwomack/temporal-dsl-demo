package com.womack;

import com.womack.activites.DslActivitiesImpl;
import com.womack.workflows.DslWorkflow;
import com.womack.workflows.DslWorkflowImpl;
import com.womack.model.Flow;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

import java.net.URL;

import static com.google.api.ResourceProto.resource;

public class Starter {

  public static final WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
  public static final WorkflowClient client = WorkflowClient.newInstance(service);
  public static final WorkerFactory factory = WorkerFactory.newInstance(client);

  public static void main(String[] args) {
    Flow flow = getFlowFromResource();

    WorkflowServiceStubs service = WorkflowServiceStubs.newLocalServiceStubs();
    WorkflowClient client = WorkflowClient.newInstance(service);
    WorkerFactory factory = WorkerFactory.newInstance(client);
    Worker worker = factory.newWorker("dsl-task-queue");
    worker.registerWorkflowImplementationTypes(DslWorkflowImpl.class);
    worker.registerActivitiesImplementations(new DslActivitiesImpl());
    factory.start();

    DslWorkflow workflow =
        client.newWorkflowStub(
            DslWorkflow.class,
            WorkflowOptions.newBuilder()
                .setWorkflowId("dsl-workflow")
                .setTaskQueue("dsl-task-queue")
                .build());

    String result = workflow.run(flow, "sample input");

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
