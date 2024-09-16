# kafka-app Assessment of TSWorks:-

# CI/CD Pipeline and Kafka Application Deployment
This project implements a complete CI/CD pipeline for Kafka, producer, and consumer applications using GitHub Actions. The pipeline builds and packages the applications, pushes Docker images to Docker Hub, and deploys them to a local Kubernetes cluster (Minikube) using Helm charts.

# Prerequisites
Before running the pipeline, ensure that you have the following:
1. Docker Hub account with credentials set as GitHub secrets (DOCKER_HUB_USERNAME, DOCKER_HUB_TOKEN).
2. A properly configured Minikube cluster on your local machine.
3. A valid KUBECONFIG file stored as a GitHub secret (KUBECONFIG), base64-encoded.
4. Helm installed on the local machine.

# Steps
# 1. CI/CD Pipeline Overview
The pipeline is triggered on each push to the main branch.

# 2. Job Stages
   a. Checkout Code
      The repository is checked out using the GitHub Actions checkout action.

      yaml
- name: Checkout code
  uses: actions/checkout@v3 
   b. List Directory Structure
      This step lists the directory structure for debugging purposes.

     yaml
- name: List directory structure
  run: |
    pwd
    ls -R
   c. Set Execute Permissions
      Ensures the mvnw wrapper script for both the consumer and producer applications is executable.

     yaml
- name: Set execute permission for mvnw
  run: |
    chmod +x ./consumer/mvnw
    chmod +x ./producer/mvnw
   d. Set Up JDK
      This step installs Java JDK 17 using the actions/setup-java action.

yaml
- name: Set up JDK
  uses: actions/setup-java@v3
  with:
    java-version: '17'
    distribution: 'temurin'
  e. Install Maven
     Maven is downloaded and installed.

yaml
- name: Install Maven
  run: |
    wget https://downloads.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip
    unzip apache-maven-3.9.9-bin.zip
    sudo mv apache-maven-3.9.9 /opt/maven
    echo "M2_HOME=/opt/maven" | sudo tee -a /etc/environment
    echo "PATH=${M2_HOME}/bin:${PATH}" | sudo tee -a /etc/environment
    source /etc/environment
  f. Build and Package Consumer Application
     This step runs Maven to clean and package the consumer application.

yaml
- name: Build and package consumer app
  working-directory: ./consumer
  run: mvn clean package -DskipTests
  g. Build Docker Image for Consumer Application
     A Docker image for the consumer app is built and pushed to Docker Hub.

yaml
- name: Build Docker image for consumer app
  run: |
    docker build -t docker.io/<your-docker-username>/consumer-app:latest ./consumer
    echo "${{ secrets.DOCKER_HUB_TOKEN }}" | docker login docker.io -u "${{ secrets.DOCKER_HUB_USERNAME }}" --password-stdin
    docker push docker.io/<your-docker-username>/consumer-app:latest
  h. Build and Package Producer Application
     This step runs Maven to clean and package the producer application.

yaml
- name: Build and package producer app
  working-directory: ./producer
  run: mvn clean package -DskipTests
  i. Build Docker Image for Producer Application
     A Docker image for the producer app is built and pushed to Docker Hub.

yaml
- name: Build Docker image for producer app
  run: |
    docker build -t docker.io/<your-docker-username>/producer-app:latest ./producer
    echo "${{ secrets.DOCKER_HUB_TOKEN }}" | docker login docker.io -u "${{ secrets.DOCKER_HUB_USERNAME }}" --password-stdin
    docker push docker.io/<your-docker-username>/producer-app:latest

# 3. Kubernetes Deployment
     a. Install Helm
        Helm is installed to manage Kubernetes deployments.

yaml
- name: Set up Helm
  run: |
    curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
     b. Setup kubeconfig
        This step sets up the kubeconfig file for connecting to the Kubernetes cluster.

yaml
- name: Setup kubeconfig
  run: |
    mkdir -p $HOME/.kube
    echo "${{ secrets.KUBECONFIG }}" | base64 -d > $HOME/.kube/config
    chmod 600 $HOME/.kube/config
     c. Debug Kubernetes Configuration
        Checks the Kubernetes configuration and nodes for troubleshooting.

yaml
- name: Debug kubeconfig
  run: |
    kubectl config view --raw
    kubectl get nodes || echo "No nodes available or kubeconfig is incorrect."
     d. Deploy Consumer Application
        Deploys the consumer app using Helm charts to Kubernetes.

yaml
- name: Deploy consumer app to Kubernetes
  run: |
    helm upgrade --install consumer-app ./helm/consumer-chart --namespace my-namespace --create-namespace
     e. Deploy Producer Application
        Deploys the producer app using Helm charts to Kubernetes.

yaml
- name: Deploy producer app to Kubernetes
  run: |
    helm upgrade --install producer-app ./helm/producer-chart --namespace my-namespace --create-namespace
    f. Deploy Kafka
       Deploys the Kafka broker using Helm charts to Kubernetes.

yaml
- name: Deploy Kafka to Kubernetes
  run: |
    helm upgrade --install kafka ./helm/kafka-chart --namespace my-namespace --create-namespace
     g. Deploy Zookeeper
        Deploys Zookeeper using Helm charts to Kubernetes.

yaml
- name: Deploy Zookeeper to Kubernetes
  run: |
    helm upgrade --install zookeeper ./helm/zookeeper-chart --namespace my-namespace --create-namespace

# 4. Challenges and Issues
     Kubernetes Configuration Issues: There have been challenges with kubeconfig access and ensuring the Kubernetes cluster is reachable.
     Note: Continue troubleshooting Kubernetes setup, need to ensure correct pod deployment, and validate REST API endpoints for message posting.

