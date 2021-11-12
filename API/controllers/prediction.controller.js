// import libraries
const fs = require("fs");
const { PythonShell } = require("python-shell");
const { exec } = require("child_process");
const axios = require("axios");
const AWS = require("aws-sdk");
const sampleData = require("../sample-request");
const XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;
const { v4: uuidv4 } = require("uuid");

// IAM API KEY of IBM Cloud
const API_KEY = "Z53pnfFDwVayUCVgtVFbDcgmKKPIT0GlUeOY2yZTnV5P";

// ML Endpoint from IBM
const ML_ENDPOINT =
  "https://us-south.ml.cloud.ibm.com/ml/v4/deployments/028e5f30-3b99-45a9-9f21-b6b2670101b4/predictions?version=2021-11-12";

// Database Connectivity
const Cloudant = require("@cloudant/cloudant");
const cloudant = new Cloudant({
  url: "https://19bfb4e7-9436-46dc-9482-9c23c5a73963-bluemix.cloudantnosqldb.appdomain.cloud",
  plugins: {
    iamauth: { iamApiKey: "sOGe8NNT3MCHJablwqsaslgwZXWOs6mDAYCCz09lOOKs" },
  },
});
let db;

db = cloudant.use("predictions");

exports.getStats = async (req, res) => {
  const email = req.params.email;
  db.find(
    { selector: { email, documentType: "predictions" } },
    function (err, existingdoc) {
      return res.status(200).json({
        status: "success",
        data: existingdoc.docs,
      });
    }
  );
};

exports.getPrediction = async (req, res) => {
  try {
    const audioSample = req.body.audio;

    fs.writeFileSync(
      "./assets/data/sample.m4a",
      Buffer.from(
        audioSample.replace("data:audio/mpeg_4; codecs=opus;base64,", ""),
        "base64"
      )
    );
    let dataToSend;
    let options = {};
    await exec(
      "ffmpeg -i assets/data/sample.m4a assets/output/sample.wav",
      (err, stdout, stderr) => {
        if (err) {
          console.error(err);
        } else {
          console.log("Converted");
        }
      }
    );

    await PythonShell.run(
      "scripts/preprocessing.py",
      options,
      async function (err, result) {
        if (err) throw err;
        dataToSend = result.toString();

        const inputDataToBeSentToModel = {
          input_data: [
            {
              values: JSON.parse(dataToSend),
            },
          ],
        };

        function getToken(errorCallback, loadCallback) {
          const req = new XMLHttpRequest();
          req.addEventListener("load", loadCallback);
          req.addEventListener("error", errorCallback);
          req.open("POST", "https://iam.cloud.ibm.com/identity/token");
          req.setRequestHeader(
            "Content-Type",
            "application/x-www-form-urlencoded"
          );
          req.setRequestHeader("Accept", "application/json");
          req.send(
            `grant_type=urn:ibm:params:oauth:grant-type:apikey&apikey=${API_KEY}`
          );
        }

        function apiPost(
          scoring_url,
          token,
          payload,
          loadCallback,
          errorCallback
        ) {
          const oReq = new XMLHttpRequest();
          oReq.addEventListener("load", loadCallback);
          oReq.addEventListener("error", errorCallback);
          oReq.open("POST", scoring_url);
          oReq.setRequestHeader("Accept", "application/json");
          oReq.setRequestHeader("Authorization", "Bearer " + token);
          oReq.setRequestHeader(
            "Content-Type",
            "application/json;charset=UTF-8"
          );
          oReq.send(payload);
        }

        getToken(
          (err) => console.log(err),
          function () {
            let tokenResponse;
            try {
              tokenResponse = JSON.parse(this.responseText);
            } catch (ex) {}
            // const payloadOld = JSON.stringify(sampleData.sample_data);
            const payload = JSON.stringify(inputDataToBeSentToModel);
            // console.log("payloaddddddddddddd", payload);
            // console.log("payload old", payloadOld);

            // Below two lines are responsible for seeing how the payload looks like

            // fs.writeFileSync("payloadOld.txt", payloadOld, function () {
            //   console.log("file written");
            // });
            // fs.writeFileSync("payloadNew.txt", payload, function () {
            //   console.log("file written");
            // });

            const scoring_url = ML_ENDPOINT;
            apiPost(
              scoring_url,
              tokenResponse.access_token,
              payload,
              function (resp) {
                let parsedPostResponse;
                try {
                  parsedPostResponse = JSON.parse(this.responseText);
                } catch (ex) {}

                const responseToTheApi = {
                  predictions: [
                    {
                      id: "dense_2",
                      fields: [
                        "prediction",
                        "prediction_classes",
                        "probability",
                      ],
                      values: [
                        [
                          [0.00020936131477355957],
                          [0],
                          [0.00020936131477355957],
                        ],
                      ],
                    },
                  ],
                };

                const predictionValue =
                  responseToTheApi.predictions[0].values[0][0][0];

                // Store prediction into Database
                const ddocPrediction = {
                  _id: uuidv4(),
                  documentType: "predictions",
                  email: req.body.email,
                  prediction: predictionValue.toString(),
                  date: new Date(),
                };
                db.insert(ddocPrediction, function (err, result) {
                  if (err) {
                    throw err;
                  }
                });

                // Send response to App
                return res.status(200).json({
                  status: "success",
                  data: parsedPostResponse,
                });
              },
              function (error) {
                console.log(error);
              }
            );
          }
        );
      }
    );
  } catch (err) {
    res.status(400).json({
      status: "fail",
      message: err,
    });
  }
};
