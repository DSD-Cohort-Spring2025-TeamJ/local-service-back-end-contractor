# Service Booking Application
Welcome to the Service Booking Backend Application, a web-based platform designed to simplify the process of booking home services such as plumbing, water leakage repairs, and sewer maintenance etc. This application leverages AI to match user-reported issues with the most suitable service recognition, streamlining the booking process for end users, technicians, and administrators.

## Features
  * **User-Friendly Interface:** End users can easily describe their issues (e.g., plumbing, water leakage, sewer problems) via the web app.
  * **AI-Powered Service Matching:** The app intelligently analyzes user input to recommend the most appropriate service.
  * **Booking System:** Users can select their preferred date and time for the service after the issue is identified.
  * **Admin Dashboard:** Admins can review, accept, decline, or leave bookings as pending.
  * **Automated Notifications**:  
    * Technicians receive email notifications with booking details once the admin approves.
    * If required parts are out of stock, technicians are notified in advance.
  * **Scalable Backend:** Handles user requests, admin approvals, and email workflows efficiently.  

## Usage
- For Users: Visit the web app, describe your issue, and book a service.
- For Admins: Log in to the admin panel to manage bookings.
- For Technicians: Check your email for assigned bookings and updates on parts availability.
 
## How It Works
  1. **User Submission:** The end user visits the web app and submits a description of their issue (e.g., "leaking pipe in the kitchen").
  2. **AI Analysis:** The AI processes the description and suggests the best service match (e.g., plumbing).
  3. **Booking:** The user is redirected to a page to choose a convenient date and time for the service.
  4. **Admin Review:** The admin reviews the booking and can accept, decline, or leave it pending.
  5. **Technician Assignment:** Upon approval, the system emails the assigned technician with details.
  6. **Stock Check:** If parts are unavailable, an additional email notifies the technician before their visit.

## Tech Stack
  * **Backend:** Java, Spring Boot, Spring Data JPA, Hibernate  
  * **Database:** PostgreSQL (hosted on AWS RDS)  
  * **AI Integration:** OpenAI API (GPT-3.5)  
  * **Hosting:** AWS EC2 via Elastic Beanstalk  
  * **CI/CD:** GitHub Actions for automated deployment  
  * **Email Service:** Java Mail Sender / MailTrap.io
  * **Calendar Integration:** Google Calendar API with Google Authentication and JWT 
  * **SSL:** Configured with Let's Encrypt automated scripts 

## Installation

### Prerequisites

Before running the project, make sure you have the following installed:

- **Java 8 or later** (JDK)
- **Maven** (or Gradle, if the project uses it)
- **Git** (for cloning the repository)
- **PostgreSQL**
- **EMAIl_API_KEY** for sending Automated Emails
- **OPEN_AI_KEY** for integrating AI services into the application.


### Getting Started

To run this project locally, follow these steps:
1. Clone the Repository using GIT command
```bash
git clone https://github.com/DSD-Cohort-Spring2025-TeamJ/local-service-back-end-contractor.git
```
2. Go to the folder 
```bash
cd local-service-back-end-contractor
```
4. Install Dependencies
```bash
mvn clean install
# This command will download dependencies, compile the source code, and package the application into a .jar file.
```
4. Set Up Environment Variables
> Modify the properties in application.properties with your configurations
```application.properties
spring.datasource.url=jdbc:postgresql://localhost:3306/booking-app
spring.datasource.username=database_username
spring.datasource.password=database_password
spring.mail.username=your_mail_username
spring.mail.password=your_mail_password

openai.api.key=your_open_ai_api_key
```
5. Once the project is built and configured, you can run the Spring Boot application using the following command:
```bash
mvn spring-boot:run
```
OR Alternatively, you can run the generated .jar file directly.
```bash
java -jar target/localservice-api-0.0.1-SNAPSHOT.jar
```

6. Access the App by opening your browser and navigate to http://localhost:5000 (or the port you configured).

### Troubleshooting
- **Port Conflict:** If port 5000 is already in use, you can change the port by updating `application.properties`:
```application.properties
server.port=port_number
```
- **Database Connection Issues:** Ensure that the database service is running and correctly configured in the `application.properties`.
- **OPEN AI Issues:** Ensure that open ai key is correctly configured in `application.properties`.
- **MAIL Issues:** Ensure you have provided the valid mail credentials in `application.properties`.

## Contributing
We welcome contributions! To contribute:
1. Fork this repository.
2. Create a new branch (git checkout -b feature/your-feature-name).
3. Commit your changes (git commit -m "Add your message").
4. Push to the branch (git push origin feature/your-feature-name).
5. Open a pull request.


## License
This project is licensed under [Dallas Software Dveelopers Org](https://www.dallassoftwaredevelopers.org)
