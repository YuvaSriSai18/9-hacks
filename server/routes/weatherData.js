const router = require("express").Router();
const WeatherData = require("../models/weather");
const User = require("../models/User");

// Helper function to determine humidity comfort
function humidityComfort(rh, temperature) {
  if (temperature < 20) {
    if (rh < 30) return "Air is too dry";
    if (rh <= 50) return "Comfortable";
    return "Too humid";
  } else {
    if (rh <= 40) return "Comfortable";
    if (rh <= 60) return "A bit humid";
    if (rh <= 70) return "Very humid";
    return "Uncomfortable";
  }
}

// Route to fetch the last weather data record for a user
router.get("/api/data", async (req, res) => {
  const {email}  = req.body;

  if (!email) {
    return res.status(400).json({ msg: "Email is required to fetch data" });
  }

  try {
    const dataUser = await WeatherData.findOne({ userMail: email });
    if (!dataUser || dataUser.weatherRecords.length === 0) {
      return res
        .status(404)
        .json({ msg: "No weather data found for this user" });
    }

    const userLastWeatherData =
      dataUser.weatherRecords[dataUser.weatherRecords.length - 1];
    res.status(200).json({ lastWeatherData: userLastWeatherData });
  } catch (error) {
    console.error("Error fetching weather data:", error);
    res
      .status(500)
      .json({ msg: "Error fetching weather data", error: error.message });
  }
});

// Route to post new weather data for a user
router.post("/post-data", async (req, res) => {
  console.log("Received sensor data:", req.body);
  const { smoke, hydrogen, carbon, gasLeak, temperature, humidity, email } =
    req.body;

  if (!email) {
    return res.status(400).json({ msg: "Email is required to post data" });
  }

  try {
    // Check if a record with this email already exists
    let weatherDataUser = await WeatherData.findOne({ userMail: email });

    if (weatherDataUser) {
      // If user exists, add new weather data to the weatherRecords array
      weatherDataUser.weatherRecords.push({
        temperature,
        humidity,
        carbon,
        hydrogen,
        smoke,
        gasLeak,
      });
      await weatherDataUser.save();
    } else {
      // If user does not exist, create a new document with this weather data
      weatherDataUser = new WeatherData({
        userMail: email,
        weatherRecords: [
          {
            temperature,
            humidity,
            carbon,
            hydrogen,
            smoke,
            gasLeak,
          },
        ],
      });
      await weatherDataUser.save();
    }

    res
      .status(201)
      .json({ msg: "Weather data added successfully", data: weatherDataUser });
  } catch (error) {
    console.error("Error adding weather data:", error);
    res
      .status(500)
      .json({ msg: "Error adding weather data", error: error.message });
  }
});

module.exports = router;
