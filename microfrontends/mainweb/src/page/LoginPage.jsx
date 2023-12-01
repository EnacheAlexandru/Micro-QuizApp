import React, { useState } from "react";
import useStore from "sideweb/store";
import ApiService from "../ApiService";
import { ProgressBar } from "react-loader-spinner";

import "../index.scss";

const LoginPage = () => {
  const INVALID_AUTH_ERROR_MSG = "Invalid credentials";

  const { token, setToken } = useStore();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [isLoading, setIsLoading] = useState(false);
  const [isInvalidAuth, setIsInvalidAuth] = useState(false);

  const loginHandle = async () => {
    setIsLoading(true);
    await new Promise((resolve) => setTimeout(resolve, 1000));
    ApiService.post("/login", { username: username, password: password })
      .then((response) => {
        setToken(response.message);
        console.log(`Token set for ${username}`)
      })
      .catch(() => {
        setIsInvalidAuth(true);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  return (
    <div>
      <div className="text-7xl m-5">QuizApp</div>

      <div className="text-2xl m-5">Username</div>

      <div className="ml-5">
        <input
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          className="border border-gray-500 p-1.5"
        ></input>
      </div>

      <div className="text-2xl m-5">Password</div>
      <div className="ml-5">
        <input
          type="text"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          className="border border-gray-500 p-1.5"
        ></input>
      </div>

      {isInvalidAuth ? (
        <div className="m-5 text-red-600">{INVALID_AUTH_ERROR_MSG}</div>
      ) : (
        <div className="m-5"></div>
      )}

      <div className="m-5 mt-5">
        <div>
          <button
            onClick={loginHandle}
            className="text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
          >
            Login
          </button>
        </div>

        {isLoading ? (
          <div>
            <ProgressBar
              height="80"
              width="80"
              ariaLabel="progress-bar-loading"
              wrapperClass="progress-bar-wrapper"
              borderColor="#1D4ED8"
              barColor="#51E5FF"
            />
          </div>
        ) : (
          <div></div>
        )}
      </div>
    </div>
  );
};

export default LoginPage;
