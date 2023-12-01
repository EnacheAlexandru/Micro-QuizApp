import React from "react";
import ReactDOM from "react-dom";

import "./index.scss";
import LoginPage from "./page/LoginPage";

const App = () => {
  return (
    <LoginPage />
  )
};

ReactDOM.render(<App />, document.getElementById("app"));