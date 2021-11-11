const express = require("express");
const predictionController = require("../controllers/prediction.controller");
const router = express.Router();

router.route("/").post(predictionController.getPrediction);

router.route("/report/:email").get(predictionController.getStats);

module.exports = router;
