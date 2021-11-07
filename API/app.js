const express = require("express");
const morgan = require("morgan");
const cors = require("cors");

const app = express();

const authRouter = require("./routes/auth.routes");
const predictionRouter = require("./routes/prediction.routes");

app.use(cors());
app.set("trust proxy", true);
app.use(express.json({ limit: "30mb" }));
app.use(express.urlencoded({ extended: true }));

app.use(function (req, res, next) {
  res.header("Access-Control-Allow-Origin", "*");
  res.header(
    "Access-Control-Allow-Headers",
    "Origin, X-Requested-With, Content-Type, Accept"
  );
  next();
});

app.use(morgan("tiny"));

if (process.env.NODE_ENV === "development") {
  app.use(morgan("dev"));
}

app.get("/", (req, res) => {
  res.send("Welcome to the Cought-It API");
});

app.use("/api/auth", authRouter);
app.use("/api/prediction", predictionRouter);

module.exports = app;
