const express = require("express");
const predictionController = require("../controllers/prediction.controller");
const router = express.Router();

router.route("/").post(predictionController.getPrediction);

module.exports = router;
