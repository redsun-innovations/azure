#!/bin/sh

# Ensure the script exits if any command fails
set -e

# Define variables
APP_NAME="my-spring-boot-app"
JAR_NAME="my-spring-boot-app.jar"
DOCKER_IMAGE_NAME="my-spring-boot-app-image"

# Step 1: Compile the Java application and create the JAR file
echo "Compiling the Java application..."
mvn clean package

# Step 2: Build the Docker image
echo "Building the Docker image..."
docker build -t $DOCKER_IMAGE_NAME .

echo "Docker image $DOCKER_IMAGE_NAME built successfully."
