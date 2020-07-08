// eslint-disable-next-line @typescript-eslint/no-var-requires
const path = require("path");
// eslint-disable-next-line @typescript-eslint/no-var-requires
const HtmlWebpackPlugin = require("html-webpack-plugin");

module.exports = {
  mode: "development",
  entry: "./src/index.tsx",
  output: {
    path: path.join(__dirname, "/dist"),
    publicPath: "/",
    filename: "[hash].js",
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: "src/html/index.html",
    }),
  ],
  resolve: {
    extensions: [".ts", ".tsx", ".js", ".jsx"],
    modules: [path.join(__dirname, "src"), path.join(__dirname, "node_modules")],
  },
  module: {
    rules: [
      // eslint-loader
      {
        test: /\.(ts|tsx)$/,
        enforce: "pre",
        exclude: /node_modules/,
        use: [
          {
            loader: "eslint-loader",
            options: {
              fix: false,
              failOnError: true,
            },
          },
        ],
      },

      // loaders for .ts or .tsx
      {
        test: /\.(ts|tsx)$/,
        exclude: /node_modules/,
        use: [
          // babel-loader for react
          // {
          //   loader: "babel-loader",
          //   options: {
          //     presets: [
          //       ["@babel/preset-react"],
          //       ["@babel/preset-env", { useBuiltIns: "usage", targets: ">0.25%", corejs: 3 }],

          //     ]
          //   }
          // },
          // ts-loader for typescript
          {
            loader: "ts-loader",
            options: {
              configFile: "tsconfig.json",
              transpileOnly: true,
              experimentalWatchApi: true,
            },
          },
        ],
      },

      // for html
      {
        test: /\.html$/,
        loader: "html-loader",
      },
    ],
  },
};
