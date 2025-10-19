# Celonis Programming Challenge

Dear applicant,

Congratulations, you made it to the Celonis Programming Challenge!

Note that, although you are welcome to spend as long as you feel appropriate on your solution, this assignment usually takes a minimum of 3 hours.

We will be happy to discuss any problems and ideas during the Challenge Interview.

Why do we ask you to complete this challenge?

First of all, we need to have some way of comparing different applicants, and we try to answer certain questions which
we cannot out-right ask in an interview - also we don't want to ask too many technical questions
in a face-to-face interview to not be personally biased in a potentially stressful situation.
To be as transparent as possible, we want to give you some insights into what we look at and how we evaluate.
This challenge gives you the possibility to shine :)

Note that there is nothing wrong with googling when you have certain questions or are unsure about some APIs,
but you should not outright copy code. If you decide to copy code, please mark it as such, citing the source.

## Complete and extend a java application

For this challenge, you have received a project which has a few problems.
You first have to fix those problems in order to get the application running, and then you should extend it with the requirements below.

What we are looking into:
  - Understanding and implementation of a specification
  - Java implementation skills (Java 11, Spring Boot)
  - Multithreading / locking execution
  - **Note**: performance and scalability are important, please apply reasonable balance between solution performance and invested time

      We expect to do some demo during the next technical interview,
      so please ensure the API works and prepare some mocks
      (Postman, curl or any preferred HTTP/REST tools)

How to understand the task:
  - consider the provided challenge as an application with some existing functionality,
    which was used to "generate" a file and download it
  - fix current issues to make the application runnable
  - keep existing behavior and API. Refactorings are allowed and welcome
  - extend and generalize the supplied sources according to the description below


### Task 1: Dependency injection

The project you received fails to start correctly due to a problem in the dependency injection.
Identify that problem and fix it.

### Task 2: Extend the application

The task is to extend the current functionality of the backend by
- implementing a new task type
- showing the progress of the task execution
- implementing a task cancellation mechanism.

The new task type is a simple counter which is configured with two input parameters, `x` and `y` of type `integer`.
When the task is executed, counter should start in the background and progress should be monitored.
Counting should start from `x` and get increased by one every second.
When counting reaches `y`, the task should finish successfully.

The progress of the task should be exposed via the API so that a web client can monitor it.
Canceling a task that is being executed should be possible, in which case the execution should stop.

### Task 3: Periodically clean up the tasks

The API can be used to create tasks, but the user is not required to execute those tasks.
The tasks that are not executed after an extended period (e.g. a week) should be periodically cleaned up (deleted).

# Appendix — Operation, Testing, and References

## 1) How to View the API Documentation (Swagger)

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
  You can try all endpoints live there.

- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

**Important (Security):** Add the header in each request:  

  Celonis-Auth: totally_secret 

(Swagger allows you to add it in **Authorize** or via **Try it out**).

---

## 2) Main Endpoints

### Base Tasks
- `GET /api/tasks` — list tasks
- `POST /api/tasks` — create task
- `GET /api/tasks/{taskId}` — get by ID
- `PUT /api/tasks/{taskId}` — update task
- `DELETE /api/tasks/{taskId}` — delete task
- `POST /api/tasks/{taskId}/execute` — generate `challenge.zip`
- `GET /api/tasks/{taskId}/result` — download ZIP

### New Task Type: Counter
- `POST /api/tasks/{taskId}/counter/start` — start counter `{x,y}`
- `GET /api/tasks/{taskId}/counter/progress` — progress
- `POST /api/tasks/{taskId}/counter/cancel` — cancel

---

## 3) Example cURL Commands (all include the auth header)

Replace `<TASK_ID>` with the actual ID returned upon creation.

### 3.1 Create Task
```bash
curl -X POST "http://localhost:8080/api/tasks" \
  -H "Celonis-Auth: totally_secret" \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Test Counter",
        "description": "Example",
        "status": "PENDING"
      }'
```
### 3.2 List Tasks
```bash
curl -H "Celonis-Auth: totally_secret" \
  "http://localhost:8080/api/tasks"
```

### 3.3 Get By Id
```bash
curl -H "Celonis-Auth: totally_secret" \
  "http://localhost:8080/api/tasks/<TASK_ID>"
```

### 3.4 Update Task
```bash
curl -X PUT "http://localhost:8080/api/tasks/<TASK_ID>" \
  -H "Celonis-Auth: totally_secret" \
  -H "Content-Type: application/json" \
  -d '{"name": "New name"}'
```

### 3.5 Delete Task
```bash
curl -X DELETE \
  -H "Celonis-Auth: totally_secret" \
  "http://localhost:8080/api/tasks/<TASK_ID>"
```

### 3.6 Execute (ZIP legacy)
```bash
curl -X POST \
  -H "Celonis-Auth: totally_secret" \
  "http://localhost:8080/api/tasks/<TASK_ID>/execute"
```

### 3.7 Download Result (ZIP)
```bash
curl -X POST \
  -H "Celonis-Auth: totally_secret" \
  "http://localhost:8080/api/tasks/<TASK_ID>/execute"
```

### 3.8 Start Counter
```bash
curl -X POST "http://localhost:8080/api/tasks/<TASK_ID>/counter/start" \
  -H "Celonis-Auth: totally_secret" \
  -H "Content-Type: application/json" \
  -d '{"x": 1, "y": 10}'
```

### 3.9 Counter Progress
```bash
curl -H "Celonis-Auth: totally_secret" \
  "http://localhost:8080/api/tasks/<TASK_ID>/counter/progress"
```

### 3.10 Cancel Counter
```bash
curl -X POST \
  -H "Celonis-Auth: totally_secret" \
  "http://localhost:8080/api/tasks/<TASK_ID>/counter/cancel"
```

## 4) How to View the H2 Database and Modify Data for Testing

    spring.h2.console.enabled=true
    spring.h2.console.path=/h2-console

    spring.datasource.url=jdbc:h2:mem:testdb
    spring.datasource.driver-class-name=org.h2.Driver
    spring.datasource.username=sa
    spring.datasource.password=

Start the app and open: http://localhost:8080/h2-console

    Connect with:
    JDBC URL: jdbc:h2:mem:testdb
    User: sa
    Password: (empty)

### Query/edit the table (usually PROJECT_GENERATION_TASK).

    SELECT * FROM PROJECT_GENERATION_TASK ORDER BY CREATION_DATE DESC;

Simulate an old (10 days) PENDING task:

    UPDATE PROJECT_GENERATION_TASK
    SET CREATION_DATE = DATEADD('DAY', -10, CURRENT_TIMESTAMP())
    WHERE STATUS = 'PENDING';

## 5) Test the Cleanup Job (Task 3)

The job periodically deletes unexecuted tasks (e.g., PENDING) older than 7 days.
Controlled by properties:

    # Enable/disable
    tasks.cleanup.enabled=true
    # Retention days for PENDING
    tasks.cleanup.retention-days=7
    # Cron: default every day at 03:00
    tasks.cleanup.cron=0 0 3 * * *


🔁 Workaround to test every 15s

Option A (temporary, in code): change job annotation to fixedDelay:

    // In TaskCleanupJob
    @Scheduled(fixedDelay = 15000) // every 15s after the previous execution finishes
    public void cleanupOldPendingTasks() { ... }


Option B (without changing code): keep @Scheduled(cron = "${tasks.cleanup.cron:...}")
and set in application.properties a cron every minute, e.g. at second 0 and 30:

    tasks.cleanup.cron=0,30 * * * * *

How to verify:

Mark a PENDING task with CREATION_DATE 10 days ago (see SQL above).

Wait for the job trigger and check logs; you’ll see something like:

CleanupJob: deleted 1 pending tasks older than 7 days (0 temp files removed).


## 6)  Where ZIP Files Are Stored and How They Are Downloaded

When executing /api/tasks/{taskId}/execute, challenge.zip (included in src/main/resources) is copied to a temp file and its absolute path is stored in the entity’s storageLocation.

GET /api/tasks/{taskId}/result returns the file with:

Content-Disposition: attachment; filename=challenge.zip


If it does not exist, response is:

File not generated yet

## 7)  Quick Notes

All endpoints require header: Celonis-Auth: totally_secret (validated by a simple filter).

The counter runs in background using ScheduledExecutorService; you can cancel live with /counter/cancel.

The cleanup job ignores RUNNING/COMPLETED tasks; if you want to also clean completed ones after X days, add another search/retention logic.