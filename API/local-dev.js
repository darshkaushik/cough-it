const app = require("./app");
const http = require("http");
var crypto = require("crypto-js");
//set port, listen for requests
const PORT = process.env.PORT || 8080;

function getSignatureKey(key, dateStamp, regionName, serviceName) {
  var kDate = crypto.HmacSHA256(dateStamp, "AWS4" + key);
  var kRegion = crypto.HmacSHA256(regionName, kDate);
  var kService = crypto.HmacSHA256(serviceName, kRegion);
  var kSigning = crypto.HmacSHA256("aws4_request", kService);
  return kSigning;
}

http.createServer(app);
app.listen(8080, () => {
  console.log(`Local server running on port ${PORT}`);
});
