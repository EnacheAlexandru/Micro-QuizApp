import React from "react";
import ReactDOM from "react-dom";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import "./index.scss";
import LoginPage from "./pages/LoginPage";
import QuestionsPage from "./pages/QuestionsPage";
import NotFoundPage from "./pages/NotFoundPage";
import AnsweredPage from "./pages/AnsweredPage";
import LeaderboardPage from "./pages/LeaderboardPage";

const App = () => {
  return (
    <Router>
        <Routes>
          <Route exact path="/" element={<LoginPage />}></Route>
          {/* <Route exact path="/user" element={<MyQuestionsPage />}></Route> */}
          <Route exact path="/other/answer" element={<AnsweredPage />}></Route>
          <Route exact path="/other" element={<QuestionsPage />}></Route>
          <Route exact path="/leaderboard" element={<LeaderboardPage />}></Route>
          <Route exact path="*" element={<NotFoundPage />}></Route> 
        </Routes>
      </Router>
  )
};

ReactDOM.render(<App />, document.getElementById("app"));