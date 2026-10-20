FROM jenkins/jenkins:lts
USER root

# تسطيب Docker CLI عشان نقدر نعمل docker build جوه البايبلاين
RUN apt-get update && \
    apt-get install -y ca-certificates curl gnupg lsb-release && \
    mkdir -m 0755 -p /etc/apt/keyrings && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg && \
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/debian $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null && \
    apt-get update && \
    apt-get install -y docker-ce-cli && \
    rm -rf /var/lib/apt/lists/*
USER jenkins
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
ENV CASC_JENKINS_CONFIG="/var/jenkins_home/casc_configs/"

RUN jenkins-plugin-cli --plugins "configuration-as-code job-dsl workflow-aggregator git kubernetes docker-workflow"

COPY casc.yaml /var/jenkins_home/casc_configs/casc.yaml
COPY seed-job.groovy /usr/share/jenkins/ref/init.groovy.d/seed-job.groovy
