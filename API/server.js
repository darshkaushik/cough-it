const dotenv = require("dotenv");
dotenv.config({ path: "./.env" });
const mongoose = require("mongoose");
const app = require("./app");
const http = require("http");

const Cloudant = require("@cloudant/cloudant");
const cloudant = new Cloudant({
  url: process.env.CLOUDANT_DB_URL,
  plugins: {
    iamauth: { iamApiKey: process.env.CLOUDANT_IAM_API_KEY },
  },
});

let db;

exports.db = cloudant.use("coughit");

const DB_URI = process.env.DATABASE_URI;

//set port, listen for requests
const PORT = process.env.PORT || 8080;

http.createServer(app);
app.listen(8080, () => {
  console.log(`Local server running on port ${PORT}`);
});
