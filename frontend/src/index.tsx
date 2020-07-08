import React from "react";
import ReactDOM from "react-dom";
import { createBrowserHistory } from "history";

import { Router } from "react-router-dom";
import MainContentRouter from "./components/MainContentRouter";

const history = createBrowserHistory();

function App() {
  return (
    <Router history={history}>
      <MainContentRouter />
    </Router>
  );
}

ReactDOM.render(<App />, document.getElementById("app"));
