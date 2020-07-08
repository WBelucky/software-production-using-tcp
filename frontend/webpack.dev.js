const { merge } = require("webpack-merge");
const common = require("./webpack.config.js");
const Dotenv = require("dotenv-webpack");

module.exports = merge(common, {
  devtool: "source-map",
  cache: true,
  devServer: {
    historyApiFallback: true,
  },
  plugins: [new Dotenv({ path: ".env" })],
});
