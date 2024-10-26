const mongoose = require("mongoose");

const weatherSchema = new mongoose.Schema(
  {
    temperature: { type: Number },
    humidity: { type: Number },
    carbon: { type: Number },
    hydrogen: { type: Number },
    smoke: { type: Number },
    gasLeak: { type: Number },
  },
  { timestamps: true }
);

// Define an outer schema with an array of `weatherSchema`
const weatherDataSchema = new mongoose.Schema({
  userMail: { type: String },
  weatherRecords: [weatherSchema],
});

// Create a model for the outer schema
const WeatherData = mongoose.model("WeatherData", weatherDataSchema);

module.exports = WeatherData;
