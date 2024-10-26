const express = require("express");
const app = express();
const mongoose = require("mongoose");
require("dotenv").config();
// Use JSON parser middleware
app.use(express.json());
// Mongoose Connect
mongoose
  .connect(process.env.MONGODB_URI, {
    useNewUrlParser: true, 
    useUnifiedTopology: true,
  })
  .then(() => console.log("Mongo DB connected"))
  .catch((err) => {
    console.log(`Error Occurred : ${err}`);
  });

const weatherRouter = require("./routes/weatherData");
app.use("/weather", weatherRouter);
const authRouter = require("./routes/User");
app.use("/auth", authRouter);

app.get("/", (req, res) => res.send("Server is running"));

app.listen(process.env.PORT, () => {
  console.log(`Server is running on port ${process.env.PORT}`);
});
