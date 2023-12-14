# Serverless functions for AWS Lambda
The module `serverless-demo` contains different functions that can be run by the AWS Lambda cloud service.

## Installation

1. Inside the module, run maven `clean` and then `package`
2. The command will generate a folder named `target` that will contain a file that will end with `-aws.jar`
3. This JAR file will be deployed on AWS Lambda

## Functions

- `reverse` - takes a string and reverses it
  - Example: Lando Norris => sirroN odnaL