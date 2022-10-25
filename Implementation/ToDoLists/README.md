# ToDoLists

How to start the ToDoLists application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/ToDoLists-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/users/healthcheck`

Database Connection Check
---

To see your applications database connection, enter url `http://localhost:8081/users/dbConnectionCheck`

Add a new user to the db
---

To add a new user to the db, enter url `http://localhost:8081/users/addUser?usename=<username>&password=<password>`

Log in a user 
---

To log in, enter url `http://localhost:8081/users/logIn?usename=<username>&password=<password>`

Reset password
---

To reset password, enter url `http://localhost:8081/users/resetPassword?usename=<username>&oldPassword=<oldPassword>&newPassword=<newPassword>`
