const fs = require("fs");
const { PythonShell } = require("python-shell");
const { exec } = require("child_process");
const axios = require("axios");
const AWS = require("aws-sdk");

// exports.getPrediction = async (req, res) => {
//   try {
//     const audioSample = req.body.audio;

//     fs.writeFileSync(
//       "./assets/data/sample.m4a",
//       Buffer.from(
//         audioSample.replace("data:audio/mpeg_4; codecs=opus;base64,", ""),
//         "base64"
//       )
//     );

//     let dataToSend;

//     let options = {};

//     exec(
//       "ffmpeg -i assets/data/sample.m4a assets/output/sample.wav",
//       (err, stdout, stderr) => {
//         if (err) {
//           //some err occurred
//           console.error(err);
//         } else {
//           console.log("Converted");
//         }
//       }
//     );

//     PythonShell.run(
//       "scripts/preprocessing.py",
//       options,
//       async function (err, result) {
//         if (err) throw err;
//         dataToSend = result.toString();

//         const data = JSON.stringify(dataToSend);

//         var request = require("request");
//         var Crypto = require("crypto-js");
//         var strftime = require("strftime");

//         function sign(key, msg) {
//           return Crypto.HmacSHA256(key, msg, { asBytes: true });
//         }

//         function getSignatureKey(key, dateStamp, regionName, serviceName) {
//           var kDate = sign(dateStamp, "AWS4" + key);
//           var kRegion = sign(regionName, kDate);
//           var kService = sign(serviceName, kRegion);
//           var kSigning = sign("aws4_request", kService);

//           return kSigning;
//         }

//         function sha256(str) {
//           return Crypto.SHA256(str);
//         }

//         // ************* REQUEST VALUES *************
//         var method = "POST";
//         var service = "sagemaker";
//         var host = "runtime.sagemaker.us-east-2.amazonaws.com";
//         var region = "us-east-2";
//         var endpoint =
//           "https://runtime.sagemaker.us-east-2.amazonaws.com/endpoints/tensorflow-inference-2021-10-10-02-56-05-523/invocations";
//         var request_parameters = "Action=DescribeRegions&Version=2013-10-15";

//         var access_key = "AKIAXV27PUM6OXOVBVN5";
//         var secret_key = "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";

//         var date = new Date();
//         var amzdate = strftime("%Y%m%dT%H%M%SZ", date);
//         var datestamp = strftime("%Y%m%d", date);

//         // ************* TASK 1: CREATE A CANONICAL REQUEST *************
//         var canonical_uri = "/";
//         var canonical_querystring = request_parameters;
//         var canonical_headers =
//           "host:" + host + "\n" + "x-amz-date:" + amzdate + "\n";
//         var signed_headers = "host;x-amz-date";

//         var payload_hash = sha256("");
//         var canonical_request =
//           method +
//           "\n" +
//           canonical_uri +
//           "\n" +
//           canonical_querystring +
//           "\n" +
//           canonical_headers +
//           "\n" +
//           signed_headers +
//           "\n" +
//           payload_hash;

//         // ************* TASK 2: CREATE THE STRING TO SIGN*************
//         var algorithm = "AWS4-HMAC-SHA256";
//         var credential_scope =
//           datestamp + "/" + region + "/" + service + "/" + "aws4_request";
//         var string_to_sign =
//           algorithm +
//           "\n" +
//           amzdate +
//           "\n" +
//           credential_scope +
//           "\n" +
//           sha256(canonical_request);

//         // ************* TASK 3: CALCULATE THE SIGNATURE *************
//         var signing_key = getSignatureKey(
//           secret_key,
//           datestamp,
//           region,
//           service
//         );
//         var signature = sign(signing_key, string_to_sign);

//         // ************* TASK 4: ADD SIGNING INFORMATION TO THE REQUEST *************
//         var authorization_header =
//           algorithm +
//           " " +
//           "Credential=" +
//           access_key +
//           "/" +
//           credential_scope +
//           ", " +
//           "SignedHeaders=" +
//           signed_headers +
//           ", " +
//           "Signature=" +
//           signature;
//         var headers = {
//           "x-amz-date": amzdate,
//           Authorization: authorization_header,
//           host: host,
//         };
//         var request_url = endpoint + "?" + canonical_querystring;

//         var options = {
//           url: request_url,
//           headers: headers,
//         };

//         request(options, function (err, res) {
//           console.log(res.statusCode, res.body);
//         });
//       }
//     );

//     return res.status(200).json({
//       status: "success",
//       data: response.data,
//     });

//     // return res.status(200).json({
//     //   status: "success",
//     //   dataToSend: dataToSend,
//     // });
//   } catch (err) {
//     console.log(err);
//     res.status(400).json({
//       status: "fail",
//       message: err,
//     });
//   }
// };

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
