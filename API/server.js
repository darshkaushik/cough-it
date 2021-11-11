const dotenv = require("dotenv");
dotenv.config({ path: `./.env` });
const mongoose = require("mongoose");
const app = require("./app");
const http = require("http");

const DB_URI = process.env.DATABASE_URI;

mongoose.connect(DB_URI).then((con) => {
  console.log(`Successfully connected to DB`);
});

//set port, listen for requests
const PORT = process.env.PORT || 8080;

http.createServer(app);
app.listen(8080, () => {
  console.log(`Local server running on port ${PORT}`);
});
