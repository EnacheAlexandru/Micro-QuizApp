import React from "react";
import useStore from "sideweb/store";
import { useNavigate } from "react-router-dom";

const Header = () => {
  const {
    username: storeUsername,
    setUsername: setStoreUsername,
    setToken: setStoreToken,
    selectedPage: storeSelectedPage,
    setSelectedPage: setStoreSelectedPage,
  } = useStore();
  const navigateTo = useNavigate();

  const handleMyQuestions = () => {
    setStoreSelectedPage(0);
    navigateTo("/user");
  };

  const handleAnswered = () => {
    setStoreSelectedPage(1);
    navigateTo("/other/answer");
  };

  const handleQuestions = () => {
    setStoreSelectedPage(2);
    navigateTo("/other");
  };

  const handleLeaderboard = () => {
    setStoreSelectedPage(3);
    navigateTo("/leaderboard");
  };

  const handleLogout = () => {
    setStoreSelectedPage(2);
    setStoreUsername("");
    setStoreToken("");
    navigateTo("/");
  };

  const selected =
    "text-white bg-green-500 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center dark:bg-green-600 dark:hover:bg-green-700 dark:focus:ring-green-800";
  const notSelected =
    "text-white bg-green-800 hover:bg-green-700 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center dark:bg-green-600 dark:hover:bg-green-700 dark:focus:ring-green-800";

  return (
    <div className="p-3 bg-blue-500 text-white flex">
      <div className="font-bold text-3xl flex-grow">QuizApp</div>
      <div className="flex flex-grow">
        <button
          onClick={handleMyQuestions}
          className={storeSelectedPage === 0 ? selected : notSelected}
        >
          My Questions
        </button>
      </div>
      <div className="flex flex-grow">
        <button
          onClick={handleAnswered}
          className={storeSelectedPage === 1 ? selected : notSelected}
        >
          Answered
        </button>
      </div>
      <div className="flex flex-grow">
        <button
          onClick={handleQuestions}
          className={storeSelectedPage === 2 ? selected : notSelected}
        >
          Questions
        </button>
      </div>
      <div className="flex flex-grow">
        <button
          onClick={handleLeaderboard}
          className={storeSelectedPage === 3 ? selected : notSelected}
        >
          Leaderboard
        </button>
      </div>
      <div className="font-bold text-2xl flex items-center">
        <div className="mr-3">Welcome {storeUsername}!</div>
        <div className="">
          <button
            onClick={handleLogout}
            className="text-white bg-red-500 hover:bg-red-800 focus:ring-4 focus:outline-none focus:ring-red-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center dark:bg-red-600 dark:hover:bg-red-700 dark:focus:ring-red-800"
          >
            Logout
          </button>
        </div>
      </div>
    </div>
  );
};

export default Header;
