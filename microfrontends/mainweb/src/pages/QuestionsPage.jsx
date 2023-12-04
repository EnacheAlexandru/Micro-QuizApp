import React, { useEffect, useState } from "react";
import Header from "../components/Header";
import { ProgressBar } from "react-loader-spinner";
import useStore from "sideweb/store";
import axios from "axios";
import DateFormatter from "../utils/DateFormatter";
import webSocketManager from "../utils/WebSocketManager";

const NotAnsweredPage = () => {
  const { token: storeToken } = useStore();

  const FETCH_LIST_ERROR_MSG = "Error processing or session expired";
  const [isErrorFetch, setIsErrorFetch] = useState(false);

  const [isLoading, setIsLoading] = useState(true);

  const [questionsList, setQuestionsList] = useState([]);

  // for dynamic generation
  const [answeredMap, setAnsweredMap] = useState({});
  const [optionMap, setOptionMap] = useState({});

  useEffect(() => {
    handleGetQuestions();
    webSocketManager.connect(storeToken);
  }, []);

  const updateSetAnsweredMap = (key, value) => {
    setAnsweredMap((prev) => ({
      ...prev,
      [key]: value,
    }));
  };

  const updateSetOptionMap = (key, value) => {
    setOptionMap((prev) => ({
      ...prev,
      [key]: value,
    }));
  };

  useEffect(() => {
    if (questionsList.length > 0) {
      questionsList.forEach((item) => {
        updateSetAnsweredMap(item.id, "");
        updateSetOptionMap(item.id, "");
      });
    }
  }, [questionsList]);

  const handleGetQuestions = async () => {
    setIsLoading(true);
    await new Promise((resolve) => setTimeout(resolve, 100));
    await axios
      .get("http://localhost:8080/question/other", {
        headers: { Authorization: `Bearer ${storeToken}` },
      })
      .then((response) => {
        console.log(response);
        setIsErrorFetch(false);
        setQuestionsList(response.data);
      })
      .catch((error) => {
        console.log(error);
        setIsErrorFetch(true);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleAnswer = async (questionId) => {
    setIsLoading(true);
    await new Promise((resolve) => setTimeout(resolve, 100));
    await axios
      .post(
        `http://localhost:8080/question/answer`,
        {
          id: questionId,
          answer: optionMap[questionId],
        },
        {
          headers: { Authorization: `Bearer ${storeToken}` },
        }
      )
      .then((response) => {
        console.log(response);
        setIsErrorFetch(false);
        updateSetAnsweredMap(questionId, response.data.message);
      })
      .catch((error) => {
        console.log(error);
        setIsErrorFetch(true);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  return (
    <div>
      <Header />
      <div className="m-5 flex justify-center">
        <button
          onClick={handleGetQuestions}
          className="text-white bg-blue-500 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
        >
          Refresh
        </button>
      </div>
      {isLoading ? (
        <div className="m-5 flex justify-center">
          <ProgressBar
            height="80"
            width="80"
            ariaLabel="progress-bar-loading"
            wrapperClass="progress-bar-wrapper"
            borderColor="#1D4ED8"
            barColor="#51E5FF"
          />
        </div>
      ) : isErrorFetch ? (
        <div className="m-5 text-red-600 flex justify-center">
          {FETCH_LIST_ERROR_MSG}
        </div>
      ) : questionsList.length > 0 ? (
        <div className="m-5 flex flex-wrap justify-evenly gap-4 items-center">
          {questionsList.map((item) => (
            <React.Fragment key={item.id}>
              <div className="block w-96 max-w-sm p-6 bg-white border border-gray-200 rounded-lg shadow">
                <div className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">
                  {item.title}
                </div>
                <div
                  className="font-normal text-gray-700 dark:text-gray-400"
                  style={{ fontSize: "13px" }}
                >
                  By: {item.username}
                </div>
                <div
                  className="font-normal mb-1 text-gray-700 dark:text-gray-400"
                  style={{ fontSize: "13px" }}
                >
                  Created on: {DateFormatter.format(item.creation)}
                </div>
                <div className="text-gray-700 dark:text-gray-400 font-bold flex">
                  {answeredMap[item.id] !== "" ? (
                    <div></div>
                  ) : (
                    <input
                      type="radio"
                      value={item.option0}
                      name={item.id}
                      onChange={(e) =>
                        updateSetOptionMap(item.id, e.target.value)
                      }
                    />
                  )}
                  <div
                    className={`ml-1 pl-1 rounded-lg flex-1 ${
                      answeredMap[item.id] === item.option0
                        ? "bg-green-300"
                        : answeredMap[item.id] !== "" &&
                          optionMap[item.id] === item.option0
                        ? "bg-red-300"
                        : ""
                    }`}
                  >
                    {item.option0}
                  </div>
                </div>
                <div className="text-gray-700 dark:text-gray-400 font-bold flex">
                  {answeredMap[item.id] !== "" ? (
                    <div></div>
                  ) : (
                    <input
                      type="radio"
                      value={item.option1}
                      name={item.id}
                      onChange={(e) =>
                        updateSetOptionMap(item.id, e.target.value)
                      }
                    />
                  )}
                  <div
                    className={`ml-1 pl-1 mt-1 rounded-lg flex-1 ${
                      answeredMap[item.id] === item.option1
                        ? "bg-green-300"
                        : answeredMap[item.id] !== "" &&
                          optionMap[item.id] === item.option1
                        ? "bg-red-300"
                        : ""
                    }`}
                  >
                    {item.option1}
                  </div>
                </div>
                <div className="text-gray-700 dark:text-gray-400 font-bold flex">
                  {answeredMap[item.id] !== "" ? (
                    <div></div>
                  ) : (
                    <input
                      type="radio"
                      value={item.option2}
                      name={item.id}
                      onChange={(e) =>
                        updateSetOptionMap(item.id, e.target.value)
                      }
                    />
                  )}
                  <div
                    className={`ml-1 pl-1 mt-1 rounded-lg flex-1 ${
                      answeredMap[item.id] === item.option2
                        ? "bg-green-300"
                        : answeredMap[item.id] !== "" &&
                          optionMap[item.id] === item.option2
                        ? "bg-red-300"
                        : ""
                    }`}
                  >
                    {item.option2}
                  </div>
                </div>
                <div className="text-gray-700 dark:text-gray-400 font-bold flex">
                  {answeredMap[item.id] !== "" ? (
                    <div></div>
                  ) : (
                    <input
                      type="radio"
                      value={item.option3}
                      name={item.id}
                      onChange={(e) =>
                        updateSetOptionMap(item.id, e.target.value)
                      }
                    />
                  )}
                  <div
                    className={`ml-1 pl-1 mt-1 rounded-lg flex-1 ${
                      answeredMap[item.id] === item.option3
                        ? "bg-green-300"
                        : answeredMap[item.id] !== "" &&
                          optionMap[item.id] === item.option3
                        ? "bg-red-300"
                        : ""
                    }`}
                  >
                    {item.option3}
                  </div>
                </div>
                {answeredMap[item.id] !== "" ? (
                  <div></div>
                ) : (
                  <div className="mt-2 flex">
                    <button
                      disabled={optionMap[item.id] === ""}
                      onClick={() => handleAnswer(item.id)}
                      className="text-white bg-blue-500 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center"
                    >
                      Answer
                    </button>
                  </div>
                )}
              </div>
            </React.Fragment>
          ))}
        </div>
      ) : (
        <div className="font-bold text-2xl flex justify-center">
          You answered all the questions!
        </div>
      )}
    </div>
  );
};

export default NotAnsweredPage;
