# Azure Function with Cosmos DB Integration

## Endeca

## Overview
This project demonstrates how to create an Azure Function that interacts with Azure Cosmos DB using Java. This function uploads data to the database in bulk.
- **Endeca.java**: Contains the main Azure Function logic for reading the JSON file and uploading data to Cosmos DB.
- **facet.java**: Represents the comparing two json and uploading new json data.
- **facet.json**: The JSON file containing the facet data to be uploaded.
- **QRG.json**: The JSON file containing the QRG data to be uploaded.


## Prerequisites
- [Java Development Kit (JDK) 11 or later](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Visual Studio Code](https://code.visualstudio.com/)
- [Azure Functions Core Tools](https://docs.microsoft.com/en-us/azure/azure-functions/functions-run-local)
- [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli)
- [Azure Functions Extension for Visual Studio Code](https://marketplace.visualstudio.com/items?itemName=ms-azuretools.vscode-azurefunctions)

## Setup Instructions

### Step 1: Create a New Azure Functions Project in VS Code
1. Open Visual Studio Code.
2. Press `F1` to open the command palette.
3. Type `Azure Functions: Create New Project...` and select it.
4. Choose a folder for your project.
5. Select `Java` as the language.
6. Select `None` for the template.
7. Choose a version (`3.x` or later).
8. Provide a name for your function app.

### Step 2: Add Dependencies
Add the following dependencies to `pom.xml`:

### Step 3: Configure Azure Cosmos DB
Replace the connection values in with actual Azure Cosmos DB connection string and key.
Also replace the file path in resource folder

### Step 4: Build the project
Use Maven to build the project: mvn clean package  

### Step 5: Deploy to Azure
Follow the steps to deploy the function to Azure. You can use the Azure CLI for deployment.
1. Login into Azure : az login
2. Run the command : func azure functionapp publish function name


### Code Explanation

CosmosClient Initialization:
Sets up the Cosmos DB client with connection details.

HTTP Trigger (run method):
Handles incoming HTTP GET requests, loads JSON data from the QRG.json file, parses it, and uploads it in batches to Cosmos DB.

Facet.java
1. Function Class: Contains the Azure Function definition.
2. run Method: The entry point for the HTTP trigger.
3. Loading JSON File: Reads facet.json from the resources.
4. Parsing JSON: Uses ObjectMapper to parse the JSON into a list of Endeca objects.
5. Uploading Data: Creates a list of bulk operations to upload data to Cosmos DB in batches.
6. Getters and Setters: Provides access methods for the class fields.
7. Error Handling: Catches exceptions and logs errors, returning a 500 response in case of failure.

Endeca.java
1. Function Class: Contains the Azure Function definition.
2. run Method: The entry point for the HTTP trigger.
3. Loading JSON File: Reads facet.json from the resources.
4. Endeca Class: Defines the data structure with fields pk, dimensionName, spec, and id.
5. Uploading Data: Creates a list of bulk operations to upload data to Cosmos DB in batches.
7. Error Handling: Catches exceptions and logs errors, returning a 500 response in case of failure.