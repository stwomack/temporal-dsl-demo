package com.womack.activites;

import com.womack.domain.FlowPayload;
import io.temporal.activity.ActivityInterface;

@ActivityInterface
public interface DslActivities {
  String one(FlowPayload flowPayload);

  String two(FlowPayload flowPayload);

  String three(FlowPayload flowPayload);

  String four(FlowPayload flowPayload);
}
