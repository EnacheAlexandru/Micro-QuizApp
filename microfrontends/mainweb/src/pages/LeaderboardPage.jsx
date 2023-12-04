import React, { useEffect, useState } from "react";
import Header from "../components/Header";
import { ProgressBar } from "react-loader-spinner";
import useStore from "sideweb/store";
import axios from "axios";
import webSocketManager from "../utils/WebSocketManager";

const LeaderboardPage = () => {
  const { token: storeToken, username: storeUsername } = useStore();

  const FETCH_LIST_ERROR_MSG = "Error fetching list or session expired";
  const [isErrorFetch, setIsErrorFetch] = useState(false);

  const [isLoading, setIsLoading] = useState(true);

  const [leaderboard, setLeaderboard] = useState([]);

  let position = 0;

  useEffect(() => {
    handleGetLeaderboard(1);
    webSocketManager.connect(storeToken);
  }, []);

  const handleGetLeaderboard = async (page) => {
    setIsLoading(true);
    await new Promise((resolve) => setTimeout(resolve, 100));
    await axios
      .get(`http://localhost:8080/leaderboard/list?page=${page}`, {
        headers: { Authorization: `Bearer ${storeToken}` },
      })
      .then((response) => {
        console.log(response);
        setIsErrorFetch(false);
        setLeaderboard(response.data);
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
          onClick={() => handleGetLeaderboard(1)}
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
      ) : leaderboard.players.length > 0 ? (
        <div>
          <div className="m-5 flex flex-col items-center gap-2">
            {leaderboard.players.map((item) => {
              position++;
              return (
                <React.Fragment key={item.username}>
                  <div
                    className={`w-3/4 p-3 bg-white border border-gray-200 rounded-lg shadow dark:bg-gray-800 dark:border-gray-700
                      ${
                        leaderboard.page === 1
                          ? position === 1
                            ? "bg-yellow-400"
                            : position === 2
                            ? "bg-gray-400"
                            : position === 3
                            ? "bg-yellow-600"
                            : ""
                          : ""
                      }
                      `}
                  >
                    <div className="flex justify-between">
                      <div className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">{`${
                        position + 10 * (leaderboard.page - 1)
                      }. ${item.username} ${
                        item.username === storeUsername ? "(You)" : ""
                      }`}</div>
                      <div className="text-2xl font-bold tracking-tight text-gray-900 dark:text-white">{`${item.points} / ${item.total}`}</div>
                    </div>
                  </div>
                </React.Fragment>
              );
            })}
          </div>
          <div className="flex justify-center content-center items-center">
            <div className="m-1 flex">
              <button
                onClick={() => {
                  if (leaderboard.page > 1) {
                    handleGetLeaderboard(1);
                  }
                }}
                className="text-white bg-blue-500 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
              >
                {"<<"}
              </button>
            </div>
            <div className="m-1 flex">
              <button
                onClick={() => {
                  if (leaderboard.page > 1) {
                    handleGetLeaderboard(leaderboard.page - 1);
                  }
                }}
                className="text-white bg-blue-500 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
              >
                {"<"}
              </button>
            </div>
            <div className="m-1 flex font-bold">
              {`${leaderboard.page} / ${leaderboard.pages}`}
            </div>
            <div className="m-1 flex">
              <button
                onClick={() => {
                  if (leaderboard.page < leaderboard.pages) {
                    handleGetLeaderboard(leaderboard.page + 1);
                  }
                }}
                className="text-white bg-blue-500 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
              >
                {">"}
              </button>
            </div>
            <div className="m-1 flex">
              <button
                onClick={() => {
                  if (leaderboard.page < leaderboard.pages) {
                    handleGetLeaderboard(leaderboard.pages);
                  }
                }}
                className="text-white bg-blue-500 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm sm:w-auto px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
              >
                {">>"}
              </button>
            </div>
          </div>
        </div>
      ) : (
        <div className="font-bold text-2xl flex justify-center">
          Be the first to answer a question and get to the leaderboard!
        </div>
      )}
    </div>
  );
};

export default LeaderboardPage;
