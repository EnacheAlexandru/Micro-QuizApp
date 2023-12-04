import React from "react";
import ReactDOM from "react-dom";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import QuestionsPage from "./pages/QuestionsPage";
import NotFoundPage from "./pages/NotFoundPage";
import AnsweredPage from "./pages/AnsweredPage";
import LeaderboardPage from "./pages/LeaderboardPage";
import MyQuestionsPage from "./pages/MyQuestionsPage";
import MyQuestionDetailsPage from "./pages/MyQuestionDetailsPage";
import { ToastContainer } from "react-toastify";

import 'react-toastify/dist/ReactToastify.css';
import "./index.scss";

const App = () => {
  return (
    <div>
      <ToastContainer />
      <Router>
        <Routes>
          <Route exact path="/" element={<LoginPage />}></Route>
          <Route exact path="/user" element={<MyQuestionsPage />}></Route>
          <Route exact path="/user/:questionId" element={<MyQuestionDetailsPage />}></Route>
          <Route exact path="/other/answer" element={<AnsweredPage />}></Route>
          <Route exact path="/other" element={<QuestionsPage />}></Route>
          <Route exact path="/leaderboard" element={<LeaderboardPage />}></Route>
          <Route exact path="*" element={<NotFoundPage />}></Route> 
        </Routes>
      </Router>
    </div>
  )
};

ReactDOM.render(<App />, document.getElementById("app"));