package com.womack.workflows;

import com.womack.model.Flow;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface DslWorkflow {
  @WorkflowMethod
  String run(Flow flow, String input);
}
