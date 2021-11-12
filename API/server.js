const dotenv = require("dotenv");
dotenv.config({ path: "./.env" });
const mongoose = require("mongoose");
const app = require("./app");
const http = require("http");

const Cloudant = require("@cloudant/cloudant");

const cloudant = new Cloudant({
  url: "https://19bfb4e7-9436-46dc-9482-9c23c5a73963-bluemix.cloudantnosqldb.appdomain.cloud",
  plugins: {
    iamauth: { iamApiKey: "sOGe8NNT3MCHJablwqsaslgwZXWOs6mDAYCCz09lOOKs" },
  },
});

let db;

exports.db = cloudant.use("coughit");

// var ddoc = {
//   _id: "_design/cit",
//   name: "j",
// };

// db.insert(ddoc, function (err, result) {
//   if (err) {
//     throw err;
//   }
//   console.log("insert successful");
// });

// var createDocument = function (callback) {
//   console.log("Creating document 'mydoc'");
//   // specify the id of the document so you can update and delete it later
//   // db.insert({ _id: "mydoc", a: 1, b: "two" }, function (err, data) {
//   //   console.log("Error:", err);
//   //   console.log("Data:", data);
//   //   callback(err, data);
//   // });
//   cloudant.db.list(function (err, body) {
//     body.forEach(function (db) {
//       console.log(db);
//       db.insert({ _id: "mydoc", a: 1, b: "two" }, function (err, data) {
//         console.log("Error:", err);
//         console.log("Data:", data);
//         callback(err, data);
//       });
//     });
//   });
// };

// createDocument();

// {
//   "apikey": "sOGe8NNT3MCHJablwqsaslgwZXWOs6mDAYCCz09lOOKs",
//   "host": "19bfb4e7-9436-46dc-9482-9c23c5a73963-bluemix.cloudantnosqldb.appdomain.cloud",
//   "iam_apikey_description": "Auto-generated for key b2e2cb63-0ad3-4140-ac11-bebf15979a95",
//   "iam_apikey_name": "Cloudant-key",
//   "iam_role_crn": "crn:v1:bluemix:public:iam::::serviceRole:Manager",
//   "iam_serviceid_crn": "crn:v1:bluemix:public:iam-identity::a/4d890cfde01647758b6d536bcd34c17f::serviceid:ServiceId-dbdeadae-6e93-4d5c-b634-49517d0bec72",
//   "url": "https://19bfb4e7-9436-46dc-9482-9c23c5a73963-bluemix.cloudantnosqldb.appdomain.cloud",
//   "username": "19bfb4e7-9436-46dc-9482-9c23c5a73963-bluemix"
// }

const DB_URI = process.env.DATABASE_URI;

// mongoose.connect(DB_URI).then((con) => {
//   console.log(`Successfully connected to DB`);
// });

//set port, listen for requests
const PORT = process.env.PORT || 8080;

http.createServer(app);
app.listen(8080, () => {
  console.log(`Local server running on port ${PORT}`);
});
