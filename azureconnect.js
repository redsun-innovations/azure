const { DefaultAzureCredential } = require('@azure/identity');
const { DataFactoryManagementClient } = require('@azure/arm-datafactory');
const { ResourceManagementClient } = require('@azure/arm-resources');
const { CosmosDBManagementClient } = require('@azure/arm-cosmosdb');
const { BlobServiceClient } = require('@azure/storage-blob');
const fs = require('fs');

const subscriptionId = '9a73a74a-5c48-4829-acfe-38418d839726';
const resourceGroupName = 'jefrinnew1';
const dataFactoryName = 'jefrindf1';
const location = 'eastus';
const databaseName = 'db-1';  
const containerName = 'container-1';  

const credentials = new DefaultAzureCredential();
const dataFactoryClient = new DataFactoryManagementClient(credentials, subscriptionId);
const resourceClient = new ResourceManagementClient(credentials, subscriptionId);
const cosmosClient = new CosmosDBManagementClient(credentials, subscriptionId);
const blobServiceClient = BlobServiceClient.fromConnectionString('DefaultEndpointsProtocol=https;AccountName=jefrystorages;AccountKey=Iy/e0SJ9nAcL///ppjYyQ0e8OVQQd/ZE2HIlvUfKcHPT06sD4f6Z+uSZZdCrd2ppOORNWwJDHM9t+AStH03Tvg==;EndpointSuffix=core.windows.net');

async function createResourceGroup() {
  console.log(`Creating resource group: ${resourceGroupName}...`);
  await resourceClient.resourceGroups.createOrUpdate(resourceGroupName, { location: location });
  console.log(`Resource group created: ${resourceGroupName}`);
}

async function uploadDataToBlobStorage() {
  console.log(`Uploading data.json to Blob Storage...`);
  const containerClient = blobServiceClient.getContainerClient('myjsondata');
  await containerClient.createIfNotExists();
  const content = fs.readFileSync('data.json');
  const blockBlobClient = containerClient.getBlockBlobClient('data.json');
  await blockBlobClient.upload(content, content.length);
  console.log(`data.json uploaded to Blob Storage.`);
}

async function createDataFactory() {
  console.log(`Creating Data Factory: ${dataFactoryName}...`);
  await dataFactoryClient.factories.createOrUpdate(resourceGroupName, dataFactoryName, { location: location, identity: { type: 'SystemAssigned' } });
  console.log(`Data Factory created: ${dataFactoryName}`);
}

async function createLinkedServices() {
  console.log('Creating linked services...');
  await dataFactoryClient.linkedServices.createOrUpdate(resourceGroupName, dataFactoryName, 'AzureBlobStorageLinkedService', {
    properties: {
      type: 'AzureBlobStorage',
      typeProperties: {
        connectionString: 'DefaultEndpointsProtocol=https;AccountName=jefrystorages;AccountKey=Iy/e0SJ9nAcL///ppjYyQ0e8OVQQd/ZE2HIlvUfKcHPT06sD4f6Z+uSZZdCrd2ppOORNWwJDHM9t+AStH03Tvg==;EndpointSuffix=core.windows.net'
      }
    }
  });

  await dataFactoryClient.linkedServices.createOrUpdate(resourceGroupName, dataFactoryName, 'AzureCosmosDBLinkedService', {
    properties: {
      type: 'CosmosDb',
      typeProperties: {
        connectionString: 'AccountEndpoint=https://jefrincosmosdb.documents.azure.com:443/;AccountKey=Vvd0LYgMgVm9ja2Otws6aL1vnVzBccy5my8EzFzjhvErwCZJVOEYiQQx2RNeoGYdjKH77EN7F8L1ACDb3W2KAQ==;',
        databaseName: databaseName
      }
    }
  });
  console.log('Linked services created successfully.');
}

async function createDatasets() {
  console.log('Creating datasets...');
  await dataFactoryClient.datasets.createOrUpdate(resourceGroupName, dataFactoryName, 'BlobStorageDataset', {
    properties: {
      linkedServiceName: {
        referenceName: 'AzureBlobStorageLinkedService',
        type: 'LinkedServiceReference'
      },
      type: 'AzureBlob',
      typeProperties: {
        folderPath: 'myjsondata',
        fileName: 'data.json', 
       format: {
          type: 'JsonFormat',
          filePattern: 'arrayOfObjects'
        }
      }
    }
  });

  await dataFactoryClient.datasets.createOrUpdate(resourceGroupName, dataFactoryName, 'CosmosDBDataset', {
    properties: {
      linkedServiceName: {
        referenceName: 'AzureCosmosDBLinkedService',
        type: 'LinkedServiceReference'
      },
      type: "DocumentDbCollection",
      typeProperties: {
        databaseName: databaseName,
        collectionName: containerName
      }
    }
  });
  console.log('Datasets created successfully.');
}

async function createPipeline() {
  console.log('Creating pipeline...');
  try {
    await dataFactoryClient.pipelines.createOrUpdate(resourceGroupName, dataFactoryName, 'CopyBlobToCosmosDBPipeline', {
      activities: [
        {
          name: 'CopyBlobToCosmosDB',
          type: 'Copy',
          inputs: [{ referenceName: 'BlobStorageDataset' }],
          outputs: [{ referenceName: 'CosmosDBDataset' }],
          typeProperties: {
            source: { type: 'BlobSource' },
            sink: {
              type: 'DocumentDbCollectionSink',
              writeBatchSize: 1000, // Adjust batch size if needed
              writeBehavior: 'upsert', // Adjust behavior if needed
              collectionNamePattern: '{db-1}',
              databaseName: databaseName // Add the database name here
            },
            // Ensure your column mappings are correctly defined
            // For example:
            translator: {
              type: 'TabularTranslator',
              columnMappings: [
                {
                  source: { name: 'sourceColumnName' },
                  sink: { name: 'sinkColumnName' }
                },
                // Add more mappings if needed
              ]
            }
          }
        }
      ]
    });
    console.log('Pipeline created successfully.');
  } catch (error) {
    console.error('Error creating pipeline:', error);
  }
}


async function runPipeline() {
  console.log('Running pipeline...');
    const response = await dataFactoryClient.pipelines.createRun(resourceGroupName, dataFactoryName, 'CopyBlobToCosmosDBPipeline');
    console.log(`Pipeline run initiated with Run ID: ${response.runId}`);
}


async function main() {
  try {
    console.log('Creating resource group...');
    await createResourceGroup();
    console.log('Resource group created successfully.');

    console.log('Uploading data.json to Blob Storage...');
    await uploadDataToBlobStorage();
    console.log('data.json uploaded successfully.');

    console.log('Creating Data Factory...');
    await createDataFactory();
    console.log('Data Factory created successfully.');

    console.log('Creating linked services...');
    await createLinkedServices();
    console.log('Linked services created successfully.');

    console.log('Creating datasets...');
    await createDatasets();
    console.log('Datasets created successfully.');

    console.log('Creating pipeline...');
    await createPipeline();
    console.log('Pipeline created successfully.');

    console.log('Running pipeline...');
    await runPipeline();
    console.log('Pipeline run initiated successfully.');
  } catch (error) {
    console.error('Error creating Data Factory resources:', error);
  }
}

main();
