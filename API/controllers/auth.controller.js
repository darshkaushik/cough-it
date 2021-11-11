const User = require("../models/user.model");
const Otp = require("../models/otp.model");
const bcrypt = require("bcryptjs");
const { promisify } = require("util");
const asyncHandler = require("../helpers/async");
const { v4: uuidv4 } = require("uuid");

const Cloudant = require("@cloudant/cloudant");

const cloudant = new Cloudant({
  url: "https://19bfb4e7-9436-46dc-9482-9c23c5a73963-bluemix.cloudantnosqldb.appdomain.cloud",
  plugins: {
    iamauth: { iamApiKey: "sOGe8NNT3MCHJablwqsaslgwZXWOs6mDAYCCz09lOOKs" },
  },
});

let db;

db = cloudant.use("coughit");

exports.loginUser = asyncHandler(async (req, res) => {
  const { email, uid, profile_url } = req.body;
  db.find(
    { selector: { email: req.body.email, documentType: "users" } },
    function (err, existingdoc) {
      if (
        existingdoc &&
        existingdoc.docs[0] &&
        existingdoc.docs[0].email === req.body.email
      ) {
        return res.status(200).json({
          status: "success",
          data: req.body,
        });
      } else {
        const ddoc = {
          _id: uuidv4(),
          documentType: "users",
          email,
          uid,
          profile_url,
        };
        db.insert(ddoc, function (err, result) {
          if (err) {
            throw err;
          }
          console.log("insert successful");
          return res.send("User Created successfully");
        });
      }
    }
  );
});

const jwt = require("jsonwebtoken");

const signToken = (id) => {
  return jwt.sign({ id }, process.env.JWT_SECRET, {
    expiresIn: process.env.JWT_EXPIRES_IN,
  });
};

const createSendToken = asyncHandler(async (user, statusCode, res) => {
  const token = signToken(user._id);
  user.token = token;
  await user.save();
  user.password = undefined;
  res.status(statusCode).json({
    status: "success",
    user,
  });
});

exports.signup = asyncHandler(async (req, res) => {
  const user = await User.findOne({ email: req.body.email });
  if (user) {
    return res.status(400).json({
      status: "fail",
      message: "User already registered",
    });
  }
  const { name, email, password } = req.body;
  if (!name || !email || !password) {
    return res.status(400).json({
      status: "fail",
      message: "Please fill all the information",
    });
  }
  const newUser = await User.create({
    name,
    email,
    password,
  });
  newUser.password = await bcrypt.hash(req.body.password, 12);
  await newUser.save();

  const otpValue = generateOtp.generateOtp(4);

  let otp = await Otp.findOne({ otpFor: email });

  if (!otp) {
    otp = new Otp({
      otpValue: otpValue,
      otpExpiration: 5,
      otpFor: email,
    });
    await otp.save();
  } else {
    otp.otpValue = otpValue;
    otp.otpExpiration = 5;
    await otp.save();
  }

  const msg = {
    to: email,
    from: "ratofyio@gmail.com",
    subject: "Testing Email from Ratofy",
    text: "OTP will be sent in this email",
    html: `<strong>You OTP is ${otpValue}</strong>`,
  };

  await sendEmail(msg);

  createSendToken(newUser, 201, res);
});

exports.signin = asyncHandler(async (req, res) => {
  const { email, password } = req.body;
  const user = await User.findOne({ email });
  if (!user || !user.isEmailVerified) {
    return res.status(400).json({
      status: "fail",
      message: "Please verify your email first",
    });
  }
  if (!user || !(await user.correctPassword(password, user.password))) {
    return res.status(400).json({
      status: "fail",
      message: "incorrect username or password",
    });
  }
  createSendToken(user, 200, res);
});

exports.forgetPassword = asyncHandler(async (req, res) => {
  const email = req.body.email;
  const user = await User.findOne({ email });
  if (!user) {
    return res.status(400).json({
      status: "fail",
      message: "No such user exists with this email id",
    });
  }

  const otpValue = generateOtp.generateOtp(4);

  let otp = await Otp.findOne({ otpFor: email });

  if (!otp) {
    otp = new Otp({
      otpValue: otpValue,
      otpExpiration: 5,
      otpFor: email,
    });
    await otp.save();
  } else {
    otp.otpValue = otpValue;
    otp.otpExpiration = 5;
    await otp.save();
  }

  const msg = {
    to: email,
    from: "ratofyio@gmail.com",
    subject: "OTP for Reset Password from Ratofy",
    text: "Here is your OTP for resetting password of your Ratofy Account",
    html: `<strong>You OTP is ${otpValue}</strong>`,
  };

  await sendEmail(msg);

  return res.status(200).json({
    status: "success",
    message: "OTP has been sent to your email address",
  });
});

exports.setNewPassword = asyncHandler(async (req, res) => {
  const { email, password } = req.body;
  const user = await User.findOne({ email });
  console.log(user);
  if (!user) {
    return res.status(200).json({
      status: "fail",
      message: "No user exists with that email address",
    });
  }

  user.password = await bcrypt.hash(password, 12);
  await user.save();
  return res.status(200).json({
    status: "success",
    message: "Password reset successful",
  });
});

exports.isAuthenticated = asyncHandler(async (req, res, next) => {
  const authHeader = req.headers["authorization"];
  const token = authHeader && authHeader.split(" ")[1];
  if (!token) {
    return res.status(400).json({
      status: "fail",
      message: "User not authenticated",
    });
  }
  const decoded = await promisify(jwt.verify)(token, process.env.JWT_SECRET);
  const currentUser = await User.findById(decoded.id);
  if (!currentUser) {
    return res.status(400).json({
      status: "fail",
      message: "The user belonging to this token does no longer exist.",
    });
  }
  req.user = currentUser;
  next();
});

exports.verifyOTP = asyncHandler(async (req, res) => {
  const email = req.body.email;
  const otpValue = req.body.otp;

  let otp = await Otp.findOne({ otpFor: email, otpValue: otpValue });

  if (!otp) {
    return res.status(400).json({
      status: "fail",
      message: "No otp found",
    });
  }

  let updatedAt = new Date(otp.updatedAt);
  let expiresIn = new Date(
    updatedAt.setMinutes(updatedAt.getMinutes() + otp.otpExpiration)
  );

  if (new Date() < expiresIn) {
    let user = await User.findOne({ email });
    user.isEmailVerified = true;
    await user.save();
    await otp.remove();

    return res.status(200).json({
      status: "success",
      message: "OTP verification successful",
      user,
    });
  } else {
    return res.status(400).json({
      status: "fail",
      message: "OTP has been expired",
    });
  }
});
