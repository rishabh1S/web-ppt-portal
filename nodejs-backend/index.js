import express from "express";
import convertRouter from "./routes/convert.js";

const app = express();
const port = process.env.PORT || 3000;

app.use("/convert", convertRouter);

app.listen(port, () => {
  console.log(`Server is running on http://localhost:${port}`);
});
