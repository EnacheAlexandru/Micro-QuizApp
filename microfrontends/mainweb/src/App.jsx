import React from "react";
import ReactDOM from "react-dom";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import "./index.scss";
import LoginPage from "./page/LoginPage";
import QuestionsPage from "./page/QuestionsPage";
import NotFoundPage from "./page/NotFoundPage";
import AnsweredPage from "./page/AnsweredPage";

const App = () => {
  return (
    <Router>
        <Routes>
          <Route exact path="/" element={<LoginPage />}></Route>
          {/* <Route exact path="/user" element={<MyQuestionsPage />}></Route> */}
          <Route exact path="/other/answer" element={<AnsweredPage />}></Route>
          <Route exact path="/other" element={<QuestionsPage />}></Route>
          {/* <Route exact path="/leaderboard" element={<LeaderboardPage />}></Route> */}
          <Route exact path="*" element={<NotFoundPage />}></Route> 
        </Routes>
      </Router>
  )
};

ReactDOM.render(<App />, document.getElementById("app"));