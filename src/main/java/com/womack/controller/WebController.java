package com.womack.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import com.womack.workflows.DslWorkflow;
import com.womack.model.Flow;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.UUID;
import java.io.IOException;

@Controller
public class WebController {

    @Autowired
    private WorkflowClient workflowClient;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/")
    public String index() {
        return "index";
    }

    private Flow loadSampleFlow() throws IOException {
        ClassPathResource resource = new ClassPathResource("dsl/sampleflow.json");
        return objectMapper.readValue(resource.getInputStream(), Flow.class);
    }

    @PostMapping("/api/workflow/execute")
    @ResponseBody
    public ResponseEntity<String> executeWorkflow(@RequestBody Map<String, String> request) {
        try {
            String input = request.get("input");
            if (input == null || input.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Input cannot be empty");
            }

            Flow flow = loadSampleFlow();
            WorkflowOptions options = WorkflowOptions.newBuilder()
                    .setTaskQueue("dsl-task-queue")
                    .setWorkflowId("workflow-" + UUID.randomUUID().toString())
                    .build();

            DslWorkflow workflow = workflowClient.newWorkflowStub(DslWorkflow.class, options);
            String result = workflow.run(flow, input);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error executing workflow: " + e.getMessage());
        }
    }

    @PostMapping("/api/workflow/execute-async")
    @ResponseBody
    public ResponseEntity<String> executeWorkflowAsync(@RequestBody Map<String, String> request) {
        try {
            String input = request.get("input");
            if (input == null || input.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Input cannot be empty");
            }

            Flow flow = loadSampleFlow();
            String workflowId = "workflow-" + UUID.randomUUID().toString();
            WorkflowOptions options = WorkflowOptions.newBuilder()
                    .setTaskQueue("dsl-task-queue")
                    .setWorkflowId(workflowId)
                    .build();

            DslWorkflow workflow = workflowClient.newWorkflowStub(DslWorkflow.class, options);
            
            // Start workflow asynchronously
            WorkflowClient.start(() -> workflow.run(flow, input));
            
            return ResponseEntity.ok("Workflow started asynchronously. ID: " + workflowId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error starting async workflow: " + e.getMessage());
        }
    }

    @GetMapping("/api/workflow/result/{workflowId}")
    @ResponseBody
    public ResponseEntity<String> getWorkflowResult(@PathVariable String workflowId) {
        try {
            WorkflowStub workflowStub = workflowClient.newUntypedWorkflowStub(workflowId);
            
            if (!workflowStub.getExecution().getWorkflowId().equals(workflowId)) {
                return ResponseEntity.notFound().build();
            }

            // Check if workflow is completed
            String result = workflowStub.getResult(String.class);
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error getting workflow result: " + e.getMessage());
        }
    }
}