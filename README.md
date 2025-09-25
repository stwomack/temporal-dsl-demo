# Temporal DSL Demo

A Spring Boot application demonstrating a Domain Specific Language (DSL) approach to workflow orchestration using Temporal. This project showcases how to define workflows through JSON configuration files and execute them dynamically using Temporal's workflow engine.

## Overview

This demo implements a flexible workflow system where business processes are defined as JSON configurations rather than hardcoded Java classes. The system reads flow definitions from JSON files and executes them using Temporal workflows and activities, providing a more dynamic and configurable approach to workflow management.

## Features

- **JSON-based Workflow Definition**: Define workflows using simple JSON configuration files
- **Dynamic Activity Execution**: Execute activities based on flow definitions with configurable retry policies and timeouts
- **Temporal Integration**: Built on Temporal's robust workflow engine for reliability and observability
- **Spring Boot Web Interface**: RESTful API and web UI for workflow management
- **Observability**: Integrated with OpenTelemetry, Prometheus, and Zipkin for monitoring and tracing
- **Compensation Support**: Built-in support for compensation actions (though not fully implemented in this demo)

## Architecture

### Core Components

- **Flow Model**: Defines the structure of workflow definitions with actions and metadata
- **FlowAction Model**: Represents individual steps in a workflow with retry policies and timeouts
- **DslWorkflow**: Temporal workflow interface for executing flow definitions
- **DslActivities**: Activity interface defining available business operations
- **Starter**: Main application class for running workflows

### Project Structure

```
src/main/java/com/womack/
├── TemporalDslDemo.java          # Spring Boot main application
├── Starter.java                  # Standalone workflow execution
├── model/
│   ├── Flow.java                 # Flow definition model
│   └── FlowAction.java           # Individual action model
├── workflows/
│   ├── DslWorkflow.java          # Workflow interface
│   └── DslWorkflowImpl.java      # Workflow implementation
└── activites/
    ├── DslActivities.java        # Activity interface
    └── DslActivitiesImpl.java    # Activity implementations

src/main/resources/
├── application.yaml              # Spring Boot configuration
├── dsl/
│   └── sampleflow.json          # Sample workflow definition
└── templates/
    └── index.html               # Web UI template
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Temporal Server (local or remote)

### Running the Application

1. **Start Temporal Server** (if running locally):
   ```bash
   temporal server start-dev
   ```

2. **Build and run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

### Running with Observability

The project includes OpenTelemetry configuration for distributed tracing and metrics:

1. **Start OpenTelemetry Collector**:
   ```bash
   ./run-otel.sh
   ```

2. **Run the application**:
   ```bash
   ./mvnw spring-boot:run
   ```

3. **View traces in Zipkin**: `http://localhost:9411`
4. **View metrics in Prometheus**: `http://localhost:9464`

## Usage

### Web Interface

The application provides a web interface at `http://localhost:8080` where you can:

- Execute workflows synchronously or asynchronously
- Look up workflow results by ID
- Monitor running workflows

### API Endpoints

- `POST /api/workflow/execute` - Execute workflow synchronously
- `POST /api/workflow/execute-async` - Execute workflow asynchronously
- `GET /api/workflow/result/{workflowId}` - Get workflow result

### Flow Definition Format

Workflows are defined in JSON format with the following structure:

```json
{
  "id": "sampleFlow",
  "name": "Sample Flow One",
  "description": "Sample Flow Definition",
  "actions": [
    {
      "action": "One",
      "retries": 10,
      "startToCloseSec": 3
    },
    {
      "action": "Two",
      "retries": 8,
      "startToCloseSec": 3
    }
  ]
}
```

#### Flow Properties

- `id`: Unique identifier for the flow
- `name`: Human-readable name
- `description`: Flow description
- `actions`: Array of actions to execute

#### Action Properties

- `action`: Name of the activity to execute (must match a method in DslActivities)
- `retries`: Maximum number of retry attempts (0 = no retries)
- `startToCloseSec`: Activity timeout in seconds
- `compensateBy`: Compensation action (for future implementation)
- `next`: Next action index (for future implementation)

## Configuration

### Application Configuration

The application is configured via `application.yaml`:

```yaml
spring:
  application:
    name: temporal-sandbox
  temporal:
    connection:
      target: 127.0.0.1:7233
      namespace: default

server:
  port: 8080

opentelemetry:
  sdk:
    service:
      name: temporal-sandbox
  traces:
    exporter: otlp
    endpoint: http://localhost:4317
```

### Temporal Configuration

- **Connection**: `127.0.0.1:7233` (local Temporal server)
- **Namespace**: `default`
- **Task Queue**: `dsl-task-queue`

## Development

### Adding New Activities

1. Add method to `DslActivities` interface
2. Implement method in `DslActivitiesImpl`
3. Reference the method name in your flow JSON

### Adding New Flow Definitions

1. Create JSON file in `src/main/resources/dsl/`
2. Use the flow definition format described above
3. Reference activities by their method names

### Testing

Run tests with:
```bash
./mvnw test
```

## Dependencies

- **Spring Boot 3.2.0**: Application framework
- **Temporal 1.31.0**: Workflow orchestration
- **OpenTelemetry 1.32.0**: Observability
- **Jackson**: JSON processing
- **Micrometer**: Metrics collection

## License

This project is a demonstration application for educational purposes.

## Contributing

This is a demo project. Feel free to fork and modify for your own learning and experimentation.
