# Job Portal System: API Documentation

REST API for a job portal where **job seekers** search and apply for jobs, **recruiters** post jobs and manage applications, and **admins** moderate users, jobs, reports and applications.

- **Total APIs:** 36
- **Format:** JSON (`application/json`)
- **Interactive docs (Swagger UI):** [https://jobportalsystem-production-4cc4.up.railway.app/jobportal/swagger-ui/index.html](https://jobportalsystem-production-4cc4.up.railway.app/jobportal/swagger-ui/index.html)

## API Summary

| Controller | Base path | APIs |
|---|---|---|
| User | `/users` | 4 |
| Job | `/job` | 6 |
| Application | `/application` | 4 |
| Resume | `/resume` | 4 |
| Report | `/reports` | 2 |
| Admin | `/admin` | 16 |
| **Total** | | **36** |

## Roles

| Role | Description |
|---|---|
| `JOB_SEEKER` | Searches and applies for jobs, manages own resume |
| `RECRUITER` | Posts jobs, reviews applicants, changes application status |
| `ADMIN` | Blocks/activates users, archives/opens jobs, resolves reports |

## Data Models

### User

| Field | Type | Description |
|---|---|---|
| `id` | integer (int64) | Unique user ID |
| `name` | string | Full name |
| `email` | string | Email address |
| `password` | string | Password |
| `role` | enum | `JOB_SEEKER`, `RECRUITER`, `ADMIN` |
| `status` | enum | `ACTIVE`, `BLOCKED` |

### Job

| Field | Type | Description |
|---|---|---|
| `id` | integer | Unique job ID |
| `title` | string | Job title |
| `company` | string | Company name |
| `location` | string | Job location |
| `salary` | number (double) | Salary |
| `description` | string | Job description |
| `status` | string | e.g. `OPEN`, `ARCHIVED` |
| `recruiter` | User | Recruiter who posted the job |

### OTP Request

| Field | Type | Description |
|---|---|---|
| `email` | string | User email |
| `otp` | string | One-time password |

### Resume Upload

| Field | Type | Description |
|---|---|---|
| `file` | string | Resume file content |

### Report

| Field | Type | Description |
|---|---|---|
| `reportType` | string | Type of report, e.g. `USER`, `JOB` |
| `targetId` | integer | ID of the reported user or job |
| `reason` | string | Short reason |
| `description` | string | Detailed description |

---

# 1. User Controller

### 1.1 `POST /users/signup`: Register a new user

**Body:** User

```json
{
  "name": "Raj Sutreja",
  "email": "raj@example.com",
  "password": "Secret@123",
  "role": "JOB_SEEKER"
}
```

**Response:** `200 OK`. An OTP is sent to the email for verification.

### 1.2 `POST /users/verify-otp`: Verify email with OTP

**Body:** OTP Request

```json
{
  "email": "raj@example.com",
  "otp": "123456"
}
```

**Response:** `200 OK`. The account is verified.

### 1.3 `POST /users/resend-otp`: Resend OTP

**Body:** OTP Request

```json
{
  "email": "raj@example.com",
  "otp": "string"
}
```

**Response:** `200 OK`. A new OTP is sent to the email.

### 1.4 `POST /users/login`: Log in

**Body:** User

```json
{
  "email": "raj@example.com",
  "password": "Secret@123"
}
```

**Response:** `200 OK`. Login successful.

---

# 2. Job Controller

### 2.1 `POST /job/create`: Create a new job

**Role:** Recruiter

**Body:** Job

```json
{
  "title": "Java Backend Developer",
  "company": "TechCorp",
  "location": "Ahmedabad",
  "salary": 600000,
  "description": "Spring Boot, MySQL, REST APIs",
  "status": "OPEN"
}
```

**Response:** `200 OK`. The job is created.

### 2.2 `PUT /job/update`: Update an existing job

**Role:** Recruiter

**Body:** Job (`id` identifies the job to update)

```json
{
  "id": 1,
  "title": "Senior Java Developer",
  "company": "TechCorp",
  "location": "Remote",
  "salary": 900000,
  "description": "Updated description",
  "status": "OPEN"
}
```

**Response:** `200 OK`. The job is updated.

### 2.3 `DELETE /job/delete`: Delete a job

**Role:** Recruiter

**Query parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `jobId` | int64 | Yes | ID of the job to delete |

**Example:** `DELETE /job/delete?jobId=1`

**Response:** `200 OK`. The job is deleted.

### 2.4 `GET /job/search`: Search jobs by keyword

**Role:** Job seeker

**Query parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `keyword` | string | Yes | Text to search in jobs |

**Example:** `GET /job/search?keyword=java`

**Response:** `200 OK`. Returns the list of matching jobs.

### 2.5 `GET /job/recruiter/search`: Get all jobs of the logged-in recruiter

**Role:** Recruiter

**Parameters:** None

**Example:** `GET /job/recruiter/search`

**Response:** `200 OK`. Returns the jobs posted by the recruiter.

### 2.6 `GET /job/recruiter/searchs`: Search the recruiter's own jobs by keyword

**Role:** Recruiter

**Query parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `keyword` | string | Yes | Text to search in the recruiter's jobs |

**Example:** `GET /job/recruiter/searchs?keyword=developer`

**Response:** `200 OK`. Returns the matching jobs.

---

# 3. Application Controller

### 3.1 `POST /application/jobseeker/apply/{jobid}`: Apply for a job

**Role:** Job seeker

**Path parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `jobid` | int64 | Yes | ID of the job to apply for |

**Example:** `POST /application/jobseeker/apply/1`

**Response:** `200 OK`. The application is submitted.

### 3.2 `GET /application/user/{jobid}`: View own application for a job

**Role:** Job seeker

**Path parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `jobid` | int64 | Yes | Job ID |

**Example:** `GET /application/user/1`

**Response:** `200 OK`. Returns the application details and status.

### 3.3 `GET /application/recruiter/{jobid}`: Get all applications for a job

**Role:** Recruiter

**Path parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `jobid` | int64 | Yes | Job ID |

**Example:** `GET /application/recruiter/1`

**Response:** `200 OK`. Returns the list of applications.

### 3.4 `PUT /application/recruiter/{applicationid}/change/status`: Change application status

**Role:** Recruiter

**Path parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `applicationid` | int64 | Yes | Application ID |

**Query parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `status` | string | Yes | New status, e.g. `SHORTLISTED`, `REJECTED`, `HIRED` |

**Example:** `PUT /application/recruiter/5/change/status?status=SHORTLISTED`

**Response:** `200 OK`. The status is updated.

---

# 4. Resume Controller

### 4.1 `POST /resume/upload/jobseeker`: Upload resume

**Role:** Job seeker

**Body:** Resume Upload

```json
{
  "file": "resume-file-content"
}
```

**Response:** `200 OK`. The resume is uploaded.

### 4.2 `GET /resume/jobseeker`: View own resume

**Role:** Job seeker

**Parameters:** None

**Response:** `200 OK`. Returns the job seeker's resume.

### 4.3 `DELETE /resume/delete/jobseeker`: Delete own resume

**Role:** Job seeker

**Parameters:** None

**Response:** `200 OK`. The resume is deleted.

### 4.4 `GET /resume/recruiter/application/{applicationid}/jobseeker`: View an applicant's resume

**Role:** Recruiter

**Path parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `applicationid` | int64 | Yes | Application ID |

**Example:** `GET /resume/recruiter/application/5/jobseeker`

**Response:** `200 OK`. Returns the applicant's resume.

---

# 5. Report Controller

### 5.1 `POST /reports`: Submit a report

**Body:** Report

```json
{
  "reportType": "JOB",
  "targetId": 12,
  "reason": "Fake job",
  "description": "The company asked for money before the interview."
}
```

**Response:** `200 OK`. The report is submitted.

### 5.2 `GET /reports/my`: View my reports

**Parameters:** None

**Response:** `200 OK`. Returns the reports submitted by the logged-in user.

---

# 6. Admin Controller

**Role for all APIs in this section:** Admin

## 6.A User Management

### 6.1 `GET /admin/users`: Get all users

**Parameters:** None

**Response:** `200 OK`. Returns the list of all users.

### 6.2 `GET /admin/users/name`: Search users by name

**Query parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `name` | string | Yes | User name to search |

**Example:** `GET /admin/users/name?name=raj`

**Response:** `200 OK`. Returns the matching users.

### 6.3 `GET /admin/users/email`: Search users by email

**Query parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `email` | string | Yes | User email to search |

**Example:** `GET /admin/users/email?email=raj@example.com`

**Response:** `200 OK`. Returns the matching user.

### 6.4 `PATCH /admin/users/{id}/block`: Block a user

**Path parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `id` | int64 | Yes | User ID |

**Example:** `PATCH /admin/users/3/block`

**Response:** `200 OK`. The user status becomes `BLOCKED`.

### 6.5 `PATCH /admin/users/{id}/active`: Activate a user

**Path parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `id` | int64 | Yes | User ID |

**Example:** `PATCH /admin/users/3/active`

**Response:** `200 OK`. The user status becomes `ACTIVE`.

## 6.B Job Management

### 6.6 `GET /admin/jobs`: Get all jobs

**Parameters:** None

**Response:** `200 OK`. Returns the list of all jobs.

### 6.7 `GET /admin/jobs/recruiter/{recruiterid}`: Get jobs by recruiter

**Path parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `recruiterid` | int64 | Yes | Recruiter ID |

**Example:** `GET /admin/jobs/recruiter/2`

**Response:** `200 OK`. Returns the jobs posted by that recruiter.

### 6.8 `PATCH /admin/jobs/{jobid}/open`: Open a job

**Path parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `jobid` | int64 | Yes | Job ID |

**Example:** `PATCH /admin/jobs/1/open`

**Response:** `200 OK`. The job status becomes `OPEN`.

### 6.9 `PATCH /admin/jobs/{jobid}/archive`: Archive a job

**Path parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `jobid` | int64 | Yes | Job ID |

**Example:** `PATCH /admin/jobs/1/archive`

**Response:** `200 OK`. The job status becomes `ARCHIVED`.

## 6.C Application Management

### 6.10 `GET /admin/application/jobid/{jobid}`: Get applications of a job

**Path parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `jobid` | int64 | Yes | Job ID |

**Example:** `GET /admin/application/jobid/1`

**Response:** `200 OK`. Returns all applications for that job.

### 6.11 `DELETE /admin/application/id/{applicationId}`: Delete an application

**Path parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `applicationId` | int64 | Yes | Application ID |

**Example:** `DELETE /admin/application/id/5`

**Response:** `200 OK`. The application is deleted.

## 6.D Report Management

### 6.12 `GET /admin/reports`: Get all reports

**Parameters:** None

**Response:** `200 OK`. Returns the list of all reports.

### 6.13 `GET /admin/reports/status`: Get reports by status

**Query parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `status` | string | Yes | Report status, e.g. `PENDING`, `RESOLVED`, `REJECTED` |

**Example:** `GET /admin/reports/status?status=PENDING`

**Response:** `200 OK`. Returns the matching reports.

### 6.14 `PATCH /admin/reports/{reportId}/resolve`: Resolve a report

**Path parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `reportId` | int64 | Yes | Report ID |

**Example:** `PATCH /admin/reports/4/resolve`

**Response:** `200 OK`. The report is marked as resolved.

### 6.15 `PATCH /admin/reports/{reportId}/reject`: Reject a report

**Path parameters:**

| Name | Type | Required | Description |
|---|---|---|---|
| `reportId` | int64 | Yes | Report ID |

**Example:** `PATCH /admin/reports/4/reject`

**Response:** `200 OK`. The report is marked as rejected.

## 6.E Dashboard

### 6.16 `GET /admin/dashboard`: Get dashboard statistics

**Parameters:** None

**Response:** `200 OK`. Returns summary statistics (users, jobs, applications, reports).

---

# Complete API List (36)

| # | Method | Endpoint | Description |
|---|---|---|---|
| 1 | POST | `/users/signup` | Register a new user |
| 2 | POST | `/users/verify-otp` | Verify email with OTP |
| 3 | POST | `/users/resend-otp` | Resend OTP |
| 4 | POST | `/users/login` | Log in |
| 5 | POST | `/job/create` | Create a job |
| 6 | PUT | `/job/update` | Update a job |
| 7 | DELETE | `/job/delete` | Delete a job |
| 8 | GET | `/job/search` | Search jobs by keyword |
| 9 | GET | `/job/recruiter/search` | Get recruiter's jobs |
| 10 | GET | `/job/recruiter/searchs` | Search recruiter's jobs by keyword |
| 11 | POST | `/application/jobseeker/apply/{jobid}` | Apply for a job |
| 12 | GET | `/application/user/{jobid}` | View own application |
| 13 | GET | `/application/recruiter/{jobid}` | Get applications for a job |
| 14 | PUT | `/application/recruiter/{applicationid}/change/status` | Change application status |
| 15 | POST | `/resume/upload/jobseeker` | Upload resume |
| 16 | GET | `/resume/jobseeker` | View own resume |
| 17 | DELETE | `/resume/delete/jobseeker` | Delete own resume |
| 18 | GET | `/resume/recruiter/application/{applicationid}/jobseeker` | View applicant's resume |
| 19 | POST | `/reports` | Submit a report |
| 20 | GET | `/reports/my` | View my reports |
| 21 | GET | `/admin/users` | Get all users |
| 22 | GET | `/admin/users/name` | Search users by name |
| 23 | GET | `/admin/users/email` | Search users by email |
| 24 | PATCH | `/admin/users/{id}/block` | Block a user |
| 25 | PATCH | `/admin/users/{id}/active` | Activate a user |
| 26 | GET | `/admin/jobs` | Get all jobs |
| 27 | GET | `/admin/jobs/recruiter/{recruiterid}` | Get jobs by recruiter |
| 28 | PATCH | `/admin/jobs/{jobid}/open` | Open a job |
| 29 | PATCH | `/admin/jobs/{jobid}/archive` | Archive a job |
| 30 | GET | `/admin/application/jobid/{jobid}` | Get applications of a job |
| 31 | DELETE | `/admin/application/id/{applicationId}` | Delete an application |
| 32 | GET | `/admin/reports` | Get all reports |
| 33 | GET | `/admin/reports/status` | Get reports by status |
| 34 | PATCH | `/admin/reports/{reportId}/resolve` | Resolve a report |
| 35 | PATCH | `/admin/reports/{reportId}/reject` | Reject a report |
| 36 | GET | `/admin/dashboard` | Get dashboard statistics |

---

# Status Codes

| Code | Meaning |
|---|---|
| `200` | Request succeeded |
| `400` | Invalid input |
| `401` | Not authenticated |
| `403` | Not allowed for this role |
| `404` | Resource not found |
| `500` | Server error |
