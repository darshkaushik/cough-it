const express = require("express");
const authController = require("../controllers/auth.controller");
const router = express.Router();

router.route("/signup").post(authController.signup);
router.route("/signin").post(authController.signin);

router.route("/login").post(authController.loginUser);

module.exports = router;
