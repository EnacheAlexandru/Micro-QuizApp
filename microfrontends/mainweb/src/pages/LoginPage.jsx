import React, { useState, useEffect } from "react";
import useStore from "sideweb/store";
import { ProgressBar } from "react-loader-spinner";
import LoginHeader from "../components/LoginHeader";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import webSocketManager from "../utils/WebSocketManager";

const LoginPage = () => {
  const INVALID_AUTH_ERROR_MSG = "Invalid credentials";
  const INVALID_REGISTER_ERROR_MSG = "Invalid input or username taken";
  const SUCCESS_REGISTER_MSG = "Register successful";

  const navigateTo = useNavigate();

  const {
    token: storeToken,
    setToken: setStoreToken,
    setUsername: setStoreUsername,
    setSelectedPage: setStoreSelectedPage,
  } = useStore();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [isLoading, setIsLoading] = useState(false);
  const [isInvalidAuth, setIsInvalidAuth] = useState(false);
  const [isInvalidRegister, setIsInvalidRegister] = useState(false);
  const [isSuccessRegister, setIsSuccessRegister] = useState(false);

  const handleLogin = async () => {
    setIsLoading(true);
    await new Promise((resolve) => setTimeout(resolve, 1000));
    await axios
      .post("http://localhost:8080/login", {
        username: username,
        password: password,
      })
      .then((response) => {
        // check useEffect on storeToken
        setStoreUsername(username);
        setStoreToken(response.data.message);
        setStoreSelectedPage(2);
      })
      .catch((error) => {
        console.log(error);
        setIsInvalidAuth(true);
        setIsInvalidRegister(false);
        setIsSuccessRegister(false);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleRegister = async () => {
    setIsLoading(true);
    await new Promise((resolve) => setTimeout(resolve, 1000));
    await axios
      .post("http://localhost:8080/register", {
        username: username,
        password: password,
      })
      .then(() => {
        console.log(`Successful register for ${username}`);
        setIsInvalidAuth(false);
        setIsInvalidRegister(false);
        setIsSuccessRegister(true);
      })
      .catch((error) => {
        console.log(error);
        setIsInvalidAuth(false);
        setIsInvalidRegister(true);
        setIsSuccessRegister(false);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleOnEnter = (event) => {
    if (event.keyCode === 13) {
      handleLogin();
    }
  };

  useEffect(() => {
    webSocketManager.disconnect();
  }, [])

  useEffect(() => {
    // we need to make sure that the token is loaded before going to the next page
    if (storeToken !== "") {
      console.log(`Token set for ${username}`);
      navigateTo("/other");
    }
  }, [storeToken]);

  return (
    <div>
      <LoginHeader />

      <div className="m-5">
        <div className="text-2xl font-bold">Username</div>
        <div>Between 3-16 alphanumeric or underscore</div>
      </div>
      <div className="ml-5">
        <input
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          onKeyDownCapture={handleOnEnter}
          className="border border-gray-500 p-1.5 rounded-lg"
        ></input>
      </div>

      <div className="m-5">
        <div className="text-2xl font-bold">Password</div>
        <div>Minimum of 4 alphanumeric or punctuation</div>
      </div>
      <div className="ml-5">
        <input
          // type="password"
          type="input"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          onKeyDownCapture={handleOnEnter}
          className="border border-gray-500 p-1.5 rounded-lg"
        ></input>
      </div>

      <div className="m-5 mt-5 flex">
        <div>
          <button
            onClick={handleLogin}
            className="text-white bg-blue-500 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
          >
            Login
          </button>
        </div>
        <div className="ml-10">
          <button
            onClick={handleRegister}
            className="text-white bg-green-500 hover:bg-green-800 focus:ring-4 focus:outline-none focus:ring-green-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center dark:bg-green-600 dark:hover:bg-green-700 dark:focus:ring-green-800"
          >
            Register
          </button>
        </div>
      </div>

      {isInvalidAuth ? (
        <div className="ml-5 my-1 text-red-600">{INVALID_AUTH_ERROR_MSG}</div>
      ) : (
        <div></div>
      )}

      {isInvalidRegister ? (
        <div className="ml-5 my-1 text-red-600">{INVALID_REGISTER_ERROR_MSG}</div>
      ) : (
        <div></div>
      )}

      {isSuccessRegister ? (
        <div className="ml-5 my-1 text-green-600">{SUCCESS_REGISTER_MSG}</div>
      ) : (
        <div></div>
      )}

      {isLoading ? (
        <div className="ml-5">
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
  );
};

export default LoginPage;
