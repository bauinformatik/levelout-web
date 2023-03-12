# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.7.9-SNAPSHOT/gradle-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.7.9-SNAPSHOT/gradle-plugin/reference/html/#build-image)
* [Thymeleaf](https://docs.spring.io/spring-boot/docs/2.7.9-SNAPSHOT/reference/htmlsingle/#web.servlet.spring-mvc.template-engines)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.7.9-SNAPSHOT/reference/htmlsingle/#web)
* [BIMServer](https://bimserver.org/)

### Guides
The following guides illustrate how to use some features concretely:

* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### How to deploy the WAR
1. Under the project, creat war file for the application using below command
```
mvn clean package
```
2. Within the same folder 'target' folder will be created. Copy 'levelout-web.war' from this folder 'target'
3. If external tomcat version >= 10.0, paste the war file under the folder 'tomcat_installation_folder\webapps-javaee'
    Else, paste the war file under 'tomcat_installation_folder\webapps' folder
4. Restart your tomcat server
5. Hit the URL 'http://host:port/levelout-web' to start using the levelout-web application.


### How to run the JAR
1. Keep application.properties file at a location in your machine. (The example of the file content below)
```
server.port=8080

welcome.message=Friend
spring.thymeleaf.cache=false

bimserver.host=http://127.0.0.1:8082
bimserver.user=user@email.com
bimserver.password=password

spring.servlet.multipart.max-file-size=2050MB
spring.servlet.multipart.max-request-size=2050MB
```
2. You can edit spring properties as needed. Refer various Reference Documentation for the same
3. Make sure to change the server port and BIMServer properties in the properties file as needed
4. Download the latest jar from [here](https://github.com/bauinformatik/levelout-web/releases)
5. Run the jar with the command. Refer example below
```
java -jar levelout-web-0.0.1-SNAPSHOT.jar --spring.config.location=file:///F:/Code/bauhaus/levelout-web-jar/properties_file/depth/application.properties
```


### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)
