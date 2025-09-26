package com.womack.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FlowPayload {
    private JsonNode data;

    public FlowPayload() {}

    public FlowPayload(JsonNode data) {
        this.data = data;
    }

    @JsonCreator
    public static FlowPayload fromJson(JsonNode node) {
        return new FlowPayload(node);
    }

    @JsonValue
    public JsonNode getData() {
        return data;
    }

    public void setData(JsonNode data) {
        this.data = data;
    }
}