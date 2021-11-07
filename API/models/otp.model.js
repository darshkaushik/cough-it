const mongoose = require("mongoose");

const otpSchema = new mongoose.Schema(
  {
    otpValue: {
      type: String,
      index: true,
    },
    otpExpiration: {
      type: Number,
      default: 5,
    },
    otpFor: {
      type: String,
      unique: true,
      index: true,
    },
  },
  {
    timestamps: true,
  }
);

module.exports = mongoose.model("OTP", otpSchema);
