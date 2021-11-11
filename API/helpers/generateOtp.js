exports.generateOtp = (noOfDigits) => {
  const time = Date.now() * 1;
  const str = time.toString(10);
  const digits = str;
  let OTP = "";
  for (let i = 0; i < noOfDigits; i++) {
    OTP += digits[Math.floor(Math.random() * 10)];
  }
  return OTP;
};
