# BinaryBrain - Online Classroom Management System 📚

Welcome to **BinaryBrain** 🚀, a powerful Spring Boot-based microservices platform designed to streamline online classroom and course management for teachers and students. This application offers robust tools for task assignments, submissions, peer reviews, course material sharing, and detailed reporting.

---

## Team Members 👥
- **Sourav600** (Team Leader) 🌟
- **Me-Sharif-Hasan** 💻
- **BayazidHossain2** 🛠️

## Mentor 🎓
- **Kaziasifjawwad**

---

## Project Overview ℹ️
BinaryBrain is a feature-rich online classroom management system built with a microservices architecture using Spring Boot. It empowers teachers to manage classrooms and courses efficiently while providing students with seamless access to resources and collaborative tools.

---

## Key Features ✨

### 1. User Roles
#### Teacher 👨‍🏫:
- Create and manage multiple classrooms 🏫.
- Develop and organize courses with detailed syllabi 📝.
- Assign tasks and assignments to students 📚.
- Upload and share course materials (PDFs, videos, documents) 📂.
- Access, review, and download student submissions ✅.
- Provide feedback and grades for assignments 🖊️.
- Generate comprehensive course reports with performance insights 📊.

#### Student 👩‍🎓:
- Enroll in multiple classrooms and courses 📖.
- Submit assignments via:
  - PDF uploads 📄.
  - External links (e.g., Google Drive, GitHub) 🔗.
- Store course-related materials with public or private access 🔒.
- Participate in course-specific Q&A discussions 💬.
- Access teacher-shared resources (PDFs, videos, etc.) 📽️.
- Provide peer feedback on submissions 🤝.

---

### 2. Classroom and Course Management 🏫
- Teachers can create and manage multiple classrooms, each linked to specific courses or subjects.
- Students can join classrooms using unique codes or invitations 🔑.
- Course management includes syllabi, schedules, and task assignments 📅.

---

### 3. Task and Assignment Management 📋
- Teachers can create tasks or assignments tied to specific courses.
- Students submit assignments as:
  - PDF uploads 📄.
  - External links 🔗.
- Flexible submission deadlines with automated reminders ⏰.
egde Teachers can review submissions and provide detailed feedback ✍️.

---

### 4. Peer Review System 🤝
- Students and teachers can participate in peer reviews for assignments and projects.
- Structured feedback forms ensure constructive and meaningful reviews ✅.
- Peer review data is aggregated and included in final course reports 📊.

---

### 5. Course Material Management 📚
#### Teacher Resources:
- Upload and organize course materials (PDFs, videos, documents) 📂.
- Embedded PDF readers and video players for seamless access 📽️.
- Centralized repository for all shared resources 🗄️.

#### Student Storage:
- Dedicated storage space for each student to save course-related materials 💾.
- Option to set materials as public (shared with peers) or private 🔒.
- Easy access to teacher-uploaded resources 📖.

---

### 6. Q&A Section 💬
- Dedicated Q&A section for each course to foster collaboration.
- Teachers and students can post questions and answers ❓.
- Threaded discussions for organized communication 🗣️.

---

### 7. Course Report Generation 📊
- Teachers can generate detailed reports including:
  - Student performance summaries 📈.
  - Submission and feedback details 📝.
  - Peer review insights 🤝.
- Exportable reports in PDF format for record-keeping 📄.

---

## Database Schema 🗄️
Our database schema is designed for scalability and efficiency using MySQL. View the current design here:

![Database Schema](https://github.com/user-attachments/assets/107fe291-6ce0-407d-9b5a-76a18e28acc9)

---

## Microservice Structure 🧩

![image](https://github.com/user-attachments/assets/ecdea450-c1f6-4566-a1f3-94953317135f)


BinaryBrain is built using a microservices architecture to ensure scalability, maintainability, and modularity. Below is the structure of the microservices, each corresponding to a specific module and its responsibilities:

1. **Classroom Microservice** (`classroom-microservice`) 🏫
   - **Service**: Classroom Service
   - **Responsibility**: Manages the creation, configuration, and enrollment of classrooms.
   - **Features**:
     - Create and manage classrooms with unique codes for student access 🔑.
     - Handle student enrollment and classroom memberships.
     - Link classrooms to courses managed by the Course Microservice.
   - **Database**: Stores classroom metadata, enrollment records, and relationships with courses in MySQL.

2. **Course Microservice** (`course-microservice`) 📖
   - **Service**: Course Service
   - **Responsibility**: Manages course creation, syllabi, and schedules.
   - **Features**:
     - Create and update course details, including syllabi and schedules 📝.
     - Associate tasks and materials with specific courses.
     - Provide course metadata to other services (e.g., Classroom, Task).
   - **Database**: Stores course metadata, syllabi, and schedules in MySQL.

3. **Task Microservice** (`task-microservice`) 📋
   - **Service**: Assignment Service (Task Creation)
   - **Responsibility**: Handles the creation and management of tasks and assignments.
   - **Features**:
     - Allow teachers to create tasks tied to specific courses 📚.
     - Set assignment deadlines and send automated reminders ⏰.
     - Integrate with the Task Submission Service for submission tracking.
   - **Database**: Stores task metadata, deadlines, and course associations in MySQL.

4. **Task Submission Service** (`task-submission-service`) 📄
   - **Service**: Assignment Service (Submission and Review)
   - **Responsibility**: Manages student submissions, teacher reviews, and peer reviews.
   - **Features**:
     - Handle student assignment submissions via PDF uploads or external links 🔗.
     - Facilitate teacher feedback and grading ✍️.
     - Support peer review workflows with structured feedback forms 🤝.
   - **Database**: Stores submission files, feedback, and review data in MySQL.

5. **Online Classroom Management** (`online-classroom-management`) 🌐
   - **Service**: User and Material Services
   - **Responsibility**: Serves as the core module handling user management and course material management (potentially a legacy monolithic component).
   - **Features**:
     - Manage user authentication, authorization, and profiles (teachers and students) 👤.
     - Handle course material uploads (PDFs, videos, documents) and student storage 📂.
     - Provide embedded viewers for PDFs and videos 📽️.
     - Support Q&A sections for course discussions 💬.
   - **Database**: Stores user credentials, profiles, material metadata, and Q&A threads in MySQL.
   - **Note**: This module may be refactored into separate User and Material microservices in the future for better modularity.

6. **Eureka Server** (`eureka-server`) 🔍
   - **Service**: Service Discovery
   - **Responsibility**: Acts as a service registry for microservices to discover and communicate with each other.
   - **Features**:
     - Registers all microservices (e.g., Classroom, Course, Task) for dynamic discovery.
     - Enables load balancing and fault tolerance across service instances.
   - **Database**: None (in-memory registry).

7. **Gateway** (`gateway`) 🚪
   - **Service**: API Gateway
   - **Responsibility**: Serves as the entry point for all client requests, routing them to appropriate microservices.
   - **Features**:
     - Route requests to microservices based on URL patterns 🌐.
     - Handle cross-cutting concerns like authentication, rate limiting, and logging 🔐.
     - Provide a unified API for frontend applications.
   - **Database**: None (configuration-based).

**Communication**: Microservices communicate via REST APIs for synchronous operations and may use message queues (e.g., RabbitMQ) for asynchronous tasks like notifications or report generation. The Eureka Server ensures service discovery, while the Gateway handles request routing and security.

**Note**: The `online-classroom-management` module currently encompasses multiple responsibilities (user management, material management, Q&A). Future iterations may split these into dedicated microservices (e.g., User Service, Material Service, Q&A Service) for enhanced scalability.

---

## Tech Stack 🛠️
- **Backend**: Spring Boot, Java ☕
- **Frontend**: Swagger for API documentation (Try our APIs at [Swagger UI](https://binarybrains.gentlesmoke-d65a2350.westus2.azurecontainerapps.io/swagger-ui/index.html));
- **Database**: MySQL 🗃️
- **Testing**: JUnit, Mockito 🧪
- **Code Quality**: SonarCloud ✅
- **Service Discovery**: Eureka Server 🔍
- **API Gateway**: Spring Cloud Gateway 🚪
- **Version Control**: Git, GitHub 🗂️
- **Deployment**: Azure Container Apps ☁️

---

## Work Breakdown Structure (WBS) 📋
For a detailed breakdown of project tasks, milestones, and responsibilities, refer to our **[WBS Document](https://docs.google.com/spreadsheets/d/1bgchJjNJaJP7OYBhqBkN5EjsBL5wpQkKD4xSSDxRZ_Q/edit?gid=1200157808#gid=1200157808)** 📑.

---

## Test Coverage ✅
We ensure high code quality with comprehensive test coverage, monitored via SonarCloud.

[![SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=Learnathon-By-Geeky-Solutions_binarybrain&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Learnathon-By-Geeky-Solutions_binarybrain)


---

## Getting Started 🚀

### Prerequisites
- Java 17 or higher ☕
- Maven 🛠️
- MySQL 🗃️
- Git 📂

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/Learnathon-By-Geeky-Solutions/binarybrain.git
   ```
2. Navigate to the project directory:
   ```bash
   cd binarybrain
   ```
3. Install dependencies:
   ```bash
   mvn install
   ```
4. Configure the MySQL database:
   - Create a MySQL database (e.g., `binarybrain_db`).
   - Update `application.properties` in each microservice with your MySQL credentials (e.g., `spring.datasource.url=jdbc:mysql://localhost:3306/binarybrain_db`).
5. Run the Eureka Server:
   ```bash
   cd eureka-server
   mvn spring-boot:run
   ```
6. Run the Gateway:
   ```bash
   cd gateway
   mvn spring-boot:run
   ```
7. Run other microservices (e.g., `classroom-microservice`, `course-microservice`, etc.):
   ```bash
   cd <microservice-name>
   mvn spring-boot:run
   ```
8. Access the application:
   - Visit the Swagger UI to try the APIs: [https://binarybrains.gentlesmoke-d65a2350.westus2.azurecontainerapps.io/swagger-ui/index.html](https://binarybrains.gentlesmoke-d65a2350.westus2.azurecontainerapps.io/swagger-ui/index.html) 🌐.
   - Alternatively, access the Gateway at `http://localhost:8080` for local testing.

---

## Contributing 🤝
We welcome contributions! To contribute:
1. Fork the repository 🍴.
2. Create a new branch (`git checkout -b feature/your-feature`) 🌿.
3. Commit your changes (`git commit -m "Add your feature"`) ✅.
4. Push to the branch (`git push origin feature/your-feature`) 🚀.
5. Open a Pull Request 📬.

Please ensure your code adheres to our coding standards and includes relevant tests 🧪.

---

## License 📜
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Contact 📧
For inquiries or feedback, reach out to the team via [GitHub Issues](https://github.com/Learnathon-By-Geeky-Solutions/binarybrain/issues).

---

**BinaryBrain** is a project developed as part of the **Learnathon by Geeky Solutions** 🏆. We aim to revolutionize online education with a user-friendly and feature-packed classroom management system.
