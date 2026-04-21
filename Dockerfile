FROM tomcat:10-jdk21

# Supprimer les applis par défaut de Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copier le WAR (contient tout le projet compilé)
COPY clubs.war /usr/local/tomcat/webapps/ROOT.war

# Copier les JARs dans le classpath de Tomcat
COPY lib/mysql-connector-java-8.0.28.jar /usr/local/tomcat/lib/
COPY lib/gson-2.10.1.jar /usr/local/tomcat/lib/

EXPOSE 8080