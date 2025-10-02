package com.womack.activites;

import com.womack.domain.FlowPayload;
import io.temporal.spring.boot.ActivityImpl;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
@ActivityImpl(taskQueues = "dsl-task-queue")
public class DslActivitiesImpl implements DslActivities {
  @Override
  public String one(FlowPayload flowPayload) {
    sleep(1);
    return flowPayload.getData().toString();
  }

  @Override
  public String two(FlowPayload flowPayload) {
    sleep(1);
    return flowPayload.getData().toString();
  }

  @Override
  public String three(FlowPayload flowPayload) {
    sleep(1);
    return flowPayload.getData().toString();
  }

  @Override
  public String four(FlowPayload flowPayload) {
    sleep(1);
    return flowPayload.getData().toString();
  }

  private void sleep(int seconds) {
    try {
      Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
    } catch (InterruptedException ee) {
    }
  }
}
