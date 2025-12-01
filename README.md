# 🐞 Bug Tracker

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17%2B-blue)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-19-61dafb)](https://reactjs.org/)

A full-stack bug tracking application with AI-powered analysis for efficient issue management. Built with Spring Boot and React, this application helps teams track, prioritize, and resolve bugs effectively.

## ✨ Features

### 🤖 AI-Powered Analysis
- Automatic priority and severity prediction
- Smart bug classification
- Similar issue detection using NLP

### 🐛 Bug Management
- Create, view, update, and delete bugs
- Assign bugs to team members
- Set severity levels (Low, Medium, High, Critical)
- Track status (Open, In-Progress, Resolved, Closed)
- Add comments and attachments

### 📊 Project Management
- Create and manage multiple projects
- Organize bugs by project
- Team collaboration tools
- Progress tracking

### � User Management
- Role-based access control (Admin/Developer/Tester)
- User profiles and preferences
- Activity tracking
- Secure authentication with JWT

## � Tech Stack

### Backend
- **Java 17**
- **Spring Boot 3.5**
- **Spring Security** with JWT
- **Spring Data JPA**
- **PostgreSQL**
- **DJL (Deep Java Library)** for AI/ML
- **Maven**

### Frontend
- **React 19**
- **React Router**
- **Tailwind CSS**
- **Axios** for API calls
- **React Query** for data fetching
- **React Hook Form** for forms

## 🛠️ Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- PostgreSQL 14 or higher
- Maven 3.8 or higher
- npm 9.x or higher

## � Quick Start

### Backend Setup

```bash
# Clone the repository
git clone https://github.com/Pandidurai-22/bug-tracker-backend.git
cd bug-tracker-backend

# Configure database in application.properties
cp src/main/resources/application.example.properties src/main/resources/application.properties

# Build and run
mvn spring-boot:run
```

### Frontend Setup

```bash
# In a new terminal
cd ../bug-tracker-client
npm install
npm start
```

The application will be available at:
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- API Documentation: http://localhost:8080/swagger-ui.html

## 🔧 Configuration

### Environment Variables
Create `.env` file in the frontend root:
```env
REACT_APP_API_URL=http://localhost:8080/api
```

### Database Setup
1. Create a new PostgreSQL database:
```sql
CREATE DATABASE bugtracker;
CREATE USER buguser WITH ENCRYPTED PASSWORD 'yourpassword';
GRANT ALL PRIVILEGES ON DATABASE bugtracker TO buguser;
```

2. Update `application.properties` with your database credentials.

## 📚 API Documentation

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `GET /api/auth/me` - Get current user profile

### Bugs
- `GET /api/bugs` - List all bugs
- `POST /api/bugs` - Create new bug
- `GET /api/bugs/{id}` - Get bug details
- `PUT /api/bugs/{id}` - Update bug
- `DELETE /api/bugs/{id}` - Delete bug
- `GET /api/bugs/project/{projectId}` - Get bugs by project

### Projects
- `GET /api/projects` - List all projects
- `POST /api/projects` - Create project
- `GET /api/projects/{id}` - Get project details
- `PUT /api/projects/{id}` - Update project
- `DELETE /api/projects/{id}` - Delete project

### AI Analysis
- `POST /api/ai/analyze/priority` - Predict bug priority
- `POST /api/ai/analyze/severity` - Predict bug severity
- `GET /api/ai/similar-bugs` - Find similar bugs

## 📸 Screenshots

| Home Page | Dashboard |
|-----------|-----------|
| ![Home Page](./assets/Home.png) | ![Dashboard Page](./assets/Dashboard.png) |

| Create Bug | AI Analysis |
|------------|-------------|
| ![Create Bug](./assets/create-bug(before).png) | ![AI Create Bug](./assets/create-bug(after).png) |

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 Future Enhancements

- [ ] Real-time notifications (Socket.io)
- [ ] Email alerts for bug updates
- [ ] Sprint management
- [ ] Advanced reporting and analytics
- [ ] Mobile application
- [ ] Integration with version control (GitHub/GitLab)
- [ ] File attachments for bugs
- [ ] Advanced search and filtering

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## � Author

**Pandidurai S**  
💼 Full-Stack Software Engineer (1.5+ years experience)  
📧 pandidurai32127@gmail.com  
🔗 [LinkedIn](https://in.linkedin.com/in/pandidurai-s-6a30b8212)  

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [React](https://reactjs.org/)
- [Tailwind CSS](https://tailwindcss.com/)
- [DJL](https://djl.ai/) - Deep Java Library