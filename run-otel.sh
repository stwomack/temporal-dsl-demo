   docker run -d \
     -p 4317:4317 -p 4318:4318 -p 9464:9464 \
     -v $(pwd)/otel-config.yaml:/etc/otel/config.yaml \
     otel/opentelemetry-collector:latest \
     --config=/etc/otel/config.yaml