import React, { useEffect } from "react";
import LoginHeader from "../components/LoginHeader";
import { useNavigate } from "react-router-dom";
import useStore from "sideweb/store";
import webSocketManager from "../utils/WebSocketManager";

const NotFoundPage = () => {
  const {
    setUsername: setStoreUsername,
    setToken: setStoreToken,
    setSelectedPage: setStoreSelectedPage,
  } = useStore();

  const navigateTo = useNavigate();

  const handleLogin = () => {
    setStoreSelectedPage(2);
    setStoreUsername("");
    setStoreToken("");
    navigateTo("/");
  };

  useEffect(() => {
    webSocketManager.disconnect();
  }, []);

  return (
    <div>
      <LoginHeader />
      <div className="m-5 flex text-5xl font-bold">Page 404</div>
      <div className="m-5">
        <button
          onClick={handleLogin}
          className="text-white bg-blue-500 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
        >
          To Login
        </button>
      </div>
    </div>
  );
};

export default NotFoundPage;
