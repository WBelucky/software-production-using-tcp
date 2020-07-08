import React from "react";
import { Switch, Route } from "react-router";
import Home from "./home/Home";

const MainContentRouter: React.FC = () => {
  return (
    <Switch>
      <Route exact path="/" component={Home} />
    </Switch>
  );
};

export default MainContentRouter;
