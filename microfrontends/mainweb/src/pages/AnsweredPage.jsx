import React, { useEffect, useState } from "react";
import Header from "../components/Header";
import { ProgressBar } from "react-loader-spinner";
import useStore from "sideweb/store";
import axios from "axios";
import DateFormatter from "../utils/DateFormatter";
import webSocketManager from "../utils/WebSocketManager";
import { useNavigate } from "react-router-dom";

const AnsweredPage = () => {
  const { token: storeToken } = useStore();
  const navigateTo = useNavigate();

  const FETCH_LIST_ERROR_MSG = "Error fetching list or session expired";
  const [isErrorFetch, setIsErrorFetch] = useState(false);

  const [isLoading, setIsLoading] = useState(true);

  const [answeredList, setAnsweredList] = useState([])

  useEffect(() => {
    if (storeToken === undefined || storeToken === null || storeToken === '') {
      navigateTo('/')
    }
    handleGetAnswered();
    webSocketManager.connect(storeToken);
  }, []);

  const handleGetAnswered = async () => {
    setIsLoading(true);
    await new Promise((resolve) => setTimeout(resolve, 100));
    await axios
      .get("http://localhost:8080/question/other/answer", {
        headers: { Authorization: `Bearer ${storeToken}` },
      })
      .then((response) => {
        console.log(response);
        setIsErrorFetch(false);
        setAnsweredList(response.data)
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
          onClick={handleGetAnswered}
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
      ) : answeredList.length > 0 ? (
        <div className="m-5 flex flex-wrap justify-evenly gap-4">
          {answeredList.map((item) => (
            <React.Fragment key={item.id}>
                <div className="block w-96 max-w-sm p-6 bg-white border border-gray-200 rounded-lg shadow">
                    <div className="mb-2 text-2xl font-bold tracking-tight text-gray-900 dark:text-white">{item.title}</div>
                    {item.status === 'ACTIVE' ? <div></div> : <div className="font-bold text-red-500" style={{fontSize: '13px'}}>{item.status}</div>}
                    <div className="font-normal text-gray-700 dark:text-gray-400" style={{fontSize: '13px'}}>By: {item.username}</div>
                    <div className="font-normal text-gray-700 dark:text-gray-400" style={{fontSize: '13px'}}>Created on: {DateFormatter.format(item.questionCreation)}</div>
                    <div className="font-normal mb-1 text-gray-700 dark:text-gray-400" style={{fontSize: '13px'}}>Answered on: {DateFormatter.format(item.answerCreation)}</div>
                    <div className="font-bold rounded-lg pl-1 bg-green-300 dark:text-gray-400">{item.correct}</div>
                    <div className={`font-bold rounded-lg pl-1 mt-1 ${item.option === 1 ? 'bg-red-300' : ''} text-gray-700 dark:text-gray-400`}>{item.wrong1}</div>
                    <div className={`font-bold rounded-lg pl-1 mt-1 ${item.option === 2 ? 'bg-red-300' : ''} text-gray-700 dark:text-gray-400`}>{item.wrong2}</div>
                    <div className={`font-bold rounded-lg pl-1 mt-1 ${item.option === 3 ? 'bg-red-300' : ''} text-gray-700 dark:text-gray-400`}>{item.wrong3}</div>
                </div>
            </React.Fragment>
          ))}
        </div>
        ) : (
          <div className="font-bold text-2xl flex justify-center">You did not answer any questions!</div>
        )
      }
    </div>
  );
};

export default AnsweredPage;
