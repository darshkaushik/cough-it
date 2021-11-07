const fs = require("fs");
const { PythonShell } = require("python-shell");
const { exec } = require("child_process");
const axios = require("axios");
const AWS = require("aws-sdk");

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

    exec(
      "ffmpeg -i assets/data/sample.m4a assets/output/sample.wav",
      (err, stdout, stderr) => {
        if (err) {
          //some err occurred
          console.error(err);
        } else {
          console.log("Converted");
        }
      }
    );

    PythonShell.run(
      "scripts/preprocessing.py",
      options,
      async function (err, result) {
        if (err) throw err;
        dataToSend = result.toString();

        const data = JSON.stringify(dataToSend);

        // console.log(data);

        // return res.status(200).json({
        //   status: "success",
        //   dataToSend: dataToSend,
        // });

        // const sagemaker = require("sagemaker")({
        //   region: "us-east-2",
        //   EndpointName:
        //     "https://runtime.sagemaker.us-east-2.amazonaws.com/endpoints/tensorflow-inference-2021-10-10-02-56-05-523/invocations",
        // });

        // await sagemaker(dataToSend, (err, data) => {
        //   console.log(data);
        // });

        const sageMakerRuntime = new AWS.SageMakerRuntime({
          region: "us-east-2",
        });

        var params = {
          Body: new Buffer(`{"instances": ${dataToSend}}`),
          EndpointName: "tensorflow-inference-2021-10-10-02-56-05-523",
        };

        sageMakerRuntime.invokeEndpoint(params, function (err, data) {
          // responseData = JSON.parse(Buffer.from(data.Body).toString("utf8"));
          console.log(data);
        });

        // return (body, cb) => {
        //   console.log(body, "body");
        //   params.Body = JSON.stringify(body);
        //   sageMakerRuntime.invokeEndpoint(params, (err, data) => {
        //     if (err) {
        //       cb(err, null);
        //       return;
        //     }
        //     cb(null, JSON.parse(Buffer.from(data.Body).toString("utf8")));
        //   });
        // };

        return res.status(200).json({
          status: "success",
          dataToSend: dataToSend,
        });
      }
    );
  } catch (err) {
    console.log(err);
    res.status(400).json({
      status: "fail",
      message: err,
    });
  }
};
