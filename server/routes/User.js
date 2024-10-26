const router = require("express").Router();
const bcrypt = require("bcrypt");
const User = require("../models/User");
const saltRounds = 10;
const weatherData = require("../models/weather");

async function hashPass(password) {
  const salt = await bcrypt.genSalt(saltRounds);
  return await bcrypt.hash(password, salt);
}

router.post("/sign-up", async (req, res) => {
  try {
    const { name, email, password } = req.body;

    // Check if the user already exists
    const existingUser = await User.findOne({ email });
    if (existingUser) {
      return res
        .status(400)
        .json({ msg: "User with this email already exists" });
    }

    // Hash the password
    const hashedPassword = await hashPass(password);

    // Create the new user
    const newUser = await User.create({
      displayName:name,
      email,
      password: hashedPassword,
    });
    const weatherInstance = await weatherData.create({
      userMail: email,
    });
    weatherInstance.save();
    res.status(201).json({ msg: "User created successfully", user: newUser });
  } catch (error) {
    res.status(500).json({ msg: "Error creating user", error: error.message });
  }
});

router.post("/log-in", async (req, res) => {
  try {
    const { email, password } = req.body;

    // Find the user by email
    const user = await User.findOne({ email });
    if (!user) {
      return res
        .status(400)
        .json({ msg: "User with this email does not exist" });
    }

    // Compare the provided password with the stored hashed password
    const isPasswordValid = await bcrypt.compare(password, user.password);
    if (!isPasswordValid) {
      return res.status(400).json({ msg: "Invalid password" });
    }

    // If successful, respond with a success message (you could also return a token here for authentication purposes)
    res.status(200).json({
      msg: "Login successful",
      user: { id: user._id, name: user.name, email: user.email },
      isAuthenticated:true
    });
  } catch (error) {
    res.status(500).json({ msg: "Error logging in", error: error.message });
  }
});


module.exports = router;
