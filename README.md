# video-service
Install rest shell as per mentioned here in the documentation: https://docs.spring.io/spring-data/rest/docs/2.0.0.M1/reference/html/rest-shell-chapter.html
# Download Source code and run: gradle clean bootRun

# Application will be running from port 8080 and available in http://localhost:8080/

# Swagger UI will be available for referring to Documentation: http://localhost:8080/swagger-ui/index.html#/video-controller

# Rest Shell Sample Commands:
rest-shell
follow v1/files
Then follow below sample commands.

  Upload: 
  post --data "{file:'/Users/folder/Downloads/sample-wmv.wmv'}"
  
  Delete: 
  delete sample-avi.avi
  
  List: 
  get
  
  Search (Works only for entered Values. If dont want to search leave as blank. 
  FileName contains or Size <= or Type contains or DurationOfVideoFileInSeconds <=): 
  get search --params "{name: 'a'}"
  
  
